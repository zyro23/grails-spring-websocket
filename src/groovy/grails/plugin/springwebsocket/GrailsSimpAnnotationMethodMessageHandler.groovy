package grails.plugin.springwebsocket

import groovy.transform.CompileStatic

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler

@CompileStatic
class GrailsSimpAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

	@Autowired
	GrailsApplication grailsApplication
	
	GrailsSimpAnnotationMethodMessageHandler(SubscribableChannel clientInboundChannel,
			MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {
		super(clientInboundChannel, clientOutboundChannel, brokerTemplate)
	}
	
	@Autowired
	@Override
	@Qualifier("brokerMessageConverter")
	void setMessageConverter(MessageConverter messageConverter) {
		super.setMessageConverter messageConverter
	}
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return grailsApplication.isArtefactOfType(ControllerArtefactHandler.TYPE, beanType)
	}
	
}
