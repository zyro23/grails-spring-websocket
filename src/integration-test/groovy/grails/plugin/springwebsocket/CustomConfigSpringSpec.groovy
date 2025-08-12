package grails.plugin.springwebsocket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest
class CustomConfigSpringSpec extends Specification {

    @EnableWebSocketMessageBroker
    @TestConfiguration("webSocketConfig")
    static class TestWebSocketConfig implements WebSocketMessageBrokerConfigurer {
        @Override
        void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
            stompEndpointRegistry.addEndpoint("/test")
        }
    }

    @Autowired
    ApplicationContext applicationContext

    void "ctx loads with custom websocket (spring-)config"() {
        expect:
        applicationContext.getBean("webSocketConfig") instanceof TestWebSocketConfig
        applicationContext.getBean(SimpMessagingTemplate)
    }

}
