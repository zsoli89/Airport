package hu.webuni.airport.web;


import com.querydsl.core.types.Predicate;
import hu.webuni.airport.api.FlightControllerApi;
import hu.webuni.airport.api.model.FlightDto;
import hu.webuni.airport.mapper.FlightMapper;
import hu.webuni.airport.model.Flight;
import hu.webuni.airport.repository.FlightRepository;
import hu.webuni.airport.service.FlightService;
import hu.webuni.airport.ws.DelayMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FlightController implements FlightControllerApi {

    private final NativeWebRequest nativeWebRequest;
    private final FlightService flightService;
    private final FlightMapper flightMapper;
    private final FlightRepository flightRepository;
    private final QuerydslPredicateArgumentResolver predicateResolver;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(nativeWebRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FlightDto> createFlight(FlightDto flightDto) {
        Flight flight = flightService.save(flightMapper.dtoToFlight(flightDto));
        return ResponseEntity.ok(flightMapper.flightToDto(flight));
    }

    @Override
    public ResponseEntity<List<FlightDto>> searchFlights(FlightDto flightDto) {
        return ResponseEntity.ok(flightMapper.flightsToDtos(
                flightService.findFlightsByExample(
                        flightMapper.dtoToFlight(flightDto))));
    }

    public void configurePredicate(@QuerydslPredicate(root = Flight.class) Predicate predicate) {}

    private Predicate createPredicate(String configMethodName) {
        Method method;
        try {
            method = this.getClass().getMethod(configMethodName, Predicate.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            ModelAndViewContainer mavContainer = null;
            WebDataBinderFactory binderFactory = null;
            return (Predicate) predicateResolver.resolveArgument(methodParameter, mavContainer, nativeWebRequest, binderFactory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<FlightDto>> searchFlights2(Long id, String flightNumber, String takeoffIata, List<String> takeoffTime) {
        Predicate predicate = createPredicate("configurePredicate");
        return ResponseEntity.ok(flightMapper.flightsToDtos(flightRepository.findAll(predicate)));
    }

    @Override
    public ResponseEntity<Void> startDelayPolling(Long flightId, Long rate) {
        flightService.startDelayPollingForFlight(flightId, rate);
        return ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<Void> stopDelayPolling(Long flightId) {
            flightService.stopDelayPollingForFlight(flightId);
            return ResponseEntity.ok().build();
        }

    @Override
    public ResponseEntity<Void> reportDelay(Long id, String delay) {
        this.messagingTemplate.convertAndSend("/topic/delay/" + delay, new DelayMessage(Integer.parseInt(delay), OffsetDateTime.now()));
        return ResponseEntity.ok().build();
    }
}
