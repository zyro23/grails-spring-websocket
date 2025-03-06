package grails.plugin.springwebsocket

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@GroovyASTTransformationClass("grails.plugin.springwebsocket.WebSocketArtefactTypeTransformation")
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@interface WebSocketArtefact {}