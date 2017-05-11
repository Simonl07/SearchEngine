import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * Multi-threaded builder to build inverted index.
 * 
 * @author Simonl0425
 *
 */
public class ThreadedInvertedIndexBuilder
{
	
	private static Logger log = LogManager.getLogger();

	/**
	 * Override build method that add new buildTask to work queue.
	 * 
	 * @param htmlFiles list of HTML files
	 * @param index WordIndex
	 * @param queue WorkQueue to add buildTask to
	 * @throws IOException
	 */
	public static void build(Iterable<Path> htmlFiles, InvertedIndex index, WorkQueue queue) throws IOException
	{
		for (Path p: htmlFiles)
		{
			queue.execute(new PathBuildTask(p, index));
		}
		
		queue.finish();
	}

	/**
	 * Build the inverted index for each file
	 * 
	 * @author Simonl0425
	 *
	 */
	public static class PathBuildTask implements Runnable
	{
		private Path path;
		private InvertedIndex index;
		

		/**
		 * Initialized BuildTasks
		 * 
		 * @param path of HTML
		 * @param index to add to.
		 */
		public PathBuildTask(Path path, InvertedIndex index)
		{
			log.trace("BuildTask initialized for path " + path);
			this.path = path;
			this.index = index;
		}

		@Override
		public void run()
		{
			try
			{
				log.trace(Thread.currentThread().getName() + " is building from " + path + " || " + index.getClass());

				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.build(path, local);

				index.addAll(local);
				
			} catch (IOException e)
			{
				System.out.println("Encountered error when reading from file and building the Inverted Index.");
			}
		}
	}
	
	public static class URLBuildTask implements Runnable
	{
		private URL url;
		private InvertedIndex index;
		private String html;
		

		/**
		 * Initialized BuildTasks
		 * 
		 * @param url of HTML
		 * @param index to add to.
		 */
		public URLBuildTask(URL url, String html, InvertedIndex index)
		{
			log.trace("URLBuildTask initialized for path " + url);
			this.url = url;
			this.index = index;
			this.html = html;
		}

		@Override
		public void run()
		{
			log.info("This BuildTask is handled by " + Thread.currentThread().getName());
			log.trace(Thread.currentThread().getName() + " is building from " + url + " || " + index.getClass());

			InvertedIndex local = new InvertedIndex();
			
			InvertedIndexBuilder.build(url, html, local);

			index.addAll(local);
		}
	}
}
