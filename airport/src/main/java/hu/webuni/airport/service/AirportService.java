package hu.webuni.airport.service;

import hu.webuni.airport.model.Address;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.HistoryData;
import hu.webuni.airport.model.Image;
import hu.webuni.airport.repository.AirportRepository;
import hu.webuni.airport.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AirportService {

	private static final Logger logger = LoggerFactory.getLogger(AirportService.class);

	private final AirportRepository airportRepository;
	private final ImageRepository imageRepository;
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public Airport save(Airport airport) {
		checkUniqueIata(airport.getIata(), null);
		return airportRepository.save(airport);
	}
	
	@Transactional
	public Airport update(Airport airport) {
		checkUniqueIata(airport.getIata(), airport.getId());
		if(airportRepository.existsById(airport.getId())) {
			return airportRepository.save(airport);
		}
		else
			throw new NoSuchElementException();
	}
	
	private void checkUniqueIata(String iata, Long id) {
		
		boolean forUpdate = id != null;
		Long count = forUpdate ?
				airportRepository.countByIataAndIdNot(iata, id)
				:airportRepository.countByIata(iata);
		
		if(count > 0)
			throw new NonUniqueIataException(iata);
	}
	
	public List<Airport> findAll(){
		return airportRepository.findAll();
	}
	
	public Optional<Airport> findById(long id){
		return airportRepository.findById(id);
	}
	
	@Transactional
	public void delete(long id) {
		airportRepository.deleteById(id);
	}

	// a transactional biztositja hogy ugyanabba a perzisztencia kontextusba tartozzanak
	@Transactional
	@Cacheable("pagedAirportsWithRelationships")
    public List<Airport> findAllWithRelationships(Pageable pageable) {
		// ezek mar lecsatolt entitasok lesznek, sima listaban ha nem lenne a Transactional annotacio
//		List<Airport> airports = airportRepository.findAllWithAddressAndDepartures(pageable);	--> in memory lapozas, minden sor bejon a db-bol
//		airports = airportRepository.findAllWithArrivals(pageable);
//		jobb megoldás, ha van 100 departure és 100 arrival az csak 200 sor jönne vissz a db-ből, folytathatnánk akárhány kapcsolattal
//		ha többet joinolunk akkor jó ha tudjuk hogy nincs sok sor, nem nagy az eredményhalmaz
//		ha nincs @Transactional akkor az első lekérdezés után egyből lecsatolt entitásokat kapnék, csak az arrivals lenne kitöltve
//		az address és departures nem, és dobálná a lazy init exceptionoket

		List<Airport> airports = airportRepository.findAllWithAddress(pageable);
		List<Long> airportIds = airports.stream().map(Airport::getId).toList();
		airports = airportRepository.findByIdWithArrivals(airportIds);
		airports = airportRepository.findByIdWithDepartures(airportIds, pageable.getSort());

		return airports;
	}

//	envers lekerdezes, transactionalnek kell lennie, AuditReaderFactorynak kell Entitymanager
//	egyik problemaja hogy rawtype, ezt a rawtypes szoval, masik hogy typesafety az pedig unchecked
	@Transactional
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List<Airport> getAirportHistory(long id) {

		List resultList = AuditReaderFactory.get(em)
				.createQuery()
//				elso true = csak az entitasokat akarom e, masodik true = torolt sorokat akarom e
//				ha az elso false lenne, nyigna mert az mar nem airport lista lenne hanem Object lista
				.forRevisionsOfEntity(Airport.class, true, true)
				.add(AuditEntity.property("id").eq(id))
				.getResultList();

		return resultList;
	}

//	ez a regi verzio, openapi elotti
////    plus revision infokkal
//	@Transactional
//	@SuppressWarnings({"rawtypes", "unchecked"})
//	public List<HistoryData<Airport>> getAirportHistory2(long id) {
//
//		List resultList = AuditReaderFactory.get(em)
//				.createQuery()
//				.forRevisionsOfEntity(Airport.class, false, true)
//				.add(AuditEntity.property("id").eq(id))
//				.getResultList()
//				.stream()
//				.map(o -> {
//					Object[] objArray = (Object[])o;
//					DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) objArray[1];
//					return new HistoryData<Airport>(
//							(Airport) objArray[0],
//							(RevisionType) objArray[2],
//							revisionEntity.getId(),
//							revisionEntity.getRevisionDate()
//					);
//				}).toList();
//
//		return resultList;
//	}

	//    plus revision infokkal
	@Transactional
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List<HistoryData<Airport>> getAirportHistory2(long id) {

		List resultList = AuditReaderFactory.get(em)
				.createQuery()
				.forRevisionsOfEntity(Airport.class, false, true)
				.add(AuditEntity.property("id").eq(id))
				.getResultList()
				.stream()
				.map(o -> {
					Object[] objArray = (Object[])o;
					DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) objArray[1];
					return new HistoryData<Airport>(
							(Airport) objArray[0],
							(RevisionType) objArray[2],
							revisionEntity.getId(),
							revisionEntity.getRevisionDate()
					);
				}).toList();

		return resultList;
	}

