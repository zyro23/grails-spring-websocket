package grails.plugin.springwebsocket

import grails.plugins.Plugin
import groovy.util.logging.Slf4j

@Slf4j
class SpringWebsocketGrailsPlugin extends Plugin {

	def grailsVersion = "3.1.0 > *"
	def title = "Spring Websocket Plugin"
	def author = "zyro"
	def authorEmail = ""
	def description = "Spring Websocket Plugin"
	def documentation = "https://github.com/zyro23/grails-spring-websocket"
	def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
	def scm = [url: "https://github.com/zyro23/grails-spring-websocket"]

	def watchedResources = "file:./grails-app/websockets/**/*Websocket.groovy"
	def profiles = ['web']
    List loadAfter = ['hibernate3', 'hibernate4', 'hibernate5', 'services']
    def artefacts = [ WebSocketArtefactHandler ]

	@Override
	Closure doWithSpring() {
		return {
			for (websocket in grailsApplication.getArtefacts(WebSocketArtefactHandler.TYPE)) {
            	log.debug "Configuring websocket endpoint $websocket.propertyName"
        		"${websocket.propertyName}"(websocket.getClazz()) { bean ->
        			bean.scope = 'singleton'
                    bean.autowire =  "byName"
        		}
            }
			webSocketConfig DefaultWebSocketConfig
		}
	}
	
}
