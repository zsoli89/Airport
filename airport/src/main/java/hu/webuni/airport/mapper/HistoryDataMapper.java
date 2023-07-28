package hu.webuni.airport.mapper;

import hu.webuni.airport.api.model.FlightDto;
import hu.webuni.airport.api.model.HistoryDataAirportDto;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.Flight;
import hu.webuni.airport.model.HistoryData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryDataMapper {

    HistoryDataAirportDto airportHistoryDataToDto(HistoryData<Airport> hd);

    @Mapping(ignore = true, target = "takeoff")
    @Mapping(ignore = true, target = "landing")
    FlightDto flightToDto(Flight flight);
}
