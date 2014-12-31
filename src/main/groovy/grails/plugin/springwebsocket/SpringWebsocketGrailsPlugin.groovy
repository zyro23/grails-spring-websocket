package grails.plugin.springwebsocket

import grails.core.GrailsApplication
import grails.plugins.Plugin

import org.springframework.context.ApplicationContext

class SpringWebsocketGrailsPlugin extends Plugin {

	def version = "2.0.0.BUILD-SNAPSHOT"
	def grailsVersion = "3.0.0 > *"
	def title = "Spring Websocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring Websocket Plugin"
	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [url: "https://github.com/zyro23/grails-spring-websocket"]

	@Override
	Closure doWithSpring() {
		return { ->
			webSocketConfig DefaultWebSocketConfig
		}
	}
	
}
