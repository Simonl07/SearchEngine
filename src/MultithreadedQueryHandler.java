import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
	private final ThreadSafeInvertedIndex index;
	private final WorkQueue queue;
	private static final Logger log = LogManager.getLogger();

	/**
	 * Construct QueryHandler object
	 * 
	 * @param index for InvertedIndex
	 */
	public MultithreadedQueryHandler(ThreadSafeInvertedIndex index, WorkQueue queue)
	{
		log.info("MultiThreaded QueryHandler initialized");
		this.results = new TreeMap<>();
		this.index = index;
		this.queue = queue;
	}

	@Override
	public void parse(Path path, boolean exact) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				queue.execute(new SearchTask(line, exact));
			}
		}

		queue.finish();
	}

	@Override
	public void toJSON(Path path) throws IOException
	{
		synchronized (results)
		{
			JSONWriter.writeSearchResults(path, results);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Return a copy of the results object of this handler;
	 * 
	 * @return Map of query to results.
	 */
	public TreeMap<String, List<SearchResult>> getResultsMap()
	{
		log.info("results size: " + results.size());
		return (TreeMap<String, List<SearchResult>>) results.clone();
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
	public class SearchTask implements Runnable
	{
		private String queryString;
		private boolean exact;

		/**
		 * Initialize SearchTask
		 * 
		 * @param queryString string of query
		 * @param exact mode of searching
		 * @param index WordIndex for search
		 * @param results Map of results to add to.
		 */
		public SearchTask(String queryString, boolean exact)
		{
			log.info("SearchTask: " + queryString + " constructed, exact = " + exact);
			this.queryString = queryString;
			this.exact = exact;
		}

		@Override
		public void run()
		{
			log.info(Thread.currentThread().getName() + " is performing search on " + queryString);
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

	@Override
	public void parse(String query, boolean exact)
	{
		clearResults();
		queue.execute(new SearchTask(query, exact));
		queue.finish();
	}

	/**
	 * Clear the results in the treeMap.
	 */
	private void clearResults()
	{
		results.clear();
	}

}
