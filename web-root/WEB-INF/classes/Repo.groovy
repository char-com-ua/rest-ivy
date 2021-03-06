//import groovy.grape.Grape
import groovy.xml.NamespaceBuilder
import groovy.xml.NamespaceBuilderSupport

//@groovy.transform.CompileStatic
public class Repo{
	public static String settingSfx(String s){ s?"-"+s:"" }
	//for cases when + passed into url as + that was translated to ' ' by server according to rules of web
	public static String fixRev(String r){ r && r.indexOf(" ")>=0 ? r.tr(" ","+") : r }
	
	public static AntBuilder ant(Map cfg){
		AntBuilder ant = cfg.ant?:new AntBuilder()
		Props.load().each{ ant.project.setProperty(it.getKey(),it.getValue()) }
		return ant
	}

	public static NamespaceBuilderSupport ivy(AntBuilder ant, String settings){
		ant.taskdef(resource: 'org/apache/ivy/ant/antlib.xml', uri: 'ivy')
		ant.property(name:"ivy.cache.default.root",threadDir())
		NamespaceBuilderSupport ivy = NamespaceBuilder.newInstance(ant, 'ivy', 'org.apache.ivy.ant')
		ivy.settings(file: "./ivysettings${settingSfx(settings)}.xml")
		return ivy
	}
	
	//returns List of org.apache.tools.ant.types.resources.FileResource if config fully local
	public static List resolve(Map cfg){
		AntBuilder ant = ant(cfg)
		def ivy = ivy(ant, cfg.settings)
		def pathId = "P_IVY_PATH"
		ivy.cachepath(
			[
				inline:       true, 
				keep:         true,
				organisation: cfg.org,
				module:       cfg.mod,
				revision:     fixRev(cfg.rev),
				type:         cfg.type,
				conf:         cfg.conf,
				transitive:   false,
				pathId:       pathId,
			].findAll{it.value!=null} //keep only keys with non-null values
		)
		def res = ant.antProject.references[pathId].collect{ it }
		if(cfg.regex){ res = res.findAll{ it.getName() =~ cfg.regex } }
		return res
	}
	
	public static File threadDir(){
		def f = new File("./temp/.ivy2-${Thread.currentThread().getId()}").getAbsoluteFile()
		f.mkdirs()
		return f
	}
	public static File threadFile(String name){
		def f = new File(threadDir() , name)
		return f
	}
	
	//ivy info operation
	//returns [config:[resilvedFiles]]
	public static Map<String,List<File>> info(Map cfg){
		AntBuilder ant = ant(cfg)
		//regexp for config names to resolve (default all)
		String confMask = (String)cfg.conf ?: ".*"
		def ivy = ivy(ant, cfg.settings)
		def res=[:]
		def ivyFile = cfg.ivyFile
		assert ivyFile && ivyFile.exists() && ivyFile.length()
		ivy.info(file:ivyFile)
		ant.antProject.properties['ivy.configurations'].split(',').each { conf ->
			conf=conf.trim()
			if( conf && conf.matches(confMask) ){
				String pathId = "${conf}_libPath"
				ivy.cachepath(file:ivyFile, pathid:pathId, type:'*', conf:conf, transitive:'false')
				res[conf] = ant.antProject.references[pathId].collect{ it }
			}
		}
		return res
	}
	
	
	public static List<String> revisions(Map cfg){
		AntBuilder ant = ant(cfg)
		def ivy = ivy(ant, cfg.settings)
		def keyPrefix = "P_REVISION."
		ivy.listmodules(
			[
				organisation: cfg.org,
				module:       cfg.mod,
				revision:     cfg.rev ?: '*',
				property:     "${keyPrefix}.[revision]",
				value:        "found",
				resolver:     cfg.resolver ?: cfg.settings,
				//resolveMode:  parms.resolve,
			].findAll{it.value!=null} //keep only keys with non-null values
		)
		def rev = ant.antProject.properties.findAll{it.key.startsWith(keyPrefix) && it.key=~'\\d'}.
			collect{it.key.substring(keyPrefix.length()).replaceAll("^\\.","")}.
			unique().
			collect{ (it=~'[0-9]+|[^0-9]+').findAll().collect{it.isLong()?it.toLong():it} }.
			sort().
			reverse().
			collect{it.join("")}
		
		return rev
	}
}
