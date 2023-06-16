package hu.webuni.airport.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import hu.webuni.airport.model.Airport;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AirportRepository extends JpaRepository<Airport, Long>{
	
	Long countByIata(String iata);
	
	Long countByIataAndIdNot(String iata, long id);
	// fetchGraph az alapertelmezett, masik a loadGraph, hiaba az
	// eagerFetch az entitason itt is meg kell adni az address mellett a type kiegeszitest
	// igy egyetlen select lesz a sok select helyett
	//	@Query("SELECT a FROM Airport a LEFT JOIN FETCH a.address")
	// a type ... nem kell mert semmit nem toltok be EAGER modon
	// nem kellenek az arrivalek a legjobb megoldashoz

//	nem full esetben jó
//	full esetben - Multiple bags exception, ha tobb listát szeretnénk befetchelni nem engedi, ennek megoldása hogy ne
//	List legyen hanem Set, Set-tel nem lehet többször ugyanaz a Flight az adott Airporthoz
//	ezzel egyetlen Select lenne, de sok a visszatérő adatok sora, Descartes-szorzat
//	@EntityGraph(attributePaths = {"address", "departures", "arrivals"}

	@EntityGraph(attributePaths = {"address", "departures"/*, "arrivals"*/}/*, type = EntityGraph.EntityGraphType.LOAD*/)
	@Query("SELECT a FROM Airport a")
	List<Airport> findAllWithAddressAndDepartures(Pageable pageable);

//	alapértelmezett EntityGraph típus a .FETCH, ilyenkor szigorúan az töltődik be amit attributePath-nek megadtunk "arrivals"
//	még akkor sem töltődik be ha kapcsolatban FetchType.EAGER miatt kellene
//	ha .LOAD, azok is benne lesznek amiket nem specifikálok
	@EntityGraph(attributePaths = {"arrivals"})
	@Query("SELECT a FROM Airport a")
	List<Airport> findAllWithArrivals(Pageable pageable);

//	ha be szeretnél tölteni az addresseket másik megoldás
//	@Query("SELECT a FROM Airport a LEFT JOIN FETCH a.address") ehelyett EntityGraph
	@EntityGraph(attributePaths = {"address"})
	@Query("SELECT a FROM Airport a")
	List<Airport> findAllWithAddress(Pageable pageable);

	@EntityGraph(attributePaths = {"arrivals"})
	@Query("SELECT a FROM Airport a WHERE a.id IN :ids")
	List<Airport> findByIdWithArrivals(List<Long> ids);

	@EntityGraph(attributePaths = {"departures"})
	@Query("SELECT a FROM Airport a WHERE a.id IN :ids")
	List<Airport> findByIdWithDepartures(List<Long> ids);
}
