package grails.plugin.springwebsocket

import grails.compiler.traits.TraitInjector
import groovy.transform.CompileStatic

@CompileStatic
class WebSocketTraitInjector implements TraitInjector {

    @Override
    Class getTrait() {
        return WebSocket
    }

    @Override
    String[] getArtefactTypes() {
        return [DefaultGrailsWebSocketClass.ARTEFACT_TYPE] as String[]
    }
}