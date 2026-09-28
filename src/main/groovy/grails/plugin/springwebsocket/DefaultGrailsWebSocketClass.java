package grails.plugin.springwebsocket;

import org.grails.core.AbstractInjectableGrailsClass;

public class DefaultGrailsWebSocketClass extends AbstractInjectableGrailsClass {

    public static final String WEB_SOCKET = "WebSocket";

    public DefaultGrailsWebSocketClass(Class clazz) {
        super(clazz, WEB_SOCKET);
    }
}