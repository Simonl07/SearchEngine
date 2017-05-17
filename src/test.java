public class test
{
	public static void main(String args[]) throws Exception 
	{
		String arguments[] = {
				"-url", "http://cs.usfca.edu/~cs212/wdgcss/properties.html",
				"-index", "index.json",
				"-limit", "60",
				"-threads", "10",
				"-port", "80",
		};
		
		
		Driver.main(arguments);
	}

}
