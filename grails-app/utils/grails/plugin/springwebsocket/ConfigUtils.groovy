package grails.plugin.springwebsocket

import grails.util.Environment
import grails.util.Holders

class ConfigUtils {

	static final String DEFAULT_CONFIG_CLASS = "DefaultSpringWebsocketConfig"
	
	static ConfigObject getSpringWebsocketConfig() {
		def classLoader = new GroovyClassLoader(ConfigUtils.class.classLoader)
		def defaultConfigClass = classLoader.loadClass DEFAULT_CONFIG_CLASS
		def slurper = new ConfigSlurper(Environment.current.name)
		def defaultConfig = slurper.parse(defaultConfigClass).grails.plugin.springwebsocket
		def grailsApplication = Holders.grailsApplication
		def customConfig = grailsApplication.config.grails?.plugin?.springwebsocket
		def config = customConfig ? defaultConfig.merge(customConfig) : defaultConfig
		return config
	}
	
}
