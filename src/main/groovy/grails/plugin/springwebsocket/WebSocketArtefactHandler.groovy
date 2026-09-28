package grails.plugin.springwebsocket

import grails.core.ArtefactHandlerAdapter

class WebSocketArtefactHandler extends ArtefactHandlerAdapter {

    static final String PLUGIN_NAME = "springWebsocket"
    static final String TYPE = "WebSocket"

    WebSocketArtefactHandler() {
        super(TYPE, GrailsWebSocketClass.class, DefaultGrailsWebSocketClass.class, DefaultGrailsWebSocketClass.WEB_SOCKET)
    }

    @Override
    String getPluginName() {
        return PLUGIN_NAME
    }
}