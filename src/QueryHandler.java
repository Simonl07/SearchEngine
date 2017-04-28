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
public class QueryHandler
{
	private static Logger log = LogManager.getLogger();

	// TODO private
	protected final TreeMap<String, List<SearchResult>> results;
	protected final InvertedIndex index;

	/**
	 * Construct QueryHandler object
	 * 
	 * @param index for InvertedIndex
	 */
	public QueryHandler(InvertedIndex index)
	{
		this.results = new TreeMap<>();
		this.index = index;
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
				String queries[] = WordParser.parseWords(line);
				
				if (queries.length == 0)
				{
					log.warn("zero length queries detected");
					continue;
				}

				Arrays.sort(queries);
				if (exact)
				{
					results.put(String.join(" ", queries), index.exactSearch(queries));
				} else
				{
					results.put(String.join(" ", queries), index.partialSearch(queries));
				}
			}
		}
	}
	/**
	 * output the SearchResults objects to JSON Writer.
	 * 
	 * @param path to write JSON file to
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException
	{
		JSONWriter.writeSearchResults(path, results);
	}

}