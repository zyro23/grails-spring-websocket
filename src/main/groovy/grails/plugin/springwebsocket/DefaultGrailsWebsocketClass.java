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

package grails.plugin.springwebsocket;

import grails.util.GrailsClassUtils;
import org.grails.core.AbstractInjectableGrailsClass;
import groovy.lang.Closure;
import org.grails.core.AbstractGrailsClass;
import java.util.HashMap;
import java.util.Map;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Grails artifact class which represents a Websocket Endpoint for STOMP.
 *
 * @author David Estes
 */
public class DefaultGrailsWebsocketClass extends AbstractInjectableGrailsClass implements GrailsWebsocketClass {

    public static final String WEBSOCKET = "Websocket";


    public DefaultGrailsWebsocketClass(Class clazz) {
        super(clazz, WEBSOCKET);
    }


    public void send(String destination, Object payload) throws MessagingException {
        SimpMessagingTemplate brokerMessagingTemplate = (SimpMessagingTemplate)(getApplication().getMainContext().getBean("brokerMessagingTemplate"));
        brokerMessagingTemplate.convertAndSend(destination, payload);
    }

   
}