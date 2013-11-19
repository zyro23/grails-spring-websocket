# Spring Websocket Grails Plugin

---
The current state is still a work in progress with much room for improvements.  
This is reflected by the plugin version which is still a BUILD-SNAPSHOT (at the time of writing).

Currently, only the Grails Tomcat-8 Plugin (RC5) is known to work with this plugin.  
But a websocket-supporting version of Jetty shoud basically do the job as well as a Tomcat-7 (7.0.47+) 
---

This plugin aims at making the websocket support introduced in Spring 4.0 available to Grails applications.

You can use the corresponding Spring docs/apis/samples as a reference.  
That is mentioned multiple times in this readme because there is everything explained in fine detail.

## Installation

To install the plugin into a Grails application add the following line to your `BuildConfig.groovy` plugins section:

	compile ":spring-websocket:1.0.BUILD-SNAPSHOT"

## Configuration

If the default values are fine for your application, you are good to go. No configuration required then.

**All config keys got to be prefixed with/nested in `grails.plugin.springwebsocket`**. This prefix is omitted in the table below.

The following configuration options are available (e.g. by adding some or all of them to your `Config.groovy`):

<table>
	<tr>
		<td><strong>Key</strong></td>
		<td><strong>Type</strong></td>
		<td><strong>Default</strong></td>
		<td><strong>Description</strong></td>
	</tr>
	<tr>
		<td><code>dispatcherServlet.additionalMappings</code></td>
		<td><code>Collection&lt;String&gt;</code></td>
		<td><code>["/*"]</code></td>
		<td>
			By Default, the <code>GrailsDispatcherServlet</code> is mapped to <code>*.dispatch</code>.<br />
			Because the sock.js support in Spring is not using a separate servlet but additional handlers for the <code>DispatcherServlet</code>, the relevant endpoints have to be covered by the servlet-mapping.<br />
			If the default value is too generic for your application, use this setting to narrow the servlet mappings down.<br />
			Usually, you will want to have at least your STOMP endpoints covered.
		</td>
	</tr>
	<tr>
		<td><code>messageBroker.applicationDestinationPrefixes</code></td>
		<td><code>Collection&lt;String&gt;</code></td>
		<td><code>["/app"]</code></td>
		<td>
			Prefixes to filter destinations targeting application annotated methods.<br />
			Annotations should not contain the destination prefix.<br />
			E.g. with the default value, this means if your js client sends to <code>/app/foo/bar</code>, your controller annotation should look like <code>@MessageMapping("/foo/bar")</code>.
		</td>
	</tr>
	<tr>
		<td><code>messageBroker.brokerPrefixes</code></td>
		<td><code>Collection&lt;String&gt;</code></td>
		<td><code>["/queue", "/topic"]</code></td>
		<td>
			Prefixes to filter destinations targeting the broker.<br />
			This setting affects the direction server --> client.<br />
			E.g. with the default value, the broker would process a message to <code>/topic/foo</code> but not one to <code>/unknown/prefix/foo</code>.
		</td>
	</tr>
	<tr>
		<td><code>stompEndpoints</code></td>
		<td><code>Collection&lt;Collection&lt;String&gt;&gt;</code></td>
		<td><code>[["/stomp"]]</code></td>
		<td>
			Expose a STOMP endpoint at the specified url path (or paths).<br />
			For every inner Collection, a stomp endpoint is registered with those url path(s).
			E.g. with the default value, one stomp endpoint is registered and listening at <code>/stomp</code>
		</td>
	</tr>
	<tr>
		<td><code>useCustomConfig<code></td>
		<td><code>boolean</code> (groovy truth)</td>
		<td><code>false<code></td>
		<td>
			Set this to <code>true</code> if you want to take full control and responsibility for the spring websocket configuration.<br />
			Then, all other config options above will have <strong>no</strong> effect.<br />
			Neither the <code>WebSocketConfig</code> nor the <code>GrailsSimpAnnotationMethodMessageHandler</code> will be exposed to the application.
		</td>
	</tr>
</table>

If you need more sophisticated configuration options, e.g. with a "real" STOMP broker like ActiveMQ or RabbitMQ, currently the way to go would be using the <code>useCustomConfig</code> setting and heading over to the Spring docs/apis/samples covering the configuration of websockets/messaging.

## Usage

The plugin makes the Spring websockets/messaging web-mvc controller annotations useable in Grails controllers, too.  

I think basic usage is explained best by example code.
But: the code below is just some very minimal it-works proof.  
Check the Spring docs/apis/samples for more advanced use-cases, e.g. security and authentication (Spring Security integration).

### Controller (annotated handler method)

*/grails-app/controllers/example/ExampleController.groovy*:

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
	
Unless you want your handler method to be exposed as controller action, it is important that you define the annotated method as private or protected.

### Client-side (sock.js / stomp.js)

*/grails-app/views/example/index.gsp*:

	<!DOCTYPE html>
	<html>
		<head>
			<meta name="layout" content="main"/>
			
			<r:require modules="jquery, spring-websocket" />
			<r:script>
				var socket = new SockJS("${createLink(uri: '/stomp')}");
				var client = Stomp.over(socket);
			
				client.connect("anonymous", "secret", function() {
					client.subscribe("/topic/hello", function(message) {
						$("#helloDiv").append(message.body);
					});
				});
			
				$("#helloButton").click(function() {
					client.send("/app/hello", {}, "");
				});
			</r:script>
		</head>
		<body>
			<button id="helloButton">hello</button>
			<div id="helloDiv"></div>
		</body>
	</html>
	
The index view of the controller above. It connects to the message broker and subscribes to <code>/topic/hello</code>.  
For this example, i added a button allowing to trigger a send/receive roundtrip. the use of jquery is **not required**.

Dont be confused about the credentials shown in this snippet - they do not matter.  
Stomp supports other transports like tcp, too. There, such an authentication can be useful.  
A websocket connection as transport has to be upgraded from http(s) first. Thats where authentication/credentials/sessions are handled.

### Service (brokerMessagingTemplate bean)

You can also inject and use the <code>brokerMessagingTemplate</code> bean to send messages directly, e.g. from a service.

*/grails-app/services/example/ExampleService.groovy*:

	class ExampleService {
		
		def brokerMessagingTemplate
		
		void hello() {
			brokerMessagingTemplate.convertAndSend "/topic/hello", "hello from service!"
		}
		
	}