package hu.webuni.airport.dto;

import hu.webuni.airport.model.Flight;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirportDto {

	private long id;
	@Size(min = 3, max = 20)
	private String name;
	private String iata;
	private AddressDto address;
	private List<FlightDto> departures;
	private Set<FlightDto> arrivals;
}
