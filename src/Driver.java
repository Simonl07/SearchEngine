import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
		
		QueryHandler queryHandler = new QueryHandler(wordIndex);

		if (argsMap.hasValue("-path"))
		{
			log.info("-path flag detected");
			try
			{
				InvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))), wordIndex);
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
					queryHandler.parse(argsMap.getString("-query"), true);
				} else
				{
					queryHandler.parse(argsMap.getString("-query"), false);
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
				queryHandler.toJSON(Paths.get(path));
			} catch (IOException e)
			{
				log.catching(e);
				System.out.println("Encountered error when writing search results into " + path);
				return;
			}
		}
	}
}
