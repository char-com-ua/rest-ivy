/**/
import java.text.SimpleDateFormat

@groovy.transform.CompileStatic
public class Dates{
	public static String httpFormat(long t){
		SimpleDateFormat httpFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
		httpFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
		return httpFormat.format(new Date(t))
	}
}
