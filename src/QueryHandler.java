import java.io.IOException;
import java.nio.file.Path;

// TODO Javadoc here, inherit the javadoc in the other classes

/**
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

	void parse(String path, boolean exact) throws IOException;
}
