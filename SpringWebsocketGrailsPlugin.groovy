import grails.plugin.springwebsocket.ConfigUtils
import grails.plugin.springwebsocket.GrailsSimpAnnotationMethodMessageHandler
import grails.plugin.springwebsocket.WebSocketConfig

import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter

class SpringWebsocketGrailsPlugin {

	def version = "1.3.1"
	def grailsVersion = "2.5.0 > *"
	def title = "Spring Websocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring Websocket Plugin"
	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [ url: "https://github.com/zyro23/grails-spring-websocket" ]

	def doWithWebDescriptor = { xml ->
		def config = ConfigUtils.getSpringWebsocketConfig application

		config.dispatcherServlet.additionalMappings.each { urlPattern ->
			xml."servlet-mapping"[-1] + {
				"servlet-mapping" {
					"servlet-name" "grails"
					"url-pattern" urlPattern
				}
			}
		}
	}

	def doWithSpring = {
		def config = ConfigUtils.getSpringWebsocketConfig application

		httpRequestHandlerAdapter HttpRequestHandlerAdapter

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

	def doWithApplicationContext = { ctx ->
		// TODO: think this should be configurable via spring websocket cfg?
		ctx.stompWebSocketHandlerMapping.alwaysUseFullPath = true
	}

}
