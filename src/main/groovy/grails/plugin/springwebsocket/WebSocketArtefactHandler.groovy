/*
 * Copyright (c) 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugin.springwebsocket

import grails.core.ArtefactHandlerAdapter
import org.codehaus.groovy.ast.ClassNode
import org.grails.compiler.injection.GrailsASTUtils
import org.springframework.util.ReflectionUtils
import groovy.transform.CompileStatic
import java.lang.reflect.Method
import java.util.regex.Pattern

import static org.grails.io.support.GrailsResourceUtils.GRAILS_APP_DIR
import static org.grails.io.support.GrailsResourceUtils.REGEX_FILE_SEPARATOR

/**
 * Grails Artefact Handler for new Conventional Websocket Class
 *
 * @author David Estes
 */
@CompileStatic
public class WebSocketArtefactHandler extends ArtefactHandlerAdapter {

    static final String TYPE = "Websocket"
    public static Pattern WEBSOCKET_PATH_PATTERN = Pattern.compile(".+" + REGEX_FILE_SEPARATOR + GRAILS_APP_DIR + REGEX_FILE_SEPARATOR + "websockets" + REGEX_FILE_SEPARATOR + "(.+)\\.(groovy)");

    public WebSocketArtefactHandler() {
        super(TYPE, GrailsWebsocketClass.class, DefaultGrailsWebsocketClass.class, TYPE)
    }

    //DEBUGGING USAGE FOR NOW
    // boolean isArtefact(ClassNode classNode) {
    //     boolean result = super.isArtefact(classNode)

    //     if(result == true ) {
    //         println "Artefact detected for ${classNode}"
    //     }
    //     return result
    // }

    // @Override
    // protected boolean isArtefactResource(Resource resource) throws IOException {
    //      try {
            
    //         String file = r.getURL().getFile();
    //         return super.isArtefactResource(resource) && WEBSOCKET_PATH_PATTERN.matcher(file).find()
    //     }
    //     catch (IOException e) {
    //         return false;
    //     }
    // }
}