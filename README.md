# Spring Websocket Grails Plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.zyro23/grails-spring-websocket)](https://central.sonatype.com/artifact/io.github.zyro23/grails-spring-websocket)
[![CI](https://github.com/zyro23/grails-spring-websocket/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/zyro23/grails-spring-websocket/actions/workflows/ci.yml)

This plugin aims at making the websocket support introduced in Spring 4.0 available to Grails applications.

You can also use the corresponding Spring docs/apis/samples as a reference.

That is mentioned multiple times in this readme because there is everything explained in fine detail.

Version compatibility:
<table>
    <tr>
        <th>Plugin version</th>
        <th>Grails version</th>
    </tr>
    <tr>
        <td>io.github.zyro23:grails-spring-websocket:3.0.x</td>
        <td>8.0.0+</td>
    </tr>
    <tr>
        <td>io.github.zyro23:grails-spring-websocket:2.7.x</td>
        <td>7.0.0+</td>
    </tr>
    <tr>
        <td>io.github.zyro23:grails-spring-websocket:2.6.x</td>
        <td>6.0.0+</td>
    </tr>
</table>

## Installation

To install the plugin into a Grails application add the following line to your `build.gradle` dependencies section:
```groovy
implementation "io.github.zyro23:grails-spring-websocket:3.0.0"
```
Plugin releases are published to maven central.

### Snapshots

To install a `-SNAPSHOT` version, add the snapshot repository:

```groovy
repositories {
    maven {
        url = "https://central.sonatype.com/repository/maven-snapshots"
    }
}
```

And add the following line to your `build.gradle` dependencies section:

```groovy
implementation "io.github.zyro23:grails-spring-websocket:3.1.0-SNAPSHOT"
```

Plugin snapshots are published to the maven central snapshot repository which has an automatic cleanup policy (90 days).

## Usage

The plugin makes the Spring websocket/messaging web-mvc annotations usable in Grails, too.

Those annotations can be used in:
* Regular Grails controllers
* `WebSocket` Grails artefacts (`./grailsw create-web-socket my.package.name.MyWebSocket`)
* Spring `@Controller` beans

I think basic usage is explained best by example code.

But: the code below is just some very minimal it-works proof.

Check the Spring docs/apis/samples for more advanced use-cases, e.g. security and authentication.

### Controller (annotated handler method)

*/grails-app/controllers/example/ExampleController.groovy*:

```groovy
package example

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

class ExampleController {

    def index() {}

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    protected String hello(String world) {
        return "hello, ${world}!"
    }
}
```

Unless you want your handler method to be exposed as a Grails controller action, you should define the annotated method as protected or add an additional annotation `@grails.web.controllers.ControllerMethod`.

Alternatively, `WebSocket` Grails artefacts and/or Spring `@Controller` beans can be used as well, for example:

*/grails-app/websockets/example/ExampleWebSocket.groovy*:

```groovy
package example

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

class ExampleWebSocket {

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    String hello(String world) {
        return "hello, ${world}!"
    }
}
```

### Client-side (stomp.js)

*/grails-app/views/example/index.gsp*:

```html
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>

        <asset:javascript src="application" />
        <asset:javascript src="spring-websocket" />

        <script type="text/javascript">
            $(() => {
                var client = new StompJs.Client({
                    brokerURL: "ws${createLink(uri: '/stomp', absolute: true).replaceFirst('(?i)http', '')}",
                    onConnect: () => {
                        client.subscribe("/topic/hello", (message) => {
                            $("#helloDiv").append(message.body);
                        });
                    },
                });
                client.activate();
                $("#helloButton").click(() => {
                    client.publish({ destination: "/app/hello", body: "world" });
                });
            });
        </script>
    </head>
    <body>
        <button id="helloButton">hello</button>
        <div id="helloDiv"></div>
    </body>
</html>
```

This would be the index view of the controller above. The js connects to the message broker and subscribes to <code>/topic/hello</code>.

For this example, I added a button allowing to trigger a send/receive roundtrip.

While this example shows jquery used with the asset-pipeline plugin, the use of jquery is **not required**.

### Service (brokerMessagingTemplate bean)

To send messages directly, the `brokerMessagingTemplate` bean (of type `SimpMessagingTemplate`) can be used.

The plugin provides a `WebSocket` trait that autowires the `brokerMessagingTemplate` and delegates to it.

That `WebSocket` trait is automatically implemented by `WebSocket` artefacts, but you can implement it from other beans as well, e.g. from a service.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
package example

import grails.plugin.springwebsocket.WebSocket

class ExampleService implements WebSocket {

    void hello() {
        convertAndSend("/topic/hello", "hello from service!")
    }
}
```

Or, if you prefer, you can also inject and use the `brokerMessagingTemplate` bean directly.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
package example

import org.springframework.messaging.simp.SimpMessagingTemplate

class ExampleService {

    SimpMessagingTemplate brokerMessagingTemplate

    void hello() {
        brokerMessagingTemplate.convertAndSend("/topic/hello", "hello from service!")
    }
}
```

## Configuration

Configuration relies on Spring java config, especially `@EnableWebSocketMessageBroker`.

### Default Configuration

By default, `WebSocketAutoConfiguration` registers a `DefaultWebSocketConfig` (implementing `WebSocketMessageBrokerConfigurer`):

* An in-memory `Map`-based message broker implementation is used
* The prefixes for broker destinations ("outgoing messages") are: `/queue` or `/topic`
* The prefix for application destinations ("incoming messages") is: `/app`
* The stomp-endpoint URI is: `/stomp`
* A `GrailsSimpAnnotationMethodMessageHandler` bean is defined to allow Grails controller methods to act as message handlers
* A `GrailsWebSocketAnnotationMethodMessageHandler` bean is defined to allow Grails webSocket methods to act as message handlers

If the default values are fine for your application, you are good to go. No further configuration required then.

### Custom Configuration

> [!NOTE]
> Registering a custom config via the legacy grails spring bean dsl (`resources.groovy`) is not supported anymore.

The default configuration can be customized/overridden by providing a (`@Configuration`) class using `@EnableWebSocketMessageBroker`.

As a starting point, you can take a look at `DefaultWebSocketConfig` or you can create a config class/bean resembling the default config with:

    ./grailsw create-web-socket-config my.package.name.MyClassName

That class will be placed under `src/main/groovy` and needs to be registered as a Spring configuration bean.

That can be accomplished in different ways, depending on your project and preferences, e.g.:

* By making sure the class is in a package covered by `@ComponentScan`
* Or, by adding `@Import(MyClassName)` to your `Application` class

Check the Spring docs/apis/samples for the available configuration options.

### Full-Featured Broker

To use a full-featured (e.g. RabbitMQ, ActiveMQ, etc.) instead of the default simple broker, please refer to the Spring docs regarding configuration.
Additionally, add a dependency for TCP connection management.

```groovy
implementation "io.projectreactor.netty:reactor-netty"
```

## User Destinations

To send messages to specific users, you can (among other ways) annotate message handler methods with `@SendToUser` and/or use the `SimpMessagingTemplate.convertAndSendToUser(...)` methods.

*/grails-app/controllers/example/ExampleController.groovy*:

```groovy
class ExampleController {

    @MessageMapping("/hello")
    @SendToUser("/queue/hello")
    protected String hello(String world) {
        return "hello from controller, ${world}!"
    }
}
```

To receive messages for the above `/queue/hello` user destination, the js client would have to subscribe to `/user/queue/hello`.

If a user is not logged in, `@SendToUser` will still work and only the user who sent the ingoing message will receive the outgoing one returned by the method.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
class ExampleService implements WebSocket {

    void hello() {
        convertAndSendToUser("myTargetUsername", "/queue/hello", "hello, target user!")
    }
}
```

Again, to receive messages for the above `/queue/hello` user destination, the js client would have to subscribe to `/user/queue/hello`.

## Security

To secure websocket messaging, we can leverage the first-class websocket security support of Spring Security 4.0+.

Check the Spring Security docs and the Spring Guides to get a jump-start into the topic.

There is a variety of options how to build your solution, including:
* Securing message handler methods in a declarative fashion using annotations (e.g. `@PreAuthorize`)
* Securing message handler methods by using an `@AuthenticationPrincipal`-annotated argument.
* Filtering messages and subscriptions (e.g. with an `SecurityWebSocketMessageBrokerConfigurer`)

I will only show a short example of securing message handler methods with security annotations and filtering inbound messages. I hope you do not mind the lack of import statements in the following code snippets ;)

A working Spring Security setup is required. For the sake of brevity, here a super-minimalistic Spring Security dummy configuration:

*/build.gradle*:

```groovy
dependencies {
    implementation "org.springframework.security:spring-security-config"
    implementation "org.springframework.security:spring-security-messaging"
    implementation "org.springframework.security:spring-security-web"
}
```

*/src/main/groovy/example/WebSecurityConfig.groovy*:

```groovy
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build()
        return new InMemoryUserDetailsManager(userDetails)
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .anyRequest()
                        .authenticated())
                .build()
    }
}
```

Spring security will by default enable CSRF protection for websocket messages.

To include the required token in the stomp headers, your js code could look like this:

*/grails-app/views/example/index.gsp*:

```javascript
$(function() {
    var url = "ws${createLink(uri: '/stomp', absolute: true).replaceFirst('(?i)http', '')}"
    var csrfHeaderName = "${request._csrf.headerName}";
    var csrfToken = "${request._csrf.token}";
    var client = new StompJs.Client({
        brokerURL: url,
        connectHeaders: {
            [csrfHeaderName]: csrfToken
        },
        onConnect: () => {
            // subscriptions etc. [...]
        },
    });
    client.activate();
});
```

There are still embedded GSP GString expressions present, which means that snippet will only work in a GSP as-is. If you plan on extracting the js properly into an own js file (or similar), you will have to pass those values along.

### Securing Message Handler Methods

Securing message handler methods can be achieved with annotations in a declarative fashion.

The following example shows a Grails controller with a secured message handler method and an message exception handler method.

*/grails-app/controllers/example/ExampleController.groovy*:

```groovy
class ExampleController {

