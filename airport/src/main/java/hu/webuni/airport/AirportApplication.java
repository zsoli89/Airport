package hu.webuni.airport;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hu.webuni.airport.service.AirportService;
import hu.webuni.airport.service.PriceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SpringBootApplication
public class AirportApplication implements CommandLineRunner {

	private final PriceService priceService;
	
	private final AirportService airportService;
	
	public static void main(String[] args) {
		SpringApplication.run(AirportApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(priceService.getFinalPrice(200));
		System.out.println(priceService.getFinalPrice(20000));
	}

}
