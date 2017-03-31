/**/

@groovy.transform.CompileStatic
public class IOUtils{
	public static long copy(InputStream input, OutputStream output, int bufsize) throws IOException {
		byte[] buffer = new byte[bufsize];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
