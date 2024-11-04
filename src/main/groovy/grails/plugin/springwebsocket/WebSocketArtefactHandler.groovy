package grails.plugin.springwebsocket

import grails.core.ArtefactHandlerAdapter

class WebSocketArtefactHandler extends ArtefactHandlerAdapter {

    static final String PLUGIN_NAME = "springWebsocket"

    WebSocketArtefactHandler() {
        super(DefaultGrailsWebSocketClass.ARTEFACT_TYPE, GrailsWebSocketClass.class, DefaultGrailsWebSocketClass.class, DefaultGrailsWebSocketClass.ARTEFACT_TYPE)
    }

    @Override
    String getPluginName() {
        return PLUGIN_NAME
    }
}