import grails.plugin.springwebsocket.GrailsSimpAnnotationMethodMessageHandler
import grails.plugin.springwebsocket.WebSocketConfig

import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter

class SpringWebsocketGrailsPlugin {
	
	def version = "0.1.BUILD-SNAPSHOT"
	def grailsVersion = "2.4 > *"
	def pluginExcludes = ["grails-app/views/error.gsp"]

	def title = "Spring Websocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring Websocket Plugin"

	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [ url: "https://github.com/zyro23/grails-spring-websocket" ]

	def doWithWebDescriptor = { xml ->
		def config = application.config.grails?.plugin?.springwebsocket
		def additionalMappings = config?.dispatcherServlet?.additionalMappings ?: ["/*"]
		additionalMappings.each { urlPattern ->
			xml."servlet-mapping"[-1] + {
				"servlet-mapping" {
					"servlet-name" "grails"
					"url-pattern" urlPattern
				}
			}
		}
	}

	def doWithSpring = {
		def config = application.config.grails?.plugin?.springwebsocket
		
		httpRequestHandlerAdapter HttpRequestHandlerAdapter
		
		if (!config.useCustomConfig) {
			webSocketConfig WebSocketConfig
			
			grailsSimpAnnotationMethodMessageHandler(
				GrailsSimpAnnotationMethodMessageHandler,
				ref("brokerMessagingTemplate"),
				ref("webSocketResponseChannel")
			) {
				destinationPrefixes = config?.messageBroker?.applicationDestinationPrefixes ?: WebSocketConfig.DEFAULT_APPLICATION_DESTINATION_PREFIXES
			}
		}
	}

}
