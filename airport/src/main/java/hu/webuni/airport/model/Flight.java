package hu.webuni.airport.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Audited
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

	@Column(name = "delay")
	private Integer delayInSec;
	
	
}
