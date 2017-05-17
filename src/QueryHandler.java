import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

/**
 * QueryHandler interface, As reference.
 * @author Simonl0425
 *
 */
public interface QueryHandler
{

	/**
	 * output the SearchResults objects to JSON Writer.
	 * 
	 * @param path to write JSON file to
	 * @throws IOException
	 */
	void toJSON(Path path) throws IOException;

	/**
	 * Parse the given path and store into a local TreeMap.
	 * 
	 * @param path of queries
	 * @param exact search methods
	 * @throws IOException
	 */
	void parse(Path path, boolean exact) throws IOException;
	
	void parse(String query, boolean exact);
	
	
	TreeMap<String, List<SearchResult>> getResultsMap();
}
