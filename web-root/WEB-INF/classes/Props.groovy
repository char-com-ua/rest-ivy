/**/

@groovy.transform.CompileStatic
public class Props{
	public static final String PROPS_PATH = "./rest-ivy.properties"
	private static Properties p = null
	private static long lastModified = -1
	
	public static Properties load(){
		try{
			File pf = new File(PROPS_PATH).getAbsoluteFile()
			if(p==null || p.size()==0 || lastModified!=pf.lastModified()){
				p=new Properties().with{
					it.load( pf.newInputStream() )
					return it
				}
				lastModified = pf.lastModified()
			}
		}catch(e){
			System.err.println(e.toString())
			return new Properties()
		}
		return p
	}
	
	public static String get(String key){
		return load().getProperty(key)
	}
	
	
	public static String require(String key){
		String s = get(key)
		if(s==null)throw new RuntimeException("use `$PROPS_PATH` to define required parameter: `$key`")
		return s
	}

}
