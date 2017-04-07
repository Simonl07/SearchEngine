import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryHandler
{
	private static Logger log = LogManager.getLogger();
	
	public static TreeMap<String, ArrayList<SearchResult>> parseAndSearch(String path, InvertedIndex index, String mode) throws IOException
	{
		log.info("Parsing and searching query from " + path);
		TreeMap<String, ArrayList<SearchResult>> results = new TreeMap<>();
		for(String[] queries: parse(path))
		{
			if(mode.equals("-exact"))
			{
				log.debug("exact search mode");
				results.put(String.join(" ", queries), index.exactSearch(queries));
			}else{
				log.debug("partial search mode");
				results.put(String.join(" ", queries), index.partialSearch(queries));
			}
		}
		return results;
	}
	
	
	public static ArrayList<String[]> parse(String path) throws IOException
	{
		ArrayList<String[]> output = new ArrayList<>();
		try(BufferedReader reader = Files.newBufferedReader(Paths.get(path),StandardCharsets.UTF_8))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				String queries[] = WordParser.parseWords(line);
				if(queries.length == 0)
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