//	regi verzio, openapi elotti
//	//    plus revision kapcsolatokkal
////	itt jelentkezhet az n+1 problema, de feltetelezzuk, hogy egy entitast nem modositanak csillioszor, ezert megoldasnak elfogadjuk ezt is
//	@Transactional
//	@SuppressWarnings({"rawtypes", "unchecked"})
//	public List<HistoryData<Airport>> getAirportHistory3(long id) {
//
//		List resultList = AuditReaderFactory.get(em)
//				.createQuery()
//				.forRevisionsOfEntity(Airport.class, false, true)
//				.add(AuditEntity.property("id").eq(id))
////				ha entitasokon belul revisionoznek ott lehetne Jointype, itt forRevisionsOfEntity itt nem lehet
////				ehelyett lejjebb streamen belul map-nel kenyszeritjuk ki, verziohelyes kapcsolat lesz
////				.traverseRelation("address", JoinType.LEFT)
//				.getResultList()
//				.stream()
//				.map(o -> {
//					Object[] objArray = (Object[])o;
//					DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) objArray[1];
//					Airport airport = (Airport) objArray[0];
//					airport.getAddress().getCity(); // a getAdress meg nem kenyszeriti ki a kapcsolat betolteset, csak a plusz getCity() vagy
//					airport.getArrivals().size();  // size()
//					airport.getDepartures().size();
//
//					return new HistoryData<Airport>(
//							airport,
//							(RevisionType) objArray[2],
//							revisionEntity.getId(),
//							revisionEntity.getRevisionDate()
//					);
//				}).toList();
//
//		return resultList;
//	}

	//    plus revision kapcsolatokkal
//	itt jelentkezhet az n+1 problema, de feltetelezzuk, hogy egy entitast nem modositanak csillioszor, ezert megoldasnak elfogadjuk ezt is
	@Transactional
	@SuppressWarnings({"rawtypes", "unchecked"})
	public List<HistoryData<Airport>> getAirportHistory3(long id) {

		List resultList = AuditReaderFactory.get(em)
				.createQuery()
				.forRevisionsOfEntity(Airport.class, false, true)
				.add(AuditEntity.property("id").eq(id))
//				ha entitasokon belul revisionoznek ott lehetne Jointype, itt forRevisionsOfEntity itt nem lehet
//				ehelyett lejjebb streamen belul map-nel kenyszeritjuk ki, verziohelyes kapcsolat lesz
//				.traverseRelation("address", JoinType.LEFT)
				.getResultList()
				.stream()
				.map(o -> {
					Object[] objArray = (Object[])o;
					DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) objArray[1];
					Airport airport = (Airport) objArray[0];
					Address address = airport.getAddress();
					if (address != null)
						airport.getAddress().getCity(); // a getAdress meg nem kenyszeriti ki a kapcsolat betolteset, csak a plusz getCity() vagy
					airport.getArrivals().size();  // size()
					airport.getDepartures().size();

					return new HistoryData<Airport>(
							airport,
							(RevisionType) objArray[2],
							revisionEntity.getId(),
							revisionEntity.getRevisionDate()
					);
				}).toList();

		return resultList;
	}

	@Transactional
	public Image saveImageForAirport(long airportId, String fileName, byte[] bytes) {
		Airport airport = airportRepository.findById(airportId).get();
		Image image = Image.builder()
				.data(bytes)
				.fileName(fileName)
				.build();
		image = imageRepository.save(image);
		airport.getImages().add(image);
		return image;
	}
}
