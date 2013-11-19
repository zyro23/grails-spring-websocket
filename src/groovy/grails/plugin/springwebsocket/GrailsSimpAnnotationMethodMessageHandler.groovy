package grails.plugin.springwebsocket

import groovy.transform.CompileStatic

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.handler.SimpAnnotationMethodMessageHandler
import org.springframework.messaging.support.converter.MessageConverter

@CompileStatic
class GrailsSimpAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

	@Resource
	GrailsApplication grailsApplication
	@Resource
	SubscribableChannel webSocketRequestChannel
	
	GrailsSimpAnnotationMethodMessageHandler(SimpMessageSendingOperations brokerTemplate, MessageChannel webSocketResponseChannel) {
		super(brokerTemplate, webSocketResponseChannel)
	}
	
	@Override
	@Resource(name = "simpMessageConverter")
	void setMessageConverter(MessageConverter messageConverter) {
		super.setMessageConverter messageConverter
	}
	
	
	@Override
	void afterPropertiesSet() {
		webSocketRequestChannel.subscribe this
		super.afterPropertiesSet()
	}
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return grailsApplication.isArtefactOfType(ControllerArtefactHandler.TYPE, beanType)
	}
	
}
