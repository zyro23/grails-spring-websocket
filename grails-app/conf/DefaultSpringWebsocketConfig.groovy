grails {
	plugin {
		springwebsocket {
			useCustomConfig = false
			dispatcherServlet.additionalMappings = ["/stomp/*"]
			clientInboundChannel.threadPoolSize = 4..10
			clientOutboundChannel.threadPoolSize = 4..10
			messageBroker {
				applicationDestinationPrefixes = ["/app"]
				userDestinationPrefix = "/user/"
				brokerPrefixes = ["/queue", "/topic"]
				stompRelay {
					enabled = false
					host = "127.0.0.1"
					port = 61613
					systemLogin = "guest"
					systemPasscode = "guest"
					clientLogin = "guest"
					clientPasscode = "guest"
				}
			}
			stompEndpoints = [["/stomp"]]
		}
	}
}
