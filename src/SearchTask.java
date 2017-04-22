import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class SearchTask implements Runnable
{
	private String queries[];
	private boolean exact;
	private TreeMap<String, List<SearchResult>> results;
	private ThreadedInvertedIndex index;

	public SearchTask(String queries[], boolean exact, ThreadedInvertedIndex index, TreeMap<String, List<SearchResult>> results)
	{
		this.queries = queries;
		this.exact = exact;
		this.results = results;
		this.index = index;
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
			results.put(String.join(" ", queries), local);
		}
	}

}