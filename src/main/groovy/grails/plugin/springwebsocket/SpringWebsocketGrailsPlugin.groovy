package grails.plugin.springwebsocket

import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware

import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.handler.AbstractHandlerMapping

class SpringWebsocketGrailsPlugin implements GrailsApplicationAware {

	def version = "2.0.0.BUILD-SNAPSHOT"
	def grailsVersion = "3.0.0 > *"
	def title = "Spring Websocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring Websocket Plugin"
	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [url: "https://github.com/zyro23/grails-spring-websocket"]

	GrailsApplication grailsApplication
	
	def doWithSpring = {
		def config = ConfigUtils.getSpringWebsocketConfig grailsApplication
		
		if (!config.useCustomConfig) {
			webSocketConfig WebSocketConfig, config

			grailsSimpAnnotationMethodMessageHandler(
				GrailsSimpAnnotationMethodMessageHandler,
				ref("clientInboundChannel"),
				ref("clientOutboundChannel"),
				ref("brokerMessagingTemplate")
			) {
				destinationPrefixes = config.messageBroker.applicationDestinationPrefixes
			}
		}
	}

	def doWithApplicationContext = { ApplicationContext ctx ->
		def config = ConfigUtils.getSpringWebsocketConfig grailsApplication
		
		String[] additionalMappings = config.dispatcherServlet.additionalMappings
		ServletRegistrationBean dispatcherServletRegistration = ctx.getBean "dispatcherServletRegistration"
		dispatcherServletRegistration.addUrlMappings additionalMappings
		
		// TODO: think this should be configurable via spring websocket cfg?
		// TODO: yes it is. upcoming change with spring-4.1
		AbstractHandlerMapping handlerMapping = ctx.getBean "stompWebSocketHandlerMapping"
		handlerMapping.alwaysUseFullPath = true
	}

}
