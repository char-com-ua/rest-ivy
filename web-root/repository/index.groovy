/**/

try {
	def parms = request.getParameterMap().collectEntries{k,v-> [k,v[0]]}
	assert Props.require("repository.root")
	File repoRoot = new File( Props."repository.root" )
	def path = request.getPathInfo()
	def method = request.getMethod()
	
	if(path){
		File repoPath = new File(repoRoot, path)
		if( method=='PUT' ){
			if(repoPath.exists())throw new IOException("File ${path} exists. Override not allowed.")
			repoPath.getParentFile().mkdirs() 
			if(!repoPath.getParentFile().exists())throw new IOException("Can't create dir: ${repoPath.getParentFile()}")
			InputStream istream = request.getInputStream()
			repoPath << istream
			istream.close()
			response.setStatus(201,"File created")
		}else{
			assert method in ['GET','HEAD']
			if( repoPath.exists() ){
				if(repoPath.isDirectory()){
					if(path.endsWith("/")){
						def listFiles = repoPath.listFiles().sort{ a,b-> a.isFile() <=> b.isFile() ?: a.getName() <=> b.getName() }
						response.setStatus(200)
						response.setContentType("text/html")
						if(method=='GET'){
							html.setDoubleQuotes(true)
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
						}
					}else{
						response.setStatus(response.SC_MOVED_TEMPORARILY);
						response.setHeader("Location", ".${path}/");
					}
				}else{
					//process GET/HEAD
					response.setStatus(200)
					response.setContentType( URLConnection.guessContentTypeFromName(repoPath.getName()) ?: "application/octet-stream" )
					response.setContentLength( (int)repoPath.length() ) //TODO:int???
					//response.setHeader("Content-Disposition", "attachment; filename=\"" + repoPath.getName() + "\"");
					response.setHeader("x-file-name", repoPath.getName());
					response.setHeader("x-last-modified", new java.sql.Timestamp( repoPath.lastModified() ).toString());
					response.setHeader("Last-Modified", Dates.httpFormat( repoPath.lastModified() ) );
					if(method=='GET'){
						//write content into output stream
						def stream = response.getOutputStream()
						stream << repoPath.newInputStream()
						stream.flush()
						stream.close()
					}
				}
			}else{
				response.setStatus(404,"ERROR: not found")
				response.setContentType("text/plain")
				println "Path not found: request.getPathInfo()"
			}
		}
	}else{
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", "./repository/");
	}
}catch(Throwable e){
	response.setStatus(500,"ERROR: $e")
	response.setContentType("text/plain")
	println "ERROR: $e"
	org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(e).printStackTrace(out)
}

