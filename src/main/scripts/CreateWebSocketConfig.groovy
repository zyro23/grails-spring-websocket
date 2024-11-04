def model = model(args[0] ?: "WebSocketConfig")

render(
        template: "grails/plugin/springwebsocket/WebSocketConfig.groovy",
        destination: "src/main/groovy/${model.packagePath}/${model.className}.groovy",
        model: [model: model]
)