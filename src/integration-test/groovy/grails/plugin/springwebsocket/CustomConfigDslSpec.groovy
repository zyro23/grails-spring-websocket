package grails.plugin.springwebsocket

import grails.core.GrailsApplicationLifeCycleAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest(properties = "spring.autoconfigure.exclude=grails.plugin.springwebsocket.WebSocketAutoConfiguration")
class CustomConfigDslSpec extends Specification {

    @Configuration("webSocketConfig")
    @EnableWebSocketMessageBroker
    static class TestWebSocketConfig implements WebSocketMessageBrokerConfigurer {
        @Override
        void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
            stompEndpointRegistry.addEndpoint("/test")
        }
    }

    @TestConfiguration
    static class TestConfig extends GrailsApplicationLifeCycleAdapter {
        @Override
        Closure doWithSpring() {
            return {
                webSocketConfig(TestWebSocketConfig)
            }
        }
    }

    @Autowired
    ApplicationContext applicationContext

    void "ctx loads with custom websocket (dsl-)config"() {
        expect:
        applicationContext.getBean("webSocketConfig") instanceof TestWebSocketConfig
        applicationContext.getBean(SimpMessagingTemplate)
    }
}
