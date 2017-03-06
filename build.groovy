
//groovy
try {
	def warFile = "./rest-ivy.war"

	def groovyJar = new File(GroovyShell.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
	def groovyLib = groovyJar.getParentFile()
	println "groovy lib path = $groovyLib"
	
	def ant = new AntBuilder()
	
	ant.delete( file:warFile )
	ant.war(destfile:warFile){
		fileset ( dir:"web-root" )
		lib ( dir:groovyLib )
	}
	
	
	println "SUCCESS"
}catch(e){
	println "ERROR:"
	org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(e).printStackTrace(System.out)
	System.exit(1)
}

