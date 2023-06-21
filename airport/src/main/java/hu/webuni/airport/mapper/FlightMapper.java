package hu.webuni.airport.mapper;

import java.util.List;

import hu.webuni.airport.dto.AirportDto;
import hu.webuni.airport.model.Airport;
import org.mapstruct.Mapper;

import hu.webuni.airport.dto.FlightDto;
import hu.webuni.airport.model.Flight;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FlightMapper {

	Flight dtoToFlight(FlightDto flightDto);

	FlightDto flightToDto(Flight flight);
	List<FlightDto> flightsToDtos(List<Flight> flights);

	List<FlightDto> flightsToDtos(Iterable<Flight> findAll);

	@Mapping(target = "address", ignore = true)
	@Mapping(target = "departures", ignore = true)
	@Mapping(target = "arrivals", ignore = true)
	AirportDto airportSummaryToDto(Airport airport);

}
