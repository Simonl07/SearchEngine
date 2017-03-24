import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

// TODO That is okay to have... but please use an uppercase first letter
public class tester {
	public static void main(String args[])
	{
		try(BufferedReader input = Files.newBufferedReader(Paths.get(args[0]),StandardCharsets.UTF_8);)
		{
			String line = "";
			String content = "";
			while((line = input.readLine())!= null)
			{
				content+= line + "\n";
			}
			
			
			
			System.out.println(HTMLCleaner.stripHTML(content));
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
