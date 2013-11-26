package grails.plugin.springwebsocket

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.messaging.config.EnableWebSocketMessageBroker
import org.springframework.web.socket.messaging.config.StompEndpointRegistry
import org.springframework.web.socket.messaging.config.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	static final List<String> DEFAULT_APPLICATION_DESTINATION_PREFIXES = ["/app"]
	static final List<String> DEFAULT_BROKER_PREFIXES = ["/queue", "/topic"]
	static final List<List<String>> DEFAULT_STOMP_ENDPOINTS = [["/stomp"]]
	
	@Resource
	GrailsApplication grailsApplication
	
	@Override
	void configureMessageBroker(MessageBrokerRegistry mbr) {
		def config = grailsApplication.config.grails?.plugin?.springwebsocket
		
		def brokerPrefixes = config?.messageBroker?.brokerPrefixes ?: DEFAULT_BROKER_PREFIXES
		mbr.enableSimpleBroker(brokerPrefixes as String[])
		
		def applicationDestinationPrefixes = config?.messageBroker?.applicationDestinationPrefixes ?: DEFAULT_APPLICATION_DESTINATION_PREFIXES
		mbr.setApplicationDestinationPrefixes(applicationDestinationPrefixes as String[])
	}

	@Override
	void registerStompEndpoints(StompEndpointRegistry ser) {
		def config = grailsApplication.config.grails?.plugin?.springwebsocket
		
		def stompEndpoints = config?.stompEndpoints ?: DEFAULT_STOMP_ENDPOINTS
		stompEndpoints.each {
			ser.addEndpoint(it as String[]).withSockJS()
		}
	}
	
}
