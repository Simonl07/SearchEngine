import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;


public class tester {
	public static void main(String args[])
	{
		ArgumentMap map = new ArgumentMap();
		map.parse(args);
		
		Path rootPath = Paths.get(map.getValue("-path"));
		
		ArrayList<Path> htmlFiles = HTMLLocator.find(rootPath);
		
		for(Path path: htmlFiles)
		{
			try(BufferedReader input = Files.newBufferedReader(path,StandardCharsets.UTF_8))
			{
				String line = "";
				String content = "";
				while((line = input.readLine())!= null)
				{
					content+= line;
				}
				
				System.out.println(HTMLCleaner.stripHTML(content));
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			
		}
		
	}
}
