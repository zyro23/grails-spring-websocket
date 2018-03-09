package grails.plugin.springwebsocket

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grails.compiler.injection.ArtefactTypeAstTransformation

@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class WebSocketArtefactTypeTransformation extends ArtefactTypeAstTransformation {

	@Override
	protected String resolveArtefactType(SourceUnit sourceUnit, AnnotationNode annotationNode, ClassNode classNode) {
		return DefaultGrailsWebSocketClass.ARTEFACT_TYPE
	}

	@Override
	protected Class getAnnotationTypeClass() {
		return WebSocket
	}

}