
@groovy.transform.CompileStatic
public class FilteredStream extends OutputStream{
	static Map<Long,ByteArrayOutputStream> delegate = new java.util.concurrent.ConcurrentHashMap();
	static PrintStream original = null;
	
	public static void filterStart(){
		//sync problem here
		if(original==null){
			original = System.out;
			System.out = new PrintStream( new FilteredStream(), true );
		}
	
		ByteArrayOutputStream bb = new ByteArrayOutputStream(512);
		PrintStream p = new PrintStream(bb);
		delegate.put(Thread.currentThread().getId() as Long, bb);
	}
	
	public static String filterEnd(String encoding="UTF-8"){
		ByteArrayOutputStream bb = delegate.remove(Thread.currentThread().getId() as Long);
		if(bb==null)return null
		return bb.toString(encoding);
	}
	
	public void write(int b) {
		ByteArrayOutputStream bb = delegate.get(Thread.currentThread().getId() as Long);
		if(bb==null){
			original.write(b);
		}else{
			bb.write(b);
		}
	}
}
