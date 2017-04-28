import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO Make a nested class
public class SearchTask implements Runnable
{
	private String queries[];
	private boolean exact;
	private TreeMap<String, List<SearchResult>> results;
	private ThreadedInvertedIndex index;
	private Logger log = LogManager.getLogger();

	public SearchTask(String queries[], boolean exact, ThreadedInvertedIndex index, TreeMap<String, List<SearchResult>> results)
	{
		this.queries = queries;
		this.exact = exact;
		this.results = results;
		this.index = index;
		log.info(Arrays.toString(queries) + " search task initialized, handled by " + Thread.currentThread().getName());
	}

	public void run()
	{
		List<SearchResult> local;
		if (exact)
		{
			local = index.exactSearch(queries);
		} else
		{
			local = index.partialSearch(queries);
		}

		synchronized (results)
		{
			// TODO Do the join() outside the synchronized block
			results.put(String.join(" ", queries), local);
		}
	}

}