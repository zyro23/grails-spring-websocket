package ${model.packageName}

import grails.plugin.springwebsocket.GrailsSimpAnnotationMethodMessageHandler
import grails.plugin.springwebsocket.GrailsWebSocketAnnotationMethodMessageHandler
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@CompileStatic
@Configuration
@EnableWebSocketMessageBroker
class ${model.className} implements WebSocketMessageBrokerConfigurer {

    @Override
    void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker("/queue", "/topic")
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app")
    }

    @Override
    void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/stomp")
    }

    @Bean
    GrailsSimpAnnotationMethodMessageHandler grailsSimpAnnotationMethodMessageHandler(
            @Qualifier("clientInboundChannel") SubscribableChannel clientInboundChannel,
            @Qualifier("clientOutboundChannel") MessageChannel clientOutboundChannel,
            @Qualifier("brokerMessagingTemplate") SimpMessageSendingOperations brokerMessagingTemplate) {
        GrailsSimpAnnotationMethodMessageHandler handler = new GrailsSimpAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }

    @Bean
    GrailsWebSocketAnnotationMethodMessageHandler grailsWebSocketAnnotationMethodMessageHandler(
            @Qualifier("clientInboundChannel") SubscribableChannel clientInboundChannel,
            @Qualifier("clientOutboundChannel") MessageChannel clientOutboundChannel,
            @Qualifier("brokerMessagingTemplate") SimpMessageSendingOperations brokerMessagingTemplate) {
        GrailsWebSocketAnnotationMethodMessageHandler handler = new GrailsWebSocketAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }

}
