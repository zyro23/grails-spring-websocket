def model = model(args[0] ? (args[0].endsWith('WebSocket') ? args[0] : "${args[0]}WebSocket") : "WebSocket")

render(
        template: "grails/plugin/springwebsocket/WebSocket.groovy",
        destination: "grails-app/websockets/${model.packagePath}/${model.className}.groovy",
        model: [model: model]
)