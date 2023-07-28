package hu.webuni.airport.config;

import hu.webuni.airport.xmlws.AirportXmlWs;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@RequiredArgsConstructor
@Configuration
public class WebServiceConfig {

    private final Bus bus;
    private final AirportXmlWs airportXmlWs;

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, airportXmlWs);
//        ezen az url-en fog kipublikalodni
        endpoint.publish("/airport");
        return endpoint;
    }
}
