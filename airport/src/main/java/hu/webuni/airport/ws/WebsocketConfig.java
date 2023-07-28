package hu.webuni.airport.ws;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        websockettel ide kell csatlakozniuk
        registry.addEndpoint("/api/stomp");
//        ha SockJS-es kliensünk van azt is támogassuk
        registry.addEndpoint("/api/stomp").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        ami ilyen url-re jön üzenet azt egybol tovabbitja a feliratkozoknak, eleg a prefixet megadni
        registry.enableSimpleBroker("/topic");
//        ha a kliensek kuldenek fel websocketes kerest, szeretnenk szerveren oldalon messagemapperes metodusban, azt itt kellene kuldeniuk
        registry.setApplicationDestinationPrefixes("/app");
    }
}
