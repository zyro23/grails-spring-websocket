package grails.plugin.springwebsocket

import groovy.transform.CompileStatic

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler

@CompileStatic
class GrailsSimpAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

	@Resource
	GrailsApplication grailsApplication
	
	GrailsSimpAnnotationMethodMessageHandler(SubscribableChannel clientInboundChannel,
			MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {
		super(clientInboundChannel, clientOutboundChannel, brokerTemplate)
	}
	
	@Override
	@Resource(name = "brokerMessageConverter")
	void setMessageConverter(MessageConverter messageConverter) {
		super.setMessageConverter messageConverter
	}
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return grailsApplication.isArtefactOfType(ControllerArtefactHandler.TYPE, beanType)
	}
	
}
