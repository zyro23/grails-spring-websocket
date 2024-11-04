package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
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
class DefaultWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker "/queue", "/topic"
        messageBrokerRegistry.setApplicationDestinationPrefixes "/app"
    }

    @Override
    void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/stomp")
    }

    @Bean
    GrailsSimpAnnotationMethodMessageHandler grailsSimpAnnotationMethodMessageHandler(
            SubscribableChannel clientInboundChannel,
            MessageChannel clientOutboundChannel,
            SimpMessageSendingOperations brokerMessagingTemplate) {
        def handler = new GrailsSimpAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }

    @Bean
    GrailsWebSocketAnnotationMethodMessageHandler grailsWebSocketAnnotationMethodMessageHandler(
            SubscribableChannel clientInboundChannel,
            MessageChannel clientOutboundChannel,
            SimpMessageSendingOperations brokerMessagingTemplate) {
        def handler = new GrailsWebSocketAnnotationMethodMessageHandler(clientInboundChannel, clientOutboundChannel, brokerMessagingTemplate)
        handler.destinationPrefixes = ["/app"]
        return handler
    }

}
