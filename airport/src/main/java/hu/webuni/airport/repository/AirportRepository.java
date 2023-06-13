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
	@EntityGraph(attributePaths = {"address", "departures"/*, "arrivals"*/}/*, type = EntityGraph.EntityGraphType.LOAD*/)
	@Query("SELECT a FROM Airport a")
	List<Airport> findAllWithAddressAndDepartures(Pageable pageable);

	@EntityGraph(attributePaths = {"arrivals"})
	@Query("SELECT a FROM Airport a")
	List<Airport> findAllWithArrivals(Pageable pageable);

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
