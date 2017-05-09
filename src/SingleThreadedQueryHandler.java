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
public class SingleThreadedQueryHandler implements QueryHandler
{
	private static Logger log = LogManager.getLogger();
	private final TreeMap<String, List<SearchResult>> results;
	private final InvertedIndex index;

	/**
	 * Construct QueryHandler object
	 * 
	 * @param index for InvertedIndex
	 */
	public SingleThreadedQueryHandler(InvertedIndex index)
	{
		this.results = new TreeMap<>();
		this.index = index;
	}


	
	@Override
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

	@Override
	public void toJSON(Path path) throws IOException
	{
		JSONWriter.writeSearchResults(path, results);
	}

}