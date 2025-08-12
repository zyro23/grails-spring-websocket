package grails.plugin.springwebsocket


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("test")
@SpringBootTest
class DefaultConfigSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    void "ctx loads with default websocket (auto-)config"() {
        expect:
        applicationContext.getBean("webSocketConfig") instanceof DefaultWebSocketConfig
        applicationContext.getBean(SimpMessagingTemplate)
    }

}


