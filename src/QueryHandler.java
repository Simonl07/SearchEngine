import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

	// TODO Move away from the static methods here (more of a collector than a builder)
	

	private final TreeMap<String, List<SearchResult>> results;
	private final InvertedIndex index;
	
	public QueryHandler(InvertedIndex index) {
		this.results = new TreeMap<>();
		this.index = index;
	}
	
	public void parse(String path, boolean exact) throws IOException{
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
				
				if(exact)
				{
					results.put(String.join(" ", queries), index.exactSearch(queries));
				}else{
					results.put(String.join(" ", queries), index.partialSearch(queries));
				}
			}
		}
	}
	
	/**
	 * parse the query for given path and perform search on the invertedIndex.
	 * 
	 * @param path for query files
	 * @param index InvertedIndex for search.
	 * @param mode partial or exact mode for search
	 * @return TreeMap of queries matching with a list of SearchResult objects.
	 * @throws IOException
	 */
	public static TreeMap<String, List<SearchResult>> parseAndSearch(String path, InvertedIndex index, String mode) throws IOException
	{
		log.info("Parsing and searching query from " + path);
		TreeMap<String, List<SearchResult>> results = new TreeMap<>();
		for (String[] queries: parse(path))
		{
			if (mode.equals("-exact"))
			{
				log.debug("exact search mode");
				results.put(String.join(" ", queries), index.exactSearch(queries));
			} else
			{
				log.debug("partial search mode");
				results.put(String.join(" ", queries), index.partialSearch(queries));
			}
		}
		return results;
	}

	/**
	 * Helper method for parsing queries into a List of String array
	 * 
	 * @param path of the query file
	 * @return List of String array of words in queries.
	 * @throws IOException
	 */
	public static List<String[]> parse(String path) throws IOException
	{
		ArrayList<String[]> output = new ArrayList<>();
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
				output.add(queries);
			}
		}
		return output;
	}
}
