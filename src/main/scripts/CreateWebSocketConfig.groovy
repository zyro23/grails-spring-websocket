def packageName, className
def parts = args[0]?.tokenize "."
if (!parts) {
	packageName = defaultPackage
	className = "WebSocketConfig"
} else if (parts.size() == 1) {
	packageName = defaultPackage
	className = parts[0]
} else {
	packageName = parts[0..-2].join "."
}
def destination = packageName.tokenize(".").join("/") + "/" + className + ".groovy"
render(
	template: "grails/plugin/springwebsocket/WebSocketConfig.groovy",
	destination: "src/main/groovy/${destination}",
	model: [
		packageName: packageName,
		className: className
	]
)