package hu.webuni.airport.ws;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

//  ha van keses van egy rest vegpont, ebbol a vegpontbol fogjuk szetbroadcastolni azoknak a klienseknek akik
//    feliratkoztak annak a jaratnak a keseseire

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
//        mi ezt nem fogjuk hasznalni
        registry.setApplicationDestinationPrefixes("/app");
    }
}
