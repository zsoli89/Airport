package hu.webuni.airport.ws;


import hu.webuni.airport.security.JwtAuthFilter;
import hu.webuni.airport.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

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

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    List<String> authHeaders = accessor.getNativeHeader("X-Authorization");

                    UsernamePasswordAuthenticationToken authentication = JwtAuthFilter.createUserDetailsFromAuthHeader(authHeaders.get(0), jwtService);
                    accessor.setUser(authentication);
                }

                return message;
            }
        });
    }

}
