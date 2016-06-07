package grails.plugin.springwebsocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	ConfigObject config
	
	/**
	 *  using constructor-based injection here because the overridden configuration methods
	 *  are called (it seems) before property injection or @PostConstruct handling take place 
	 */
	WebSocketConfig(ConfigObject config) {
		assert config
		this.config = config
	}

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
		def stompRelayConf = config.messageBroker.stompRelay
		String[] brokerPrefixes = config.messageBroker.brokerPrefixes
		def relayEnabled = stompRelayConf.enabled
		if (relayEnabled) {
			def relay = mbr.enableStompBrokerRelay(brokerPrefixes)
			relay.relayHost = stompRelayConf.host
			relay.relayPort = stompRelayConf.port
			relay.systemLogin = stompRelayConf.systemLogin
			relay.systemPasscode = stompRelayConf.systemPasscode
			relay.clientLogin = stompRelayConf.clientLogin
			relay.clientPasscode = stompRelayConf.clientPasscode
		} else {
			mbr.enableSimpleBroker(brokerPrefixes)
		}

		String[] applicationDestinationPrefixes = config.messageBroker.applicationDestinationPrefixes
		mbr.setApplicationDestinationPrefixes(applicationDestinationPrefixes)

		def userDestinationPrefix = config.messageBroker.userDestinationPrefix
		mbr.setUserDestinationPrefix userDestinationPrefix
	}

	@Override
	void registerStompEndpoints(StompEndpointRegistry ser) {
		String[] allowedOrigins = config.allowedOrigins
		for (String[] endpoint in config.stompEndpoints) {
			ser.addEndpoint(endpoint).setAllowedOrigins(allowedOrigins).withSockJS()
		}
	}

}
