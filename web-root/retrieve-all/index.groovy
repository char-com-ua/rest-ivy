//import java.nio.file.Files
//import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
try {
	if(request.method=="POST"){
		def parms = request.getParameterMap().collectEntries{k,v-> [k,v[0]]}
		
		def ivyFile = Repo.threadFile(".tmp.ivy.xml")
		ivyFile.setText(ivy,"UTF-8")
		def res = Repo.info(parms + [ivyFile:ivyFile])
		
		assert res : "no resolved elements"
		def outName = "resolve-all.zip"
		
		response.setStatus(200)
		response.setContentType( "application/octet-stream" )
		response.setHeader("Content-Disposition", "attachment; filename=\"" + outName + "\"");
		response.setHeader("x-file-name", outName);
		
		ZipOutputStream zip = new ZipOutputStream(response.getOutputStream())
		//ZipOutputStream zip = new ZipOutputStream(new FileOutputStream("./test.zip"))
		res.each{conf,files->
			files.each{file->
				zip.putNextEntry( new ZipEntry("${conf}/${file.getName()}") )
				file.getInputStream().withStream{ input-> IOUtils.copy( input, zip, 4096 ) }
				zip.closeEntry()
			}
		}
		zip.close()
	}else{
		help()
	}
}catch(Throwable e){
	e.printStackTrace(System.out)
	response.setStatus(500,"ERROR: $e")
	response.setContentType("text/plain")
	println "ERROR: $e"
	e.printStackTrace(out)
}

def help(){
	response.setStatus(400)
	response.setContentType("text/plain")
	println """
	retrieves a set of artifacts according to POST-ed ivy.xml file
	returns all as a zip file where each resolved artifact placed into directory named as config in ivy.xml
		parameters:
		settings   (optional) the ivy settings ref name 
				   the file "./ivysettings-\${settings}.xml" must exist
	"""
}  
