package grails.plugin.springwebsocket;

import org.grails.core.AbstractInjectableGrailsClass;

public class DefaultGrailsWebSocketClass extends AbstractInjectableGrailsClass {

    public static final String ARTEFACT_TYPE = "WebSocket";

    public DefaultGrailsWebSocketClass(Class clazz) {
        super(clazz, ARTEFACT_TYPE);
    }

}