import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO Need to integrate project 1 changes into your project 2, and integrate project 2 into your master branch

/**
 * Parses command-line arguments to build an inverted index.
 * 
 * @author Simonl0425
 */
public class Driver
{
	private static Logger log = LogManager.getLogger();

	/**
	 * Parses command-line arguments to build an inverted index and perform
	 * search from query.
	 * 
	 * @param args the command line parameters
	 */
	public static void main(String[] args)
	{
		ArgumentMap argsMap = new ArgumentMap(args);

		InvertedIndex wordIndex = new InvertedIndex();

		TreeMap<String, List<SearchResult>> searchResults = new TreeMap<>();

		if (argsMap.hasValue("-path"))
		{
			log.info("-path flag detected");
			try
			{
				wordIndex = InvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))));
			} catch (IOException e)
			{
				log.catching(e);
				System.out.println("Encountered error when reading from file and building the Inverted Index.");
				return;
			}
		}

		if (argsMap.hasFlag("-index"))
		{
			log.info("-index flag detected");
			String indexPath = argsMap.getString("-index", "index.json");
			try
			{
				wordIndex.toJSON(Paths.get(indexPath));
			} catch (IOException e)
			{
				log.catching(e);
				System.out.println("Encountered error when writing index into JSON file.");
				return;
			}
		}

		if (argsMap.hasValue("-query"))
		{
			log.info("-query flag detected");
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
				log.catching(e);
				System.out.println("Encountered error when searching through the index.");
				return;
			}
		}

		if (argsMap.hasFlag("-results"))
		{
			log.info("-results flag detected");
			String path = argsMap.getString("-results", "results.json");
			try
			{
				JSONWriter.writeSearchResults(Paths.get(path), searchResults);
			} catch (IOException e)
			{
				log.catching(e);
				System.out.println("Encountered error when writing search results into " + path);
				return;
			}
		}
	}
}
