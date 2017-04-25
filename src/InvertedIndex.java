import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Customized data structure to store words, paths and indices, with other data
 * structure-like methods.
 * 
 * @author Simonl0425
 *
 */
public class InvertedIndex
{
	private static Logger log = LogManager.getLogger();

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedMap;

	/**
	 * Initialize the TreeMap<String, TreeMap<Path, TreeSet<Integer>>>
	 */
	public InvertedIndex()
	{
		invertedMap = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		log.trace("invertedMap initialized.");
	}

	/**
	 * Add specific word to the invertedIndex
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

	/**
	 * perform exact search in the inverted index, and return an ArrayList of
	 * search results
	 * 
	 * @param queries String array of queries for searching
	 * @return ArrayList of SearchResult objects
	 */
	public ArrayList<SearchResult> exactSearch(String[] queries)
	{
		log.trace("performing exact search on " + Arrays.toString(queries));
		HashMap<String, SearchResult> results = new HashMap<>();
		ArrayList<SearchResult> finalResults = new ArrayList<>();

		for (String query: queries)
		{
			if (this.contains(query))
			{
				search(query, results, finalResults);
			}
		}

		Collections.sort(finalResults);
		return finalResults;
	}

	/**
	 * perform partial search in the inverted index, and return an ArrayList of
	 * search results
	 * 
	 * @param queries String array of queries for searching
	 * @return ArrayList of SearchResult objects
	 */
	public ArrayList<SearchResult> partialSearch(String[] queries)
	{
		log.trace("performing partial search on " + Arrays.toString(queries));
		HashMap<String, SearchResult> results = new HashMap<>();
		ArrayList<SearchResult> finalResults = new ArrayList<>();

		for (String query: queries)
		{
			for (String word: invertedMap.tailMap(query).keySet())
			{
				if (word.startsWith(query))
				{
					search(word, results, finalResults);
				} else
				{
					break;
				}
			}
		}

		Collections.sort(finalResults);
		return finalResults;
	}

	/**
	 * Search for the given query under given path.
	 * 
	 * @param word the query to search for
	 * @param path the path of the query
	 * @param results HashMap of the query and the SearchResult object
	 */
	private void search(String word, HashMap<String, SearchResult> results, ArrayList<SearchResult> finalResults)
	{
		for (String path: invertedMap.get(word).keySet())
		{
			TreeSet<Integer> indices = invertedMap.get(word).get(path);

			if (results.containsKey(path))
			{
				SearchResult result = results.get(path);
				result.addFrequency(indices.size());
				result.setInitialPosition(indices.first());
			} else
			{
				SearchResult result = new SearchResult(path, indices.size(), indices.iterator().next());
				results.put(path, result);
				finalResults.add(result);
			}
		}

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
		JSONWriter.writeInvertedIndex(invertedMap, path);
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
