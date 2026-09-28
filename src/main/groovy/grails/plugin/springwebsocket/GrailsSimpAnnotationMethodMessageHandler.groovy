package grails.plugin.springwebsocket

import grails.artefact.Controller
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.converter.CompositeMessageConverter
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler

@CompileStatic
class GrailsSimpAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

    GrailsSimpAnnotationMethodMessageHandler(
            SubscribableChannel clientInboundChannel,
            MessageChannel clientOutboundChannel,
            SimpMessageSendingOperations brokerTemplate) {
        super(clientInboundChannel, clientOutboundChannel, brokerTemplate)
    }

    @Autowired
    void setMessageConverter(CompositeMessageConverter brokerMessageConverter) {
        super.setMessageConverter(brokerMessageConverter)
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return Controller.isAssignableFrom(beanType)
    }
}
