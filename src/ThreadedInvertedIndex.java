import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedInvertedIndex extends InvertedIndex
{
	private static Logger log = LogManager.getLogger();
	private final ReadWriteLock lock;

	public ThreadedInvertedIndex()
	{
		super();
		lock = new ReadWriteLock();
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
		lock.lockReadWrite();
		if (!invertedMap.containsKey(word))
		{
			invertedMap.put(word, new TreeMap<>());
		}

		if (!invertedMap.get(word).containsKey(path))
		{
			invertedMap.get(word).put(path, new TreeSet<>());
		}

		invertedMap.get(word).get(path).add(index);
		lock.unlockReadWrite();
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
			boolean found = false;
			lock.lockReadOnly();
			for (String word: invertedMap.tailMap(query).keySet())
			{
				if (word.startsWith(query))
				{
					search(word, results, finalResults);
					found = true;
				} else
				{
					if (found)
					{
						break;
					}
				}
			}
			lock.unlockReadOnly();
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
	public void search(String word, HashMap<String, SearchResult> results, ArrayList<SearchResult> finalResults)
	{
		lock.lockReadOnly();
		for (String path: invertedMap.get(word).keySet())
		{
			TreeSet<Integer> indices = invertedMap.get(word).get(path);

			if (results.containsKey(path))
			{
				SearchResult result = results.get(path);
				result.addFrequency(indices.size());
				result.setInitialPosition(indices.iterator().next());
			} else
			{
				SearchResult result = new SearchResult(path, indices.size(), indices.iterator().next());
				results.put(path, result);
				finalResults.add(result);
			}
		}
		lock.unlockReadOnly();

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
		lock.lockReadOnly();
		JSONWriter.writeInvertedIndex(invertedMap, path);
		lock.unlockReadOnly();
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
		lock.lockReadOnly();
		try
		{
			return invertedMap.toString();
		} finally
		{
			lock.unlockReadOnly();
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
		lock.lockReadOnly();
		try
		{
			return invertedMap.containsKey(word);
		} finally
		{
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try
		{
			return invertedMap.size();
		} finally
		{
			lock.unlockReadOnly();
		}
	}

	/**
	 * Return the amount of paths found in a specific word.
	 * 
	 * @param word to be checked
	 * @return the amount of paths under the word, 0 if the word is not found.
	 */
	public int size(String word)
	{
		lock.lockReadOnly();
		try
		{
			return contains(word) ? invertedMap.get(word).size() : 0;
		} finally
		{
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try
		{
			return contains(word, path) ? invertedMap.get(word).get(path).size() : 0;
		} finally
		{
			lock.unlockReadOnly();
		}
	}

}
