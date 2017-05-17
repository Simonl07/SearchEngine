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
	public void parse(Path path, boolean exact) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				parse(line, exact);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public TreeMap<String, List<SearchResult>> getResultsMap()
	{
		return (TreeMap<String, List<SearchResult>>) results.clone();
	}

	@Override
	public void toJSON(Path path) throws IOException
	{
		JSONWriter.writeSearchResults(path, results);
	}

	@Override
	public void parse(String query, boolean exact)
	{
		String words[] = WordParser.parseWords(query);

		if (words.length == 0)
		{
			log.warn("zero length queries detected");
			return;
		}

		Arrays.sort(words);
		if (exact)
		{
			results.put(String.join(" ", words), index.exactSearch(words));
		} else
		{
			results.put(String.join(" ", words), index.partialSearch(words));
		}
	}

}