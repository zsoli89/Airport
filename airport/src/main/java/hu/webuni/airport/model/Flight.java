package hu.webuni.airport.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Flight {

	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private long id;
	
	private String flightNumber;
	private LocalDateTime takeoffTime;

	@ManyToOne
	private Airport takeoff;
	
	@ManyToOne
	private Airport landing;
	
	
}
