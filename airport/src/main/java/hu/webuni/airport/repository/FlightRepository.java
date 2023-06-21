package hu.webuni.airport.repository;

import com.querydsl.core.types.dsl.StringExpression;
import hu.webuni.airport.model.Flight;
import hu.webuni.airport.model.QFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Optional;

public interface FlightRepository extends
        JpaRepository<Flight, Long>,
        JpaSpecificationExecutor<Flight>,
        QuerydslPredicateExecutor<Flight>,
        QuerydslBinderCustomizer<QFlight> {

    //Ahhoz, hogy a Controller-ben levő Predicate ne csak pontos egyezést fogadjon el, extendalni kell:
    //QuerydslBinderCustomizer<QFlight> ezzel lehet testre szabni milyen feltételekkel szeretnénk keresni
    @Override
    default void customize(QuerydslBindings bindings, QFlight flight) {
        // kell a first() ugyanolyan néven több requestet is felküldhetek
        // bindings.bind(flight.flightNumber).first((path, value) -> path.startsWithIgnoreCase(value));
        bindings.bind(flight.flightNumber).first(StringExpression::startsWithIgnoreCase);
        bindings.bind(flight.takeoff.iata).first(StringExpression::startsWith);

//      tesztnek
        bindings.bind(flight.landing.iata).first(StringExpression::containsIgnoreCase);

//        bindings.bind(flight.takeoffTime).first((path, value) -> {
//            LocalDateTime startOfDay = LocalDateTime.of(value.toLocalDate(), LocalTime.MIDNIGHT);
//            return path.between(startOfDay, startOfDay.plusDays(1));
//        });
        // egy mezőre vonatkozoan tobb query parametert is el akarok fogadni akkor all, first helyett
        bindings.bind(flight.takeoffTime).all((path, values) -> {
            if (values.size() != 2)
                return Optional.empty();

            //mivel nem List, hanem Collection, ezért nem tudom kikérni a 0. és 1. elemét, iterator() kérek rá
            Iterator<? extends LocalDateTime> iterator = values.iterator();
            LocalDateTime start = iterator.next();
            LocalDateTime end = iterator.next();
            LocalDateTime startOfDay = LocalDateTime.of(start.toLocalDate(), LocalTime.MIDNIGHT);
            LocalDateTime endOfDay = LocalDateTime.of(end.toLocalDate(), LocalTime.MIDNIGHT).plusDays(1);

            return Optional.ofNullable(path.between(startOfDay, endOfDay));
        });
    }
}
