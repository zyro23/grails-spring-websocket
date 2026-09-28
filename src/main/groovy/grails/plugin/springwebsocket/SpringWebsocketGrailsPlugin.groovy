package grails.plugin.springwebsocket

import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.BeanRegistrar
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor

@Slf4j
class SpringWebsocketGrailsPlugin extends Plugin {

    def grailsVersion = "8.0.0 > *"
    def title = "Spring WebSocket Plugin"
    def author = "zyro"
    def authorEmail = ""
    def description = "Spring WebSocket Plugin"
    def documentation = "https://github.com/zyro23/grails-spring-websocket"
    def issueManagement = [system: "GitHub", url: "https://github.com/zyro23/grails-spring-websocket/issues"]
    def scm = [url: "https://github.com/zyro23/grails-spring-websocket"]

    def watchedResources = "file:./grails-app/websockets/**/*WebSocket.groovy"
    def profiles = ["web"]
    def loadAfter = ["hibernate5", "services"]

    @Override
    BeanRegistrar beanRegistrar() {
        return { registry, env ->
            registry.registerBean(BeanDefinitionRegistryPostProcessor) { spec ->
                spec.supplier { supplier ->
                    return { beanDefinitionRegistry ->
                        for (webSocketClass in grailsApplication.getArtefacts(WebSocketArtefactHandler.TYPE)) {
                            ((AbstractBeanDefinition) beanDefinitionRegistry.getBeanDefinition(webSocketClass.propertyName))
                                    .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME)
                        }
                    } as BeanDefinitionRegistryPostProcessor
                }
            }
            for (webSocketClass in grailsApplication.getArtefacts(WebSocketArtefactHandler.TYPE)) {
                log.debug("configuring webSocket ${webSocketClass.propertyName}")
                registry.registerBean(webSocketClass.propertyName, webSocketClass.clazz)
            }
        }
    }
}
