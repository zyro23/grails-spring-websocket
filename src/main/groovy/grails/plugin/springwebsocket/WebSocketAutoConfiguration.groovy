package grails.plugin.springwebsocket

import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration(after = GrailsAutoConfiguration)
@CompileStatic
class WebSocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "webSocketConfig")
    DefaultWebSocketConfig webSocketConfig() {
        return new DefaultWebSocketConfig()
    }

}
