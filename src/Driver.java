import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class Driver {

	public static void main(String[] args) {
		
		boolean outputMode = true;
		boolean emptyMode = false;
		
		ArgumentMap argsMap = new ArgumentMap();
		argsMap.parse(args);
		
		String pathtxt = null;
		String indexPath = null;
		
		if(argsMap.hasFlag("-index"))
		{
			indexPath = argsMap.getValue("-index");
			if(indexPath == null)
			{
				try{
					File a = new File("index.json");
					a.createNewFile();
					return;
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}else if(!argsMap.hasFlag("-path"))
			{
				emptyMode = true;
				pathtxt = "";
			}
			System.out.println(indexPath);
		}else{
			outputMode = false;
			indexPath = "";
		}
		if(argsMap.hasFlag("-path") && (argsMap.getValue("-path")!= null))
		{
			pathtxt = argsMap.getValue("-path");
		}else{
			return;
		}
		
		
		
		Path rootPath = Paths.get(pathtxt);
		Path index = Paths.get(indexPath);
		
		ArrayList<Path> htmlFiles = HTMLLocator.find(rootPath);
		
		InvertedIndex wordIndex = new InvertedIndex();
		
		System.out.println(emptyMode);
		
		for(Path path: htmlFiles)
		{
			System.out.println("Working on: " + path.toString());
			try(BufferedReader input = Files.newBufferedReader(path,StandardCharsets.UTF_8);)
			{
				String line = "";
				String content = "";
				if(!emptyMode)
				{
					while((line = input.readLine())!= null)
					{
						content+= line + "\n";
					}
				}else{
					content = " ";
				}
				String words[] = HTMLCleaner.stripHTML(content).split("\\p{Space}+");
				for(int i = 0 ; i < words.length;i++)
				{
					wordIndex.addWord(words[i], path, i);
				}
				if(outputMode)
				{
					JSONWriter.write(wordIndex.getStructure(), index);
				}
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			
		}
	}	
}
