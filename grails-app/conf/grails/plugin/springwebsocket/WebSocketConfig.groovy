package grails.plugin.springwebsocket

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	static final List<String> DEFAULT_APPLICATION_DESTINATION_PREFIXES = ["/app"]
	static final List<String> DEFAULT_BROKER_PREFIXES = ["/queue", "/topic"]
	static final Range<Integer> DEFAULT_THREAD_POOL_SIZE = 4..10
	static final List<List<String>> DEFAULT_STOMP_ENDPOINTS = [["/stomp"]]
	
	@Resource
	GrailsApplication grailsApplication
	
	@Override
	void configureClientInboundChannel(ChannelRegistration cr) {
		def config = grailsApplication.config.grails?.plugin?.springwebsocket
		
		def poolSizeCore = config?.clientInboundChannel?.threadPoolSize?.from ?: DEFAULT_THREAD_POOL_SIZE.from
		def poolSizeMax = config?.clientInboundChannel?.threadPoolSize?.to ?: DEFAULT_THREAD_POOL_SIZE.to
		cr.taskExecutor().corePoolSize(poolSizeCore).maxPoolSize(poolSizeMax)
	}

	@Override
	void configureClientOutboundChannel(ChannelRegistration cr) {
		def config = grailsApplication.config.grails?.plugin?.springwebsocket
		
		def poolSizeCore = config?.clientOutboundChannel?.threadPoolSize?.from ?: DEFAULT_THREAD_POOL_SIZE.from
		def poolSizeMax = config?.clientOutboundChannel?.threadPoolSize?.to ?: DEFAULT_THREAD_POOL_SIZE.to
		cr.taskExecutor().corePoolSize(poolSizeCore).maxPoolSize(poolSizeMax)
	}
	
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
