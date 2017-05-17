public class Test
{
	public static void main(String args[]) throws Exception 
	{
		String arguments[] = {
				"-url", "http://cs.usfca.edu/",
				"-index", "index.json",
				"-limit", "300",
				"-threads", "30",
				"-port", "80",
		};
		
		
		Driver.main(arguments);
	}

}
