package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate

@CompileStatic
trait WebSocket {

    @Autowired
    @Delegate
    SimpMessagingTemplate brokerMessagingTemplate

}