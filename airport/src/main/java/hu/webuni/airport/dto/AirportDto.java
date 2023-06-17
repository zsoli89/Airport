package hu.webuni.airport.dto;

import lombok.*;

import javax.validation.constraints.Size;
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
	private Set<FlightDto> departures;
	private Set<FlightDto> arrivals;
}
