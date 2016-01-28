package grails.plugin.springwebsocket

import grails.plugins.Plugin

class SpringWebsocketGrailsPlugin extends Plugin {

	def grailsVersion = "3.1.0 > *"
	def title = "Spring Websocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring Websocket Plugin"
	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [url: "https://github.com/zyro23/grails-spring-websocket"]

	@Override
	Closure doWithSpring() {
		return {
			webSocketConfig DefaultWebSocketConfig
		}
	}
	
}
