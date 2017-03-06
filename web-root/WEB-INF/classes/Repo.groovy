//import groovy.grape.Grape
import groovy.xml.NamespaceBuilder
import groovy.xml.NamespaceBuilderSupport

//@groovy.transform.CompileStatic
public class Repo{
	private static String settingSfx(String s){ s?"-"+s:"" }
	//for cases when + passed into url as + that was translated to ' ' by server according to rules of web
	private static String fixRev(String r){ r && r.indexOf(" ")>=0 ? r.tr(" ","+") : r }

	public static NamespaceBuilderSupport ivy(AntBuilder ant, String settings){
		ant.taskdef(resource: 'org/apache/ivy/ant/antlib.xml', uri: 'ivy')
		ant.property(name:"ivy.cache.default.root",new File("./temp/.ivy2-${Thread.currentThread().getId()}").getAbsolutePath())
		NamespaceBuilderSupport ivy = NamespaceBuilder.newInstance(ant, 'ivy', 'org.apache.ivy.ant')
		ivy.settings(id:"ivy-settings${settingSfx(settings)}", file: "./ivysettings${settingSfx(settings)}.xml")
		return ivy
	}

	//returns List of org.apache.tools.ant.types.resources.FileResource if config fully local
	public static List resolve(Map cfg=[:]){
		AntBuilder ant = new AntBuilder()
		String settings = (String)cfg.remove("settings")
		def ivy = ivy(ant, settings)
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
				settingsRef:  "ivy-settings${settingSfx(settings)}",
				//resolveMode:  cfg.resolve,
			].findAll{it.value!=null} //keep only keys with non-null values
		)
		def res = ant.antProject.references[pathId].collect{ it }
		if(cfg.regex){ res = res.findAll{ it.getName() =~ cfg.regex } }
		return res
	}
}
