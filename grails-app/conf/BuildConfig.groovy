grails.project.work.dir = "target"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn"
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
		def springExcludes = {
			excludes "spring-beans", "spring-context", "spring-core", "spring-web", "spring-webmvc"
		}

		compile "org.springframework:spring-messaging:4.1.5.RELEASE", springExcludes
		compile "org.springframework:spring-websocket:4.1.5.RELEASE", springExcludes
		compile "com.fasterxml.jackson.core:jackson-databind:2.4.3"
    }

    plugins {
        build(":release:3.1.0",
              ":rest-client-builder:2.1.0") {
            export = false
        }
    }
}
