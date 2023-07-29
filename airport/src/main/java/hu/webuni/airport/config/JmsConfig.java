package hu.webuni.airport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
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

    @Configuration
    public class ArtemisConfig implements ArtemisConfigurationCustomizer {
        @Override
        public void customize(org.apache.activemq.artemis.core.config.Configuration configuration) {
            // Allow Artemis to accept tcp connections (Default port localhost:61616)
            configuration.addConnectorConfiguration("nettyConnector", new TransportConfiguration(NettyConnectorFactory.class.getName()));
            configuration.addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName()));
        }
    }

//    ez a pom-ban emlitett activemq TCP pulikalasa, hogy mas alkalmazasok is csatlakozhassanak ra
//    spring 3 elotti
//    @Bean
//    public BrokerService broker() throws Exception {
//        BrokerService brokerService = new BrokerService();
//        brokerService.addConnector("tcp://localhost:9999");
////        enelkul nem tudja perzisztensen tarolni az activemq
//        brokerService.setPersistent(true); // ahhoz hogy true lehessen kell a kahadb dependencia is
//        return brokerService;
//    }
}

//import org.apache.activemq.broker.BrokerService;
