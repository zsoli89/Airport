package hu.webuni.airport.dto;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirportDto {
	
	private long id;
	@Size(min = 3, max = 20)
	private String name;
	private String iata;
}
