import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;


public class tester {
	public static void main(String args[])
	{
		ArgumentMap argsMap = new ArgumentMap();
		argsMap.parse(args);
		
		Path rootPath = Paths.get(argsMap.getValue("-path"));
		
		Path index = null;
		
		if(argsMap.hasFlag("-index"))
		{
			index = Paths.get(argsMap.getValue("-index"));
		}
		
		ArrayList<Path> htmlFiles = HTMLLocator.find(rootPath);
		
		InvertedIndex wordIndex = new InvertedIndex();
		
		for(Path path: htmlFiles)
		{
			System.out.println("Working on: " + path.toString());
			try(BufferedReader input = Files.newBufferedReader(path,StandardCharsets.UTF_8);)
			{
				String line = "";
				String content = "";
				while((line = input.readLine())!= null)
				{
					content+= line + "\n";
				}
				
				String words[] = HTMLCleaner.stripHTML(content).split("\\p{Space}+");
				for(int i = 0 ; i < words.length;i++)
				{
					wordIndex.addWord(words[i], path, i);
				}
				JSONWriter.write(wordIndex.getStructure(), index);
				
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			
		}
	}
}