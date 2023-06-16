package hu.webuni.airport.mapper;

import java.util.List;

import hu.webuni.airport.dto.FlightDto;
import hu.webuni.airport.model.Flight;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import hu.webuni.airport.dto.AirportDto;
import hu.webuni.airport.model.Airport;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AirportMapper {

	List<AirportDto> airportsToDtos(List<Airport> airports);

	// igy tudjuk megkulonboztetni melyik dto-va alakito metodust hasznalja
	@IterableMapping(qualifiedByName = "summary")
	List<AirportDto> airportSummariesToDtos(List<Airport> airports);

	AirportDto airportToDto(Airport airport);

//	ahhoz hogy ignorálja az addresst vagy a többit, kell a mapping annotáció
	@Named("summary")
	@Mapping(target = "address", ignore = true)
	@Mapping(target = "departures", ignore = true)
	@Mapping(target = "arrivals", ignore = true)
	AirportDto airportSummaryToDto(Airport airport);

	Airport dtoToAirport(AirportDto airportDto);

//	végtelen ciklust előz meg, hogy folyton visszakérdezzen
	@Mapping(target = "takeoff", ignore = true)
	@Mapping(target = "landing", ignore = true)
	FlightDto flightToDto(Flight flight);
}
