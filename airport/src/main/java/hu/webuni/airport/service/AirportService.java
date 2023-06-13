package hu.webuni.airport.service;

import hu.webuni.airport.model.Airport;
import hu.webuni.airport.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AirportService {

	private static final Logger logger = LoggerFactory.getLogger(AirportService.class);

	private final AirportRepository airportRepository;

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
    public List<Airport> findAllWithRelationships(Pageable pageable) {
		// ezek mar lecsatolt entitasok lesznek, sima listaban ha nem lenne a Transactional annotacio
//		List<Airport> airports = airportRepository.findAllWithAddressAndDepartures(pageable);	--> in memory lapozas, minden sor bejon a db-bol
//		airports = airportRepository.findAllWithArrivals(pageable);

		List<Airport> airports = airportRepository.findAllWithAddress(pageable);
		List<Long> airportIds = airports.stream().map(Airport::getId).toList();

		airports = airportRepository.findByIdWithArrivals(airportIds);
		airports = airportRepository.findByIdWithDepartures(airportIds);

		return airports;
	}
}
