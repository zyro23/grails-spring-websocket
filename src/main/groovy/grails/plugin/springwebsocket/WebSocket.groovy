package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.messaging.simp.SimpMessageSendingOperations

@CompileStatic
trait WebSocket {

	@Autowired
	@Delegate
	@Qualifier("brokerMessagingTemplate")
	SimpMessageSendingOperations brokerMessagingTemplate

}