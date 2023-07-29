package hu.webuni.airport.web;

import com.querydsl.core.types.Predicate;
import hu.webuni.airport.api.model.FlightDto;
import hu.webuni.airport.mapper.FlightMapper;
import hu.webuni.airport.model.Flight;
import hu.webuni.airport.repository.FlightRepository;
import hu.webuni.airport.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
//@RestController
@RequestMapping("/api/flights")
public class FlightControllerOld {

	private final FlightService flightService;
	private final FlightMapper flightMapper;
	private final FlightRepository flightRepository;

	@PostMapping
	public FlightDto createFlight(@RequestBody @Valid FlightDto flightDto) {
		Flight flight = flightService.save(flightMapper.dtoToFlight(flightDto));
		return flightMapper.flightToDto(flight);
	}
	
	@PostMapping("/search")
	public List<FlightDto> searchFlights(@RequestBody FlightDto example){
		return flightMapper.flightsToDtos(flightService.findFlightsByExample(flightMapper.dtoToFlight(example)));
	}

	// a query-ben érkező paraméterek alapján kitölti a predicate-ket a @QuerydslPredicate
	@GetMapping("/search")
	public List<FlightDto> searchFlights2(@QuerydslPredicate(root = Flight.class) Predicate predicate){
		return flightMapper.flightsToDtos(flightRepository.findAll(predicate));
	}

	@PostMapping("/{flightId}/pollDelay/{rate}")
	public void startDelayPolling(@PathVariable long flightId,@PathVariable long rate) {
		flightService.startDelayPollingForFlight(flightId, rate);
	}

	@DeleteMapping("/{flightId}/pollDelay")
	public void stopDelayPolling(@PathVariable long flightId) {
		flightService.stopDelayPollingForFlight(flightId);
	}
}
