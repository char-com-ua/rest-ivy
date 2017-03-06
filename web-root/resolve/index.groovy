//import java.nio.file.Files
//import java.nio.file.Paths

try {
	def parms = request.getParameterMap().collectEntries{k,v-> [k,v[0]]}
	if(parms) {
		//def wrongParms = parms.keySet() - ['org','name','rev','type','settings']
		//assert !(wrongParms) : "wrong parameters: $wrongParms"
		def paths = Repo.resolve(parms)
		//write content into output stream
		response.setContentType("text/plain")
		paths.each{
			println it.getName()
		}
	}else{
		help()
	}
}catch(Throwable e){
	response.setContentType("text/plain")
	response.setStatus(500,"ERROR: $e")
	println "ERROR: $e"
	e.printStackTrace(out)
}

def help(){
	response.setStatus(400)
	println """
	resolve artefacts from the repository by following parameters:
		org        organisation
		name       module name
		rev        version/revision
		type       comma separated list of artifact types to accept in the path, * for all. [jar|zip|...]
		settings   the ivy settings ref name. if used
				   the file "./ivysettings-\${settings}.xml" must exist
	"""
}  

