// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

/*
grails {
	plugin
		springwebsocket {
			useCustomConfig = false
			dispatcherServlet.additionalMappings = ["/foo/*", "/bar/*", "/bla/*", "/hah/*"]
			clientInboundChannel.threadPoolSize = 4..10
			clientOutboundChannel.threadPoolSize = 4..10
			messageBroker {
				applicationDestinationPrefixes = ["/foo", "/bar"]
				userDestinationPrefix = "/user/"
				brokerPrefixes = ["/foo", "/bar"]
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
			stompEndpoints = [
				["/foo", "/bar"],
				["/bla", "/hah"]
			]
		}
	}
}
*/