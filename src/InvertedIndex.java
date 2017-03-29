import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * Customized data structure to store words, paths and indices, with other data structure-like methods.
 * 
 * @author Simonl0425
 *
 */
public class InvertedIndex {
	
	
	// TODO final
	private final TreeMap<String, TreeMap<Path, TreeSet<Integer>>> invertedMap;
	
	
	
	/**
	 * Initialize the TreeMap<String, TreeMap<Path, TreeSet<Integer>>>
	 */
	public InvertedIndex()
	{
		invertedMap = new TreeMap<String, TreeMap<Path, TreeSet<Integer>>>();
	}
	
	
	/**
	 * Take an ArrayList of HTML files, read the content, clean the HTML and construct the inverted Index.
	 * 
	 * @param htmlFiles ArrayList of HTML Path to read from.
	 * @throws IOException
	 */
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
	
	
	/**
	 * Add spedific word to the invertedIndex
	 * 
	 * @param word  the word to add
	 * @param path	the path of HTML where the word is find
	 * @param index	the index of the word in the HTML file.
	 */
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
	
	
	/**
	 * Check if the map contains the specific word.
	 * 
	 * @param word	to check if the map contains the word.
	 * @return true if map contains word, false otherwise.
	 * 
	 */
	public boolean contains(String word)
	{
		return invertedMap.containsKey(word);
	}
	
	/**
	 * Check if the map contains the specific word, and the words contains specific path.
	 * 
	 * @param word	to check if the map contains the word.
	 * @param path	to check if the word contains the path.
	 * @return
	 */
	public boolean contains(String word, Path path)
	{
		return contains(word) ? invertedMap.get(word).containsKey(path) : false;
	}
	
	/**
	 * Check if the map contains the specific word, the words contains specific path and the path has specific index.
	 * 
	 * @param word	to check if the map contains the word.
	 * @param path	to check if the word contains the path.
	 * @param index to check if the path contains the index.
	 * @return 
	 */
	public boolean contains(String word, Path path, int index)
	{
		return contains(word, path) ? invertedMap.get(word).get(path).contains(index) : false;
	}
	
	
	/**
	 * Return the amount of words in the invertedIndex.
	 * 
	 * @return the size of the inverted index.
	 */
	public int size()
	{
		return invertedMap.size();
	}
	
	/**
	 * Return the amount of paths found in a specific word.
	 * 
	 * @param word	to be checked
	 * @return the amount of paths under the word, 0 if the word is not found.
	 */
	public int size(String word)
	{
		return contains(word) ? invertedMap.get(word).size() : 0;
	}
	
	/**
	 * Return the amount of indices found in a specific path under a word.
	 * 
	 * @param word	to be checked
	 * @param path	to be checked
	 * 
	 * @return the amount of indices under the path, 0 if the word or path is not found.
	 */
	public int size(String word, Path path)
	{
		return contains(word, path) ? invertedMap.get(word).get(path).size() : 0;
	}
	
	
	/*
	 * TODO Add some more data-structure like methods to make more general
	 * contains(String word), contains(String word, String path), etc.
	 * size(String word), size(String word, String path), etc. 
	 */
}
