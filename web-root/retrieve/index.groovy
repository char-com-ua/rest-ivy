/**/
try {
	FilteredStream.filterStart()
	def parms = request.getParameterMap().collectEntries{k,v-> [k,v[0]]}
	if(parms) {
		//def wrongParms = parms.keySet() - ['org','name','rev','type','settings']
		//assert !(wrongParms) : "wrong parameters: $wrongParms"
		def paths = Repo.resolve(parms)
		if(paths==null || paths.size()==0)throw new Exception("artefact not found for $parms")
		if(paths.size()>1)throw new Exception("found more then one artefact for $parms: $paths")
		// http://api.dpml.net/ant/1.7.0/org/apache/tools/ant/types/resources/FileResource.html
		response.setStatus(200)
		response.setContentType( URLConnection.guessContentTypeFromName(paths[0].getName()) ?: "application/octet-stream" )
		response.setContentLength( (int)paths[0].getSize() ) //TODO:int???
		response.setHeader("Content-Disposition", "attachment; filename=\"" + paths[0].getName() + "\"");
		response.setHeader("x-file-name", paths[0].getName());
		response.setHeader("x-last-modified", new java.sql.Timestamp( paths[0].getLastModified() ).toString());
		response.setHeader("Last-Modified", Dates.httpFormat( paths[0].getLastModified() ) );
		//write content into output stream
		def stream = response.getOutputStream()
		stream << paths[0].getInputStream()
		stream.flush()
		stream.close()
	}else{
		help()
	}
}catch(Throwable e){
	response.setStatus(500,"ERROR: $e")
	response.setContentType("text/plain")
	println "ERROR: $e"
	println FilteredStream.filterEnd()
	e.printStackTrace(out)
}finally{
	FilteredStream.filterEnd()
}

def help(){
	response.setStatus(400)
	response.setContentType("text/plain")
	println """
	retrieve one artefact from the repository
	valid parameters:
		org        organisation
		mod        module name
		rev        version/revision
		type       comma separated list of artifact types to accept in the path, * for all. [jar|zip|...]
		conf       (optional) the configuration of the published artifact 
		regex      (optional) the regular expression to filter resolved file names
		settings   (optional) the ivy settings ref name 
				   the file "./ivysettings-\${settings}.xml" must exist
	"""
}  

