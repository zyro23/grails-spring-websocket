package grails.plugin.springwebsocket

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.EnableWebSocketMessageBroker
import org.springframework.messaging.simp.config.MessageBrokerConfigurer
import org.springframework.messaging.simp.config.StompEndpointRegistry
import org.springframework.messaging.simp.config.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	static final List<String> DEFAULT_APPLICATION_DESTINATION_PREFIXES = ["/app"]
	static final List<String> DEFAULT_BROKER_PREFIXES = ["/queue", "/topic"]
	static final List<List<String>> DEFAULT_STOMP_ENDPOINTS = [["/stomp"]]
	
	@Resource
	GrailsApplication grailsApplication
	
	@Override
	void configureMessageBroker(MessageBrokerConfigurer mbc) {
		def config = grailsApplication.config.grails?.plugin?.springwebsocket
		
		def brokerPrefixes = config?.messageBroker?.brokerPrefixes ?: DEFAULT_BROKER_PREFIXES
		mbc.enableSimpleBroker(brokerPrefixes as String[])
		
		def applicationDestinationPrefixes = config?.messageBroker?.applicationDestinationPrefixes ?: DEFAULT_APPLICATION_DESTINATION_PREFIXES
		mbc.setAnnotationMethodDestinationPrefixes(applicationDestinationPrefixes as String[])
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
