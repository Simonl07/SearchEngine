import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class test
{
	public static void main(String args[]) throws Exception 
	{
		InvertedIndex index = new ThreadSafeInvertedIndex();
		
		WorkQueue queue = new WorkQueue(10);
		
		
		queue.finish();
		index.toJSON(Paths.get("results.json"));
		queue.shutdown();
		
	}

}
