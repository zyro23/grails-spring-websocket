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

	compile "org.grails.plugins:spring-websocket:2.0.0.BUILD-SNAPSHOT"
	
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

Unless you want your handler method to be exposed as controller action, it is important that you define the annotated method as private or protected.

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
class ExampleService {
	
	def brokerMessagingTemplate
	
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
