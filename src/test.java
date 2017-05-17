public class test
{
	public static void main(String args[]) throws Exception 
	{
		String arguments[] = {
				"-url", "http://www.google.com",
				"-index", "index.json",
				"-limit", "500",
				"-threads", "20",
				"-port", "80",
		};
		
		
		Driver.main(arguments);
	}

}
