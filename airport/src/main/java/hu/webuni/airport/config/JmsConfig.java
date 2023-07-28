package hu.webuni.airport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);    // enelkul nem tudna deserializalni az OffsetDateTime-ot
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

//    ez a pom-ban emlitett activemq TCP pulikalasa, hogy mas alkalmazasok is csatlakozhassanak ra
    @Bean
    public BrokerService broker() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.addConnector("tcp://localhost:9999");
//        enelkul nem tudja perzisztensen tarolni az activemq
        brokerService.setPersistent(true); // ahhoz hogy true lehessen kell a kahadb dependencia is
        return brokerService;
    }
}
