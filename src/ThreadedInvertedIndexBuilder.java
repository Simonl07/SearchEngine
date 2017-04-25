import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedInvertedIndexBuilder extends InvertedIndexBuilder
{
	private static Logger log = LogManager.getLogger();
	public static void build(Iterable<Path> htmlFiles, ThreadedInvertedIndex index, WorkQueue queue) throws IOException
	{
		int i = 0;
		for (Path p: htmlFiles)
		{
			queue.execute(new BuildTask(p,index));i++;
		}
		log.info(i + " BuildTask dumped into workqueue.");
	}
}
