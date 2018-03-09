package grails.plugin.springwebsocket

import grails.core.ArtefactHandlerAdapter

/**
 * Grails Artefact Handler for new Conventional WebSocket Class
 *
 * @author David Estes
 */
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