    @ControllerMethod
    @MessageMapping("/hello")
    @PreAuthorize("hasRole('USER')")
    @SendTo("/topic/hello")
    String hello(String world) {
        return "hello from secured controller, ${world}!"
    }
    
    @ControllerMethod
    @MessageExceptionHandler
    @SendToUser(value = "/queue/errors", broadcast = false)
    String handleException(Exception e) {
        return "caught ${e.message}"
    }
}
```

Besides the security handling itself, this snippet shows one important catch: if you want to secure Grails controller actions with `@PreAuthorize`, the secured method has to be public. However, as we still do not want the method to be exposed as a controller action but only as message handler, in this case the use of `@ControllerMethod` is required.  

If you use Grails `WebSocket` artefacts or Spring `@Controller` beans as message handlers, you do obviously not require those additional `@ControllerMethod` annotations.

### Filtering messages

The following example shows how you can filter inbound messages by type and/or by destination pattern.

*/src/main/groovy/example/WebSecurityConfig.groovy*:

```groovy
@Configuration
@EnableWebSocketSecurity
class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages
                .nullDestMatcher()
                .authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors")
                .permitAll()
                .simpDestMatchers("/app/**")
                .hasRole("USER")
                .simpSubscribeDestMatchers("/user/**", "/topic/**")
                .hasRole("USER")
                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE)
                .denyAll()
                .anyMessage()
                .denyAll()
                .build()
    }
}
```

## Event Handling

grails-events is a core plugin allowing to use an event bus abstraction/impl. for event handling.

While there is no special event integration regarding websocket messaging, a service that handles application events can look like the following snippet. I am _not_ talking about Spring `ApplicationEvent`s here, but Grails events.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
class ExampleService implements WebSocket {
    
    @Subscriber("myEvent")
    void hello(String event) {
        convertAndSend("/topic/myEventTopic", "myEvent: ${event}")
    }
}
```

Events can be fired/sent from all application artefacts/beans that implement the trait `EventPublisher`.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
class ExampleService implements EventPublisher {
    
    void fireMyEvent() {
        notify("myEvent", "hello from myEvent!")
    }
}
```

Dynamic registration of event listeners is supported by the trait `EventBusAware`. E.g.:

*/grails-app/init/BootStrap.groovy*:

```groovy
class BootStrap implements EventBusAware, WebSocket {

    def init = {
        eventBus.subscribe("myEvent") { String event ->
            convertAndSend("/topic/myEventTopic", "myEvent: ${event}")
        }
    }
}
```

For further information check the Grails async docs.

## Misc

### Startup performance

Scanning Grails controllers for message handler methods can impact application startup time if you have many controllers.

One way around this is to put your message handler methods into Grails `WebSocket` artefacts instead of Grails controllers and then use a custom websocket config class without the `GrailsSimpAnnotationMethodMessageHandler`.
