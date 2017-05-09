import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * A Thread-safe version of InvertedIndex using custom read-write lock.
 * 
 * @author Simonl0425
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex
{
	private static Logger log = LogManager.getLogger();
	private final ReadWriteLock lock;

	/**
	 * Initialized the Thread-safe Inverted Index;
	 */
	public ThreadSafeInvertedIndex()
	{
		super();
		log.info("Multithreaded Index initialized");
		lock = new ReadWriteLock();
	}

	@Override
	public void addWord(String word, String path, int index)
	{
		lock.lockReadWrite();
		super.addWord(word, path, index);
		lock.unlockReadWrite();
	}

	@Override
	public void addAll(String path, String[] words, int start)
	{
		lock.lockReadWrite();
		for (String word: words)
		{
			super.addWord(word, path, start++);
		}
		lock.unlockReadWrite();
	}

	@Override
	public void addAll(InvertedIndex other)
	{
		lock.lockReadWrite();
		super.addAll(other);
		lock.unlockReadWrite();
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
		lock.lockReadOnly();
		try
		{
			return super.exactSearch(queries);
		} finally
		{
			lock.unlockReadOnly();
		}
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
		lock.lockReadOnly();
		try
		{
			return super.partialSearch(queries);
		} finally
		{
			lock.unlockReadOnly();
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
		lock.lockReadOnly();
		super.toJSON(path);
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
			return super.toString();
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
			return super.contains(word);
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
		lock.lockReadOnly();
		try
		{
			return super.contains(word, path);
		} finally
		{
			lock.unlockReadOnly();
		}

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
		lock.lockReadOnly();
		try
		{
			return super.contains(word, path, index);
		} finally
		{
			lock.unlockReadOnly();
		}
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
			return super.size();
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
			return super.size(word);
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
			return super.size(word, path);
		} finally
		{
			lock.unlockReadOnly();
		}
	}
}