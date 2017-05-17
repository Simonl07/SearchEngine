public class test
{
	public static void main(String args[]) throws Exception 
	{
		String arguments[] = {
				"-url", "http://cs.usfca.edu/~cs212/",
				"-index", "index.json",
				"-limit", "100",
				"-threads", "5",
				"-port", "80",
		};
		
		
		Driver.main(arguments);
	}

}
