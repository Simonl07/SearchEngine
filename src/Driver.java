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

		InvertedIndex wordIndex = null;

		QueryHandler queryHandler = null;

		WorkQueue queue = null;

		if (argsMap.hasValue("-threads") && argsMap.getInteger("-threads") > 0)
		{
			log.info("-thread flag detected");
			multithreaded = true;
			queue = new WorkQueue(argsMap.getInteger("-threads", 5));
			wordIndex = new ThreadedInvertedIndex();
			queryHandler = new ThreadedQueryHandler(wordIndex, queue);
		}else{
			wordIndex = new InvertedIndex();
			queryHandler = new QueryHandler(wordIndex);
		}

		
		if (argsMap.hasValue("-path"))
		{
			log.info("-path flag detected");
			try
			{
				if (multithreaded)
				{
					ThreadedInvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))), (ThreadedInvertedIndex)wordIndex, queue);
				} else
				{
					InvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))), wordIndex);
				}
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
			if(multithreaded)
			{
				synchronized(queue)
				{
					queue.finish();
				}
			}
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
				if (multithreaded)
				{
					synchronized(queue) // TODO Do not need synchronized here
					{
						queue.finish();
					}
					queryHandler = new ThreadedQueryHandler(wordIndex, queue);
				} else
				{
					queryHandler = new QueryHandler(wordIndex);
				}

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
			if(multithreaded)
			{
				synchronized(queue) // TODO Remove synchronized
				{
					queue.finish();
				}
			}
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

		// TODO shutdown the work queue
	}
}
