package box.bookstorebe.configuration;

import box.bookstorebe.common.ClientProperty;
import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.WebContentDocument;
import box.bookstorebe.repository.common.webcontent.WebContentRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebContentRepository webContentRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        WebContentDocument webContentDocument = webContentRepository.findByKey(Const.AdminDomain);
        registry.addEndpoint("/socket").setAllowedOrigins(webContentDocument.getValue()).withSockJS();
    }
}