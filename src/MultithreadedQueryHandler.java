import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Parse queries from file and perform search.
 * 
 * @author Simonl0425
 *
 */
public class MultithreadedQueryHandler implements QueryHandler
{
	private final TreeMap<String, List<SearchResult>> results;
	private final InvertedIndex index;
	private final WorkQueue queue;
	private static final Logger log = LogManager.getLogger();

	/**
	 * Construct QueryHandler object
	 * 
	 * @param index for InvertedIndex
	 */
	public MultithreadedQueryHandler(InvertedIndex index, WorkQueue queue)
	{
		log.info("MultiThreaded QueryHandler initialized");
		this.results = new TreeMap<>();
		this.index = index;
		this.queue = queue;
	}

	/**
	 * Parse the given path and store into the TreeMap.
	 * 
	 * @param path of queries
	 * @param exact search methods
	 * @throws IOException
	 */
	public void parse(String path, boolean exact) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				queue.execute(new SearchTask(line, exact, index, results));
			}
		}
	}

	/**
	 * write results to given path in JSON format
	 * 
	 * @param path path to write to.
	 */
	public void toJSON(Path path) throws IOException
	{
		JSONWriter.writeSearchResults(path, results);
	}

	/**
	 * 
	 * SearchTask for performing individual search, each search responsible for
	 * one query string, parse the query, sort the query and perform search with
	 * parsed queries.
	 * 
	 * @author Simonl0425
	 *
	 */
	public static class SearchTask implements Runnable
	{
		private String queryString;
		private boolean exact;
		private TreeMap<String, List<SearchResult>> results;
		private InvertedIndex index;

		/**
		 * Initialize SearchTask
		 * 
		 * @param queryString string of query
		 * @param exact mode of searching
		 * @param index WordIndex for search
		 * @param results Map of results to add to.
		 */
		public SearchTask(String queryString, boolean exact, InvertedIndex index, TreeMap<String, List<SearchResult>> results)
		{
			this.queryString = queryString;
			this.exact = exact;
			this.results = results;
			this.index = index;
		}

		public void run()
		{
			log.trace(Thread.currentThread().getName() + " is performing search on " + queryString);
			String queries[] = WordParser.parseWords(queryString);
			if (queries.length == 0)
			{
				log.warn("zero length queries detected");
				return;
			}
			Arrays.sort(queries);

			List<SearchResult> local;
			if (exact)
			{
				local = index.exactSearch(queries);
			} else
			{
				local = index.partialSearch(queries);
			}

			String queryString = String.join(" ", queries);
			synchronized (results)
			{
				results.put(queryString, local);
			}
		}

	}

}
