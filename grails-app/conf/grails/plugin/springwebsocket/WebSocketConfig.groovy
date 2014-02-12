package grails.plugin.springwebsocket

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
	
	def config = ConfigUtils.springWebsocketConfig
	
	@Override
	void configureClientInboundChannel(ChannelRegistration cr) {
		def poolSizeCore = config.clientInboundChannel.threadPoolSize.from
		def poolSizeMax = config.clientInboundChannel.threadPoolSize.to
		cr.taskExecutor().corePoolSize(poolSizeCore).maxPoolSize(poolSizeMax)
	}

	@Override
	void configureClientOutboundChannel(ChannelRegistration cr) {
		def poolSizeCore = config.clientOutboundChannel.threadPoolSize.from
		def poolSizeMax = config.clientOutboundChannel.threadPoolSize.to
		cr.taskExecutor().corePoolSize(poolSizeCore).maxPoolSize(poolSizeMax)
	}
	
	@Override
	void configureMessageBroker(MessageBrokerRegistry mbr) {
		def brokerPrefixes = config.messageBroker.brokerPrefixes
		def relayEnabled = config.messageBroker.stompRelay.enabled
		if (relayEnabled) {
			def relay = mbr.enableStompBrokerRelay(brokerPrefixes as String[])
			relay.relayHost = config.messageBroker.stompRelay.host
			relay.relayPort = config.messageBroker.stompRelay.port
			relay.systemLogin = config.messageBroker.stompRelay.systemLogin
			relay.systemPasscode = config.messageBroker.stompRelay.systemPasscode
			relay.clientLogin = config.messageBroker.stompRelay.clientLogin
			relay.clientPasscode = config.messageBroker.stompRelay.clientPasscode
		} else {
			mbr.enableSimpleBroker(brokerPrefixes as String[])
		}
		
		def applicationDestinationPrefixes = config.messageBroker.applicationDestinationPrefixes
		mbr.setApplicationDestinationPrefixes(applicationDestinationPrefixes as String[])
		
		def userDestinationPrefix = config.messageBroker.userDestinationPrefix
		mbr.setUserDestinationPrefix userDestinationPrefix
	}

	@Override
	void registerStompEndpoints(StompEndpointRegistry ser) {
		def stompEndpoints = config.stompEndpoints
		stompEndpoints.each {
			ser.addEndpoint(it as String[]).withSockJS()
		}
	}

}
