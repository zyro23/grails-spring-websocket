# Spring Websocket Grails Plugin

This plugin aims at making the websocket support introduced in Spring 4.0 available to Grails applications.

You can use the corresponding Spring docs/apis/samples as a reference.  
That is mentioned multiple times in this readme because there is everything explained in fine detail.

Grails version requirements:
<table>
	<tr>
		<th>grails-spring-websocket</th>
		<th>Grails</th>
	<tr>
	<tr>
		<td>2.0.x</td>
		<td>3.0.0+</td>
	</tr>
</table>

## Installation

To install the plugin into a Grails application add the following line to your `build.gradle` dependencies section:

	compile "org.grails.plugins:grails-spring-websocket:2.0.0.M3"
	
Currently, the plugin is only published to bintray, so make sure you got the `jcenter()` repository added in your `build.gradle` repositories section.
	
## Usage

The plugin makes the Spring websocket/messaging web-mvc controller annotations useable in Grails controllers, too.  

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
		return "hello from controller, ${world}!"
	}
	
}
```

Unless you want your handler method to be exposed as controller action, you should define the annotated method as protected or add an additional annotation `@grails.web.controllers.ControllerMethod`.

Spring `@Controller` beans can be used as well.

### Client-side (sock.js / stomp.js)

*/grails-app/views/example/index.gsp*:

```gsp
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		
		<asset:javascript src="jquery" />
		<asset:javascript src="spring-websocket" />
		
		<script type="text/javascript">
		 	$(function() { 
				var socket = new SockJS("${createLink(uri: '/stomp')}");
				var client = Stomp.over(socket);
			
				client.connect({}, function() {
					client.subscribe("/topic/hello", function(message) {
						$("#helloDiv").append(message.body);
					});
				});
			
				$("#helloButton").click(function() {
					client.send("/app/hello", {}, JSON.stringify("world"));
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
For this example, i added a button allowing to trigger a send/receive roundtrip.

While this example shows jquery used with the asset-pipeline plugin, the use of jquery is **not required**.

### Service (brokerMessagingTemplate bean)

You can also inject and use the <code>brokerMessagingTemplate</code> bean to send messages directly, e.g. from a service.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
package example

import org.springframework.messaging.simp.SimpMessagingTemplate

class ExampleService {
	
	SimpMessagingTemplate brokerMessagingTemplate
	
	void hello() {
		brokerMessagingTemplate.convertAndSend "/topic/hello", "hello from service!"
	}
	
}
```

## Configuration

Configuration relies on Spring java config, especially `@EnableWebSocketMessageBroker`. 

### Default Configuration

By default, a configuration bean named `webSocketConfig` of type `grails.plugin.springwebsocket.DefaultWebSocketConfig` is used.

* An in-memory `Map`-based message broker implementation is used.
* The prefixes for broker destinations ("outgoing messages") are: `/queue` or `/topic`
* The prefix for application destinations ("incoming messages") is: `/app`
* The stomp-endpoint URI is: `/stomp` 
* A `SimpAnnotationMethodMessageHandler` bean is defined to allow Grails controller methods to act as message handlers

If the default values are fine for your application, you are good to go. No configuration required then.

### Custom Configuration

If you want to customize the defaults, you should override the config bean providing your own bean named `webSocketConfig`.

As starting point, you can create a config class/bean very similar to the default config with:

	grails create-web-socket-config my.package.name.MyClassName

That class will be placed under `src/main/groovy` and needs to be registered as a Spring bean named `webSocketConfig`, e.g. like this:

*/grails-app/conf/spring/resources.groovy*:

```groovy
beans = {
	webSocketConfig my.package.name.MyClassName
}
```

From there, check the Spring docs/apis/samples for the available configuration options.

## User Destinations

To send messages to specific users, you can (among other ways) annotate message handler methods with `@SendToUser` and/or use the `SimpMessagingTemplate.convertAndSendToUser(...)` methods.

*/grails-app/controllers/example/ExampleController.groovy*:

```groovy
class ExampleController {
	
	SimpMessagingTemplate brokerMessagingTemplate
	
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
class ExampleService {
	
	SimpMessagingTemplate brokerMessagingTemplate
	
	void hello() {
		brokerMessagingTemplate.convertAndSendToUser("myTargetUsername", "/queue/hello", "hello, target user!")
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
	compile "org.springframework.security:spring-security-config:4.0.0.RC2"
	compile "org.springframework.security:spring-security-messaging:4.0.0.RC2"
	compile "org.springframework.security:spring-security-web:4.0.0.RC2"
}
```

*/src/main/groovy/example/WebSecurityConfig.groovy*:

```groovy
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic()
		http.authorizeRequests().anyRequest().authenticated()
	}

	@Autowired
	void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("password").roles("USER")
	}
	
}
```

Spring security will by default enable CSRF protection for websocket messages.  
To include the required token in the stomp headers, your js code could look like this:

*/grails-app/views/example/index.gsp*:

```javascript
$(function() { 
	var url = "${createLink(uri: '/stomp')}";
	var csrfHeaderName = "${request._csrf.headerName}";
	var csrfToken = "${request._csrf.token}";
	var socket = new SockJS(url);
	var client = Stomp.over(socket);
	var headers = {};
	headers[csrfHeaderName] = csrfToken;
	client.connect(headers, function() {
		// subscriptions etc. [...]
	});
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
	@PreAuthorize("hasRole('ROLE_USER')")
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

Note that you can still use Spring `@Controller` beans as message handlers which would obviously not require those additional `@ControllerMethod` annotations.

### Filtering messages

The following example shows how you can filter inbound messages by type and/or by destination pattern.

*/src/main/groovy/example/WebSecurityConfig.groovy*:

```groovy
@Configuration
class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	
	@Override
	void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages
			.nullDestMatcher().authenticated()
			.simpSubscribeDestMatchers("/user/queue/errors").permitAll()
			.simpDestMatchers("/app/**").hasRole("USER")
			.simpSubscribeDestMatchers("/user/**", "/topic/**").hasRole("USER")
			.simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
			.anyMessage().denyAll()
	}
	
}
```

## Event Handling

Starting with Grails 3, grails-plugin-events is a core plugin allowing to use the Reactor framework for event handling.

While there is no special event integration regarding websocket messaging (because it is not really necessary anymore), a service that handles application events can look like the follwing snippet. I am _not_ talking about Spring `ApplicationEvent`s here, but Reactor `Event`s.

*/grails-app/services/example/ExampleService.groovy*:

```groovy
@Consumer
class ExampleService {
	
	SimpMessagingTemplate brokerMessagingTemplate
	
	@Selector("myEvent")
	void hello(Event<String> event) {
		brokerMessagingTemplate.convertAndSend("/topic/myEventTopic", "myEvent: ${event.data}")
	}
	
}
```

Events can be fired/sent from all application artefacts/beans that implement the trait `Events`. Grails service beans do so by convention. Those beans also allow dynamic registration of event listeners. E.g.:

*/grails-app/services/example/ExampleService.groovy*:

```groovy
class ExampleService {
	
	void fireMyEvent() {
		notify "myEvent", "hello from myEvent!"
	}
	
}
```

*/grails-app/init/BootStrap.groovy*:

```groovy
class BootStrap implements Events {

	SimpMessagingTemplate brokerMessagingTemplate

	def init = {
		on("myEvent") { Event<String> event ->
			brokerMessagingTemplate.convertAndSend("/topic/myEventTopic", "myEvent: ${event.data}")
		}
	}

}
```

For further information check the Grails and/or Reactor docs. 
