package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Import

@AutoConfiguration
@CompileStatic
@ConditionalOnMissingBean(name = "webSocketConfig")
@Import(DefaultWebSocketConfig)
class WebSocketAutoConfiguration {
}
