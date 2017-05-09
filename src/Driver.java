import java.io.IOException;
import java.nio.file.Paths;

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
		boolean multithreaded = false;

		ArgumentMap argsMap = new ArgumentMap(args);

		ThreadSafeInvertedIndex threadSafe = null;

		InvertedIndex wordIndex = null;

		QueryHandler queryHandler = null;

		WorkQueue queue = null;

		if (argsMap.hasValue("-threads") && argsMap.getInteger("-threads") > 0)
		{
			log.info("-thread flag detected");
			multithreaded = true;
			queue = new WorkQueue(argsMap.getInteger("-threads", 5));

			threadSafe = new ThreadSafeInvertedIndex();
			
			wordIndex = threadSafe;

			queryHandler = new MultithreadedQueryHandler(threadSafe, queue);

			if (argsMap.hasValue("-path"))
			{
				log.info("-path flag detected");
				try
				{
					ThreadedInvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))), threadSafe, queue);
				} catch (IOException e)
				{
					log.catching(e);
					System.out.println("Encountered error when reading from file and building the Inverted Index.");
					return;
				}
			}
		} else
		{
			wordIndex = new InvertedIndex();

			queryHandler = new SingleThreadedQueryHandler(wordIndex);

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
		}

		if (argsMap.hasFlag("-index"))
		{
			log.info("-index flag detected");

			String indexPath = argsMap.getString("-index", "index.json");
			try
			{
				wordIndex.toJSON(Paths.get(indexPath));
				log.info("CHECK");
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
				queryHandler.parse(argsMap.getString("-query"), argsMap.hasFlag("-exact"));
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

		if (multithreaded)
		{
			queue.shutdown();
		}
	}
}
