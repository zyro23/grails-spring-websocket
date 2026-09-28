package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel
import org.springframework.messaging.converter.CompositeMessageConverter
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler

@CompileStatic
class GrailsWebSocketAnnotationMethodMessageHandler extends SimpAnnotationMethodMessageHandler {

    GrailsWebSocketAnnotationMethodMessageHandler(
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
        return WebSocket.isAssignableFrom(beanType)
    }
}
