package grails.plugin.springwebsocket

import grails.util.Environment

import org.codehaus.groovy.grails.commons.GrailsApplication

class ConfigUtils {

	public static final String DEFAULT_CONFIG_CLASS = "DefaultSpringWebsocketConfig"

	static ConfigObject getSpringWebsocketConfig(GrailsApplication grailsApplication) {
		def classLoader = new GroovyClassLoader(this.classLoader)
		def defaultConfigClass = classLoader.loadClass DEFAULT_CONFIG_CLASS
		def slurper = new ConfigSlurper(Environment.current.name)
		def defaultConfig = slurper.parse(defaultConfigClass).grails.plugin.springwebsocket
		def customConfig = grailsApplication.config.grails?.plugin?.springwebsocket
		def config = customConfig ? defaultConfig.merge(customConfig) : defaultConfig
		return config
	}

}
