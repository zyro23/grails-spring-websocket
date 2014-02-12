package grails.plugin.springwebsocket

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	static final List<String> DEFAULT_APPLICATION_DESTINATION_PREFIXES = ["/app"]
	static final List<String> DEFAULT_BROKER_PREFIXES = ["/queue", "/topic"]
	static final boolean DEFAULT_RELAY_ENABLED = false
	static final String DEFAULT_RELAY_HOST = "127.0.0.1"
	static final int DEFAULT_RELAY_PORT = 61613
	static final String DEFAULT_RELAY_LOGIN = "guest"
	static final String DEFAULT_RELAY_PASSCODE = "guest"
	static final List<List<String>> DEFAULT_STOMP_ENDPOINTS = [["/stomp"]]
	static final Range<Integer> DEFAULT_THREAD_POOL_SIZE = 4..10
	static final String DEFAULT_USER_DESTINATION_PREFIX = "/user/"
	
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
		def relayEnabled = config?.messageBroker?.stompRelay?.enabled ?: DEFAULT_RELAY_ENABLED
		if (relayEnabled) {
			def relay = mbr.enableStompBrokerRelay(brokerPrefixes as String[])
			relay.relayHost = config?.messageBroker?.stompRelay?.host ?: DEFAULT_RELAY_HOST
			relay.relayPort = config?.messageBroker?.stompRelay?.port ?: DEFAULT_RELAY_PORT
			relay.systemLogin = config?.messageBroker?.stompRelay?.systemLogin ?: DEFAULT_RELAY_LOGIN
			relay.systemPasscode = config?.messageBroker?.stompRelay?.systemPasscode ?: DEFAULT_RELAY_PASSCODE
			relay.clientLogin = config?.messageBroker?.stompRelay?.clientLogin ?: DEFAULT_RELAY_LOGIN
			relay.clientPasscode = config?.messageBroker?.stompRelay?.clientPasscode ?: DEFAULT_RELAY_PASSCODE
		} else {
			mbr.enableSimpleBroker(brokerPrefixes as String[])
		}
		
		def applicationDestinationPrefixes = config?.messageBroker?.applicationDestinationPrefixes ?: DEFAULT_APPLICATION_DESTINATION_PREFIXES
		mbr.setApplicationDestinationPrefixes(applicationDestinationPrefixes as String[])
		
		def userDestinationPrefix = config?.messageBroker?.userDestinationPrefix ?: DEFAULT_USER_DESTINATION_PREFIX
		mbr.setUserDestinationPrefix userDestinationPrefix
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
