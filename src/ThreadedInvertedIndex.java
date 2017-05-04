import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO Refactor to ThreadSafe

/**
 * 
 * A Thread-safe version of InvertedIndex using custom read-write lock.
 * 
 * @author Simonl0425
 *
 */
public class ThreadedInvertedIndex extends InvertedIndex
{
	private static Logger log = LogManager.getLogger();
	private final ReadWriteLock lock;

	/**
	 * Initialized the Thread-safe Inverted Index;
	 */
	public ThreadedInvertedIndex()
	{
		super();
		log.info("Multithreaded Index initialized");
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
		super.addWord(word, path, index);
		lock.unlockReadWrite();
	}
	
	/* TODO
	public void addAll(String path, String[] words, int start)
	{
		lock
		for (String word: words)
		{
			super.addWord(word, path, start++);
		}
		unlock
	}
	*/

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
	
	// TODO Override your contains/size methods too
}