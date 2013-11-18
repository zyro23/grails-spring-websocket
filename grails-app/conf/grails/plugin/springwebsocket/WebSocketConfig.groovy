package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerConfigurer
import org.springframework.messaging.simp.config.StompEndpointRegistry
import org.springframework.messaging.simp.config.WebSocketMessageBrokerConfigurationSupport
import org.springframework.messaging.simp.handler.SimpAnnotationMethodMessageHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter

@CompileStatic
@Configuration
class WebSocketConfig extends WebSocketMessageBrokerConfigurationSupport {
	
	static final List<String> DEFAULT_APPLICATION_DESTINATION_PREFIXES = ["/app"]
	static final List<String> DEFAULT_BROKER_PREFIXES = ["/queue", "/topic"]
	static final List<List<String>> DEFAULT_STOMP_ENDPOINTS = [["/stomp"]]
	
	@Resource
	private GrailsApplication grailsApplication
	
	@Bean
	HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
		return new WebMvcConfigurationSupport().httpRequestHandlerAdapter()
	}
	
	@Bean
	@Override
	SimpAnnotationMethodMessageHandler annotationMethodMessageHandler() {
		def handler = new GrailsSimpAnnotationMethodMessageHandler(brokerMessagingTemplate(), webSocketResponseChannel())
		handler.setDestinationPrefixes getApplicationDestinationPrefixes()
		handler.setMessageConverter simpMessageConverter()
		webSocketRequestChannel().subscribe handler
		return handler
	}
	
	@Override
	void configureMessageBroker(MessageBrokerConfigurer mbc) {
		mbc.enableSimpleBroker(getBrokerPrefixes() as String[])
		mbc.setAnnotationMethodDestinationPrefixes(getApplicationDestinationPrefixes() as String[])
	}

	@Override
	void registerStompEndpoints(StompEndpointRegistry ser) {
		getStompEndpoints().each {
			ser.addEndpoint(it as String[]).withSockJS()
		}
	}
	
	@CompileStatic(TypeCheckingMode.SKIP)
	private Collection<String> getApplicationDestinationPrefixes() {
		def destinationPrefixes = getConfig()?.messageBroker?.applicationDestinationPrefixes ?: DEFAULT_APPLICATION_DESTINATION_PREFIXES
		return destinationPrefixes
	}
	
	@CompileStatic(TypeCheckingMode.SKIP)
	private Collection<String> getBrokerPrefixes() {
		def destinationPrefixes = getConfig()?.messageBroker?.brokerPrefixes ?: DEFAULT_BROKER_PREFIXES
		return destinationPrefixes
	}
	
	@CompileStatic(TypeCheckingMode.SKIP)
	private Collection<Collection<String>> getStompEndpoints() {
		def stompEndpoints = getConfig()?.stompEndpoints ?: DEFAULT_STOMP_ENDPOINTS
		return stompEndpoints
	}
	
	@CompileStatic(TypeCheckingMode.SKIP)
	private ConfigObject getConfig() {
		def config = grailsApplication.config.grails?.plugin?.springwebsocket
		return config
	}

}
