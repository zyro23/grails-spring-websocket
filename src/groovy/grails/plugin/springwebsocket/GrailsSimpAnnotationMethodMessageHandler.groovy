package grails.plugin.springwebsocket

import groovy.transform.CompileStatic

import javax.annotation.Resource

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.handler.SimpAnnotationMethodMessageHandler

@CompileStatic
class GrailsSimpAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

	@Resource
	private GrailsApplication grailsApplication
	
	GrailsSimpAnnotationMethodMessageHandler(SimpMessageSendingOperations brokerTemplate, MessageChannel webSocketResponseChannel) {
		super(brokerTemplate, webSocketResponseChannel)
	}
	
	@Override
	protected boolean isHandler(Class<?> beanType) {
		if (super.isHandler(beanType)) {
			return true
		}
		boolean grailsHandler = grailsApplication.isArtefactOfType(ControllerArtefactHandler.TYPE, beanType)
		return grailsHandler
	}
	
}
