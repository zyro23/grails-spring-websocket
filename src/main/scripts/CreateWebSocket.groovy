def model = model(args[0] ? (args[0].endsWith('Websocket') ? args[0] : "${args[0]}Websocket") : "Websocket")

render(
	template: "grails/plugin/springwebsocket/WebSocketClass.groovy",
	destination: "grails-app/websockets/${model.packagePath}/${model.className}.groovy",
	model: [model: model]
)