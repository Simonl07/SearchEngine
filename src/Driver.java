import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Parses command-line arguments to build an inverted index.
 * 
 * @author Simonl0425
 */
public class Driver
{

	/**
	 * Parses command-line arguments to build an inverted index.
	 * 
	 * @param args the command line parameters
	 */
	public static void main(String[] args)
	{

		ArgumentMap argsMap = new ArgumentMap(args);

		InvertedIndex wordIndex = new InvertedIndex();

		TreeMap<String, ArrayList<SearchResult>> searchResults = new TreeMap<>();

		if (argsMap.hasValue("-path"))
		{
			try
			{
				wordIndex = InvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))));
			} catch (IOException e)
			{
				System.out.println("Encountered error when reading from file and building the Inverted Index.");
				return;
			}
		}

		if (argsMap.hasFlag("-index"))
		{
			String indexPath = argsMap.getString("-index", "index.json");
			try
			{
				wordIndex.toJSON(Paths.get(indexPath));
			} catch (IOException e)
			{
				System.out.println("Encountered error when writing index into JSON file.");
				return;
			}
		}

		if (argsMap.hasValue("-query"))
		{
			try
			{
				if (argsMap.hasFlag("-exact"))
				{
					searchResults = QueryHandler.parseAndSearch(argsMap.getString("-query"), wordIndex, "-exact");
				} else
				{
					searchResults = QueryHandler.parseAndSearch(argsMap.getString("-query"), wordIndex, "-partial");
				}
			} catch (IOException e)
			{
				System.out.println("Encountered error when searching through the index.");
				return;
			}
		}

		if (argsMap.hasFlag("-results"))
		{
			String path = argsMap.getString("-results", "results.json");
			try
			{
				JSONWriter.writeSearchResults(Paths.get(path), searchResults);
			} catch (IOException e)
			{
				System.out.println("Encountered error when writing search results into " + path);
				return;
			}
		}
	}
}
