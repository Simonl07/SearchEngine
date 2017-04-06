import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Customized data structure to store words, paths and indices, with other data
 * structure-like methods.
 * 
 * @author Simonl0425
 *
 */
public class InvertedIndex
{

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedMap;

	/**
	 * Initialize the TreeMap<String, TreeMap<Path, TreeSet<Integer>>>
	 */
	public InvertedIndex()
	{
		invertedMap = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * Add spedific word to the invertedIndex
	 * 
	 * @param word the word to add
	 * @param path the path of HTML where the word is find
	 * @param index the index of the word in the HTML file.
	 */
	public void addWord(String word, String path, int index)
	{
		if (!invertedMap.containsKey(word))
		{
			invertedMap.put(word, new TreeMap<>());
		}

		if (!invertedMap.get(word).containsKey(path))
		{
			invertedMap.get(word).put(path, new TreeSet<>());
		}

		invertedMap.get(word).get(path).add(index);
	}

	/**
	 * Adds the array of words at once with default start at position 1
	 *
	 * @param words array of words to add
	 *
	 * @see #addAll(String[], int)
	 */
	public void addAll(String path, String[] words)
	{
		addAll(path, words, 1);
	}

	/**
	 * Adds the array of words at once with provided start position.
	 *
	 * @param words array of words to add
	 * @param start starting position
	 */
	public void addAll(String path, String[] words, int start)
	{
		for (String word: words)
		{
			addWord(word, path, start++);
		}
	}

	public ArrayList<SearchResult> exactSearch(String[] queries)
	{
		HashMap<String, SearchResult> results = new HashMap<>();
		ArrayList<SearchResult> finalResults = new ArrayList<>();

		for (String query: queries)
		{
			if (this.contains(query))
			{
				search(query, results);
			}
		}
		for (String s: results.keySet())
		{
			finalResults.add(results.get(s));
		}

		Collections.sort(finalResults);
		return finalResults;
	}
	
	public ArrayList<SearchResult> partialSearch(String[] queries)
	{
		HashMap<String, SearchResult> results = new HashMap<>();
		ArrayList<SearchResult> finalResults = new ArrayList<>();

		for (String query: queries)
		{
			for(String word: invertedMap.keySet())
			{
				if (word.startsWith(query))
				{
					search(word, results);
				}
			}
		}
		for (String s: results.keySet())
		{
			finalResults.add(results.get(s));
		}

		Collections.sort(finalResults);
		return finalResults;
	}
	
	
	public void search(String word, HashMap<String, SearchResult> results)
	{
		for (String path: invertedMap.get(word).keySet())
		{
			search(word, path, results);
		}
	}

	public void search(String word, String path, HashMap<String, SearchResult> results)
	{
		TreeSet<Integer> indices = invertedMap.get(word).get(path);
		SearchResult newResult = new SearchResult(word, path, indices.size(), indices.iterator().next());
		SearchResult finalResult;
		if (results.containsKey(path))
		{
			finalResult = mergeResult(results.get(path), newResult);
		} else
		{
			finalResult = newResult;
		}
		results.put(path, finalResult);
		
	}

	private SearchResult mergeResult(SearchResult a, SearchResult b)
	{
		a.addFrequency(b.getFrequency());
		a.setInitialPosition(Math.min(a.getInitialPosition(), b.getInitialPosition()));
		return a;
	}

	/**
	 * Send invertedMap to JSONWriter class to output the invertedMap in JSON
	 * format.
	 * 
	 * @param path, the path to write JSON file.
	 * 
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException
	{
		JSONWriter.write(invertedMap, path);
	}

	/**
	 * Re-defined toString method that return the toString of the TreeMap
	 * invertedMap.
	 * 
	 * @return the toString of invertedMap.
	 */
	@Override
	public String toString()
	{
		return invertedMap.toString();
	}

	public void display()
	{
		for (String word: invertedMap.keySet())
		{
			System.out.println(word);
			for (String path: invertedMap.get(word).keySet())
			{
				System.out.println("\t" + path);
				System.out.println("\t\t" + invertedMap.get(word).get(path));
			}
		}
	}

	/**
	 * Check if the map contains the specific word.
	 * 
	 * @param word to check if the map contains the word.
	 * @return true if map contains word, false otherwise.
	 * 
	 */
	public boolean contains(String word)
	{
		return invertedMap.containsKey(word);
	}

	/**
	 * Check if the map contains the specific word, and the words contains
	 * specific path.
	 * 
	 * @param word to check if the map contains the word.
	 * @param path to check if the word contains the path.
	 * @return
	 */
	public boolean contains(String word, String path)
	{
		return contains(word) ? invertedMap.get(word).containsKey(path) : false;
	}

	/**
	 * Check if the map contains the specific word, the words contains specific
	 * path and the path has specific index.
	 * 
	 * @param word to check if the map contains the word.
	 * @param path to check if the word contains the path.
	 * @param index to check if the path contains the index.
	 * @return
	 */
	public boolean contains(String word, String path, int index)
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
	 * @param word to be checked
	 * @return the amount of paths under the word, 0 if the word is not found.
	 */
	public int size(String word)
	{
		return contains(word) ? invertedMap.get(word).size() : 0;
	}

	/**
	 * Return the amount of indices found in a specific path under a word.
	 * 
	 * @param word to be checked
	 * @param path to be checked
	 * 
	 * @return the amount of indices under the path, 0 if the word or path is
	 *         not found.
	 */
	public int size(String word, String path)
	{
		return contains(word, path) ? invertedMap.get(word).get(path).size() : 0;
	}

}
