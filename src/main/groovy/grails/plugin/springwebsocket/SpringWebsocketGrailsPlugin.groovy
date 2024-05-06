package grails.plugin.springwebsocket

import grails.plugins.Plugin
import groovy.util.logging.Slf4j

@Slf4j
class SpringWebsocketGrailsPlugin extends Plugin {

	def grailsVersion = "6.0.0 > *"
	def title = "Spring WebSocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring WebSocket Plugin"
	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [url: "https://github.com/zyro23/grails-spring-websocket"]

	def watchedResources = "file:./grails-app/websockets/**/*WebSocket.groovy"
	def profiles = ["web"]
	def loadAfter = ["hibernate3", "hibernate4", "hibernate5", "services"]

	@Override
	Closure doWithSpring() {
		return {
			for (websocket in grailsApplication.getArtefacts(DefaultGrailsWebSocketClass.ARTEFACT_TYPE)) {
				log.debug "configuring webSocket ${websocket.propertyName}"
				"${websocket.propertyName}"(websocket.clazz) { bean ->
					bean.autowire = "byName"
				}
			}
			webSocketConfig DefaultWebSocketConfig
		}
	}

}
