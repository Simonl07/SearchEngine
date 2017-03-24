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

// TODO Always resolve your warnings
// TODO Configure Eclipse to "Organize Imports" on save, and you'll never again see that warning
// TODO Add Javadoc comments to ALL classes and ALL methods

/**
 * Parses command-line arguments to build an inverted index.
 * 
 * @author Simonl0425
 */
public class Driver {

	/**
	 * Parses command-line arguments to build an inverted index.
	 * 
	 * @param args the command line parameters
	 */
	public static void main(String[] args) {

		ArgumentMap argsMap = new ArgumentMap();
		argsMap.parse(args);
		
		Path path = null;
		Path index = null;
		
		
		if(argsMap.hasBoth("-path"))
		{
			path = Paths.get(argsMap.getValue("-path"));
			if(argsMap.hasBoth("-index"))
			{
				index = Paths.get(argsMap.getValue("-index"));
			}else if(argsMap.hasFlag("-index"))
			{
				index = Paths.get("index.json");
			}else{
				index = null;
			}
		}else{
			index = Paths.get("index.json");
		}
		
		if(path != null)
		{
			ArrayList<Path> htmlFiles = HTMLLocator.find(path);
			
			InvertedIndex wordIndex = new InvertedIndex();
			
			for(Path p: htmlFiles)
			{
				try(BufferedReader input = Files.newBufferedReader(p,StandardCharsets.UTF_8);)
				{
					String line = "";
					String content = "";
					while((line = input.readLine())!= null)
					{
						content+= line + "\n";
					}
					String words[] = HTMLCleaner.stripHTML(content).split("\\s+");
					for(int i = 0 ; i < words.length;i++)
					{
						wordIndex.addWord(words[i].trim(), p, i);
					}
					if(index != null)
					{
						JSONWriter.write(wordIndex.getStructure(), index);
					}
				}catch(IOException ioe)
				{
					// TODO Remove stack traces and output user-friendly error messages
					ioe.printStackTrace();
				}
			}
		}else{
			try
			{
				JSONWriter.write(new InvertedIndex().getStructure(), index);
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}		
	
	/*
	 * Driver is project specific, all other "generally useful" code should be in other classes.
	 * 
	 * ArgumentMap map = new ArgumentMap(args);
	 * InvertedIndex index = new InvertedIndex();
	 * 
	 * if (-path) {
	 * 
	 * }
	 * 
	 * if (-index) {
	 * 		String path = map.getString("-index", "index.json");
	 * }
	 */
}
