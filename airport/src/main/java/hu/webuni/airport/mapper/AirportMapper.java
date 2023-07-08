package hu.webuni.airport.mapper;


import hu.webuni.airport.api.model.AirportDto;
import hu.webuni.airport.api.model.FlightDto;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.Flight;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    List<AirportDto> airportsToDtos(List<Airport> airports);

    // igy tudjuk megkulonboztetni melyik dto-va alakito metodust hasznalja
    @IterableMapping(qualifiedByName = "summary")
    List<AirportDto> airportSummariesToDtos(List<Airport> airports);

    AirportDto airportToDto(Airport airport);

    //	ahhoz hogy ignorálja az addresst és a többi kapcsolatot, kell a mapping annotáció
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
