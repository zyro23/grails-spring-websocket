# Spring Websocket Grails Plugin

- - -
*The current state is still a work in progress with room for improvements.*  
*This is reflected by the plugin version which is still a milestone build (at the time of writing)*.

*The plugin is targeting Grails 2.4+. Lower versions will not work because they lack the mandatory Spring version 4.0+.*  
*Currently, only the Grails Tomcat Plugins (tomcat-7.0.52+/tomcat8-8.0.1.1+) are known to work with this plugin.*  
- - -

This plugin aims at making the websocket support introduced in Spring 4.0 available to Grails applications.

You can use the corresponding Spring docs/apis/samples as a reference.  
That is mentioned multiple times in this readme because there is everything explained in fine detail.

## Installation

To install the plugin into a Grails application add the following line to your `BuildConfig.groovy` plugins section:

	compile ":spring-websocket:1.0.0.M1"
	
If you are using the tomcat8 plugin (8.0.1.1+), thats it.  
If you are using the tomcat plugin (7.0.52+), you need to add the following `BuildConfig.groovy` settings to ensure proper functionality:

	grails.tomcat.nio = true
	grails.tomcat.scan.enabled = true

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
	protected String hello() {
		return "hello from controller!"
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
					client.send("/app/hello", {}, "");
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
If you prefer the resources plugin instead of the asset-pipeline plugin, you can use the <code>spring-websocket</code> resources module - it includes sock.js and stomp.js:
```
<r:require module="spring-websocket" />
```

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

If the default values are fine for your application, you are good to go. No configuration required then.

The following configuration options are available (e.g. by adding some or all of them to your `Config.groovy`):

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.dispatcherServlet.additionalMappings</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>Collection&lt;String&gt;</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>["/*"]</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			By default, the <code>GrailsDispatcherServlet</code> is mapped to <code>*.dispatch</code>.<br />
			Because the sock.js support in Spring is not using a separate servlet but additional handlers for the <code>DispatcherServlet</code>, the relevant endpoints have to be covered by the servlet-mapping.<br />
			If the default value is too generic for your application, use this setting to narrow the servlet mappings down.<br />
			Usually, you will want to have at least your stomp endpoints covered.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.applicationDestinationPrefixes</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>Collection&lt;String&gt;</code></td>
	</tr>
	<tr>
		<td>Default</td><td><code>["/app"]</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Prefixes to filter destinations targeting application annotated methods.<br />
			Annotations should not contain the destination prefix.<br />
			E.g. with the default value, this means if your js client sends to <code>/app/foo/bar</code>, your controller annotation should look like <code>@MessageMapping("/foo/bar")</code>.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.userDestinationPrefix</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>String</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>"/user/"</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			The Prefix to identify user destinations.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.brokerPrefixes</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>Collection&lt;String&gt;</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>["/queue", "/topic"]</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Prefixes to filter destinations targeting the broker.<br />
			This setting affects the direction server --&gt; client.<br />
			E.g. with the default value, the broker would process a message to <code>/topic/foo</code> but not one to <code>/unknown/prefix/foo</code>.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.enabled</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>boolean</code> (groovy truth)</td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>false</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			If enabled, use a "real" stomp relay like RabbitMQ or ActiveMQ (with their corresponding stomp components active).<br />
			If not (default), a simple Map-based broker implementation will be used.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.host</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>String</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>127.0.0.1</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Only relevant if <code>stompRelay.enabled = true</code>.<br />
			The host of the stomp relay (IP address or hostname).
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.port</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>int</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>61613</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Only relevant if <code>stompRelay.enabled = true</code>.<br />
			The port of the stomp relay.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.systemLogin</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>String</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>guest</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Only relevant if <code>stompRelay.enabled = true</code>.<br />
			The login of the stomp relay for the shared system connection.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.systemPasscode</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>String</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>guest</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Only relevant if <code>stompRelay.enabled = true</code>.<br />
			The passcode of the stomp relay for the shared system connection.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.clientLogin</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>String</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>guest</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Only relevant if <code>stompRelay.enabled = true</code>.<br />
			The login of the stomp relay for the client connections.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.messageBroker.stompRelay.clientPasscode</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>String</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>guest</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Only relevant if <code>stompRelay.enabled = true</code>.<br />
			The passcode of the stomp relay for the client connections.
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.stompEndpoints</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>Collection&lt;Collection&lt;String&gt;&gt;</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>[["/stomp"]]</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Expose a STOMP endpoint at the specified url path (or paths).<br />
			For every inner Collection, a stomp endpoint is registered with those url path(s).
			E.g. with the default value, one stomp endpoint is registered and listening at <code>/stomp</code>
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.clientInboundChannel.threadPoolSize</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>Range&lt;Integer&gt;</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>4..10</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Core to max thread pool size for the TaskExecutor of the client inbound channel (client --&gt; server)
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.clientOutboundChannel.threadPoolSize</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>Range&lt;Integer&gt;</code></td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>4..10</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Core to max thread pool size for the TaskExecutor of the client outbound channel (server --&gt; client)
		</td>
	</tr>
</table>

<table>
	<tr>
		<td>Key</td>
		<td><strong><code>grails.plugin.springwebsocket.useCustomConfig</code></strong></td>
	</tr>
	<tr>
		<td>Type</td>
		<td><code>boolean</code> (groovy truth)</td>
	</tr>
	<tr>
		<td>Default</td>
		<td><code>false</code></td>
	</tr>
	<tr>
		<td>Description</td>
		<td>
			Set this to <code>true</code> if you want to take full control and responsibility for the spring websocket configuration.<br />
			Then, all other config options above will have <strong>no</strong> effect.<br />
			Neither the <code>WebSocketConfig</code> nor the <code>GrailsSimpAnnotationMethodMessageHandler</code> will be exposed to the application.
		</td>
	</tr>
</table>

If you need more sophisticated configuration options, currently the way to go would be using the <code>useCustomConfig</code> setting and heading over to the Spring docs/apis/samples covering the configuration of websockets/messaging.  
You can of course use the plugin's `WebSocketConfig` for orientation. It uses `@EnableWebSocketMessageBroker` and implements `WebSocketMessageBrokerConfigurer`.
But for bigger config adjustments, it is likely you end up extending Spring's `WebSocketMessageBrokerConfigurationSupport`. 

Future versions of this plugin may cover more configuration options.