import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class InvertedIndex {
	// TODO final
	private final TreeMap<String, TreeMap<Path, TreeSet<Integer>>> invertedMap;
	
	public InvertedIndex()
	{
		invertedMap = new TreeMap<String, TreeMap<Path, TreeSet<Integer>>>();
	}
	
	
	public void build(ArrayList<Path> htmlFiles) throws IOException
	{
		for(Path p: htmlFiles)
		{
			BufferedReader input = Files.newBufferedReader(p,StandardCharsets.UTF_8);
			String line = "";
			String content = "";
			while((line = input.readLine())!= null)
			{
				content+= line + "\n";
			}
			String words[] = HTMLCleaner.stripHTML(content).split("(?U)\\p{Space}+");
			for(int i = 0 ; i < words.length;i++)
			{
				addWord(words[i].trim(), p, i);
			}
		}
	}
	
	
	
	public void addWord(String word, Path path, int index)
	{
		if(invertedMap.containsKey(word))
		{
			TreeMap<Path, TreeSet<Integer>> tempMap = invertedMap.get(word);
			if(tempMap.containsKey(path))
			{
				TreeSet<Integer> tempSet = tempMap.get(path);
				tempSet.add(index);
			}else{
				TreeSet<Integer> newSet = new TreeSet<>();
				newSet.add(index);
				tempMap.put(path, newSet);
			}
		}else{
			TreeSet<Integer> indices = new TreeSet<>();
			indices.add(index);
			TreeMap<Path, TreeSet<Integer>> paths =  new TreeMap<>();
			paths.put(path, indices);
			invertedMap.put(word, paths);
		}
		
	}
	
	/**
	 * Return the size of the TreeMap invertedMap, in other words the amount of words in the 
	 * word index.
	 * 
	 * @return the size of the inverted index.
	 */
	public int size()
	{
		return invertedMap.size();
	}
	
	/**
	 * Send invertedMap to JSONWriter class to output the invertedMap in JSON format.
	 * @param path, the path to write JSON file.
	 * 
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException {
		JSONWriter.write(invertedMap, path);
	}
	
	/**
	 * Re-defined toString method that return the toString of the TreeMap invertedMap.
	 * 
	 * @return the toString of invertedMap.
	 */
	@Override
	public String toString()
	{
		return invertedMap.toString();
	}
	
	
	
	
	/*
	 * TODO Add some more data-structure like methods to make more general
	 * contains(String word), contains(String word, String path), etc.
	 * size(String word), size(String word, String path), etc. 
	 */
}
