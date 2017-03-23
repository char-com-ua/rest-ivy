/**/
import java.text.SimpleDateFormat

@groovy.transform.CompileStatic
public class Props{
	public static final String PROPS_PATH = "./rest-ivy.properties"
	private static Properties p = null;
	private static Properties load(){
		if(p==null || p.size()==0){
			p=new Properties().with{
				it.load( new File(PROPS_PATH).getAbsoluteFile().newInputStream() );
				return it; 
			}
		}
		return p
	}
	
	
	public static Object get(String key){
		try{
			return load().getProperty(key)
		}catch(e){
			System.err.println(e.toString())
			return null
		}
	}
	
	public static String require(String key){
		String s = get(key)
		if(s==null)throw new RuntimeException("use `$PROPS_PATH` to define required parameter: `$key`")
		return s
	}

	public static String httpFormat(long t){
		SimpleDateFormat httpFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
		httpFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
		return httpFormat.format(new Date(t))
	}
}
