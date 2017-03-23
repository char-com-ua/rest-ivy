/**/

try {
	def parms = request.getParameterMap().collectEntries{k,v-> [k,v[0]]}
	assert Props.require("repo.root")
	File repoRoot = new File( Props."repo.root" )
	def path = request.getPathInfo()
	
	if(path){
		File repoPath = new File(repoRoot, path)
		if( repoPath.exists() ){
			if(repoPath.isDirectory()){
				if(path.endsWith("/")){
					def listFiles = repoPath.listFiles().sort{ a,b-> a.isFile() <=> b.isFile() ?: a.getName() <=> b.getName() }
					response.setStatus(200)
					response.setContentType("text/html")
					
					html.html{
						head{
							title("rest-ivy:${path}")
						}
						body{
							h2("rest-ivy:${path}")
							ul{
								if( path!="/" ){
									li{
										a(href:"..", ".." )
									}
								}
								
								listFiles.each{ File file ->
									li{
										def name = file.getName() + (file.isDirectory() ?"/":"")
										a( href:name, name )
									}
								}
							}
						}
					}
				}else{
					response.setStatus(response.SC_MOVED_TEMPORARILY);
					response.setHeader("Location", ".${path}/");
				}
			}else{
				response.setStatus(200)
				response.setContentType( URLConnection.guessContentTypeFromName(repoPath.getName()) ?: "application/octet-stream" )
				response.setContentLength( (int)repoPath.length() ) //TODO:int???
				//response.setHeader("Content-Disposition", "attachment; filename=\"" + repoPath.getName() + "\"");
				response.setHeader("x-file-name", repoPath.getName());
				response.setHeader("x-last-modified", new java.sql.Timestamp( repoPath.lastModified() ).toString());
				response.setHeader("Last-Modified", Dates.httpFormat( repoPath.lastModified() ) );
				
				//write content into output stream
				def stream = response.getOutputStream()
				stream << repoPath.newInputStream()
				stream.flush()
				stream.close()
			}
			
		}else{
			response.setStatus(404,"ERROR: not found")
			response.setContentType("text/plain")
			println "Path not found: request.getPathInfo()"
		}
	}else{
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", "./repo/");
	}
}catch(Throwable e){
	response.setStatus(500,"ERROR: $e")
	response.setContentType("text/plain")
	println "ERROR: $e"
	org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(e).printStackTrace(out)
}

