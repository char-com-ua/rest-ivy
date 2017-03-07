//import java.nio.file.Files
//import java.nio.file.Paths

try {
	def parms = request.getParameterMap().collectEntries{k,v-> [k,v[0]]}
	if(parms) {
		//def wrongParms = parms.keySet() - ['org','name','rev','type','settings']
		//assert !(wrongParms) : "wrong parameters: $wrongParms"
		def rev = Repo.revisions(parms)
		response.setStatus(200)
		response.setContentType("text/plain")
		if(parms.format=='json'){
			println groovy.json.JsonOutput.toJson(rev)
		}else{
			println rev.join('\n')
		}
	}else{
		help()
	}
}catch(Throwable e){
	response.setStatus(500,"ERROR: $e")
	response.setContentType("text/plain")
	println "ERROR: $e"
	e.printStackTrace(out)
}

def help(){
	response.setStatus(400)
	response.setContentType("text/plain")
	println """
	returns verions of the module:
		org        organisation
		mod        module name
		format     (optional) how to format list: `json` to return as json array, otherwise text
		settings   (optional) the ivy settings ref name 
				   the file "./ivysettings-\${settings}.xml" must exist
	"""
}  

