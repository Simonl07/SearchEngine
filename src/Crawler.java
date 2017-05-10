import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class Crawler
{
	private final URL seed;
	private final int limit;
	private HashSet<URL> urls;
	private WorkQueue queue;

	public Crawler(WorkQueue queue, URL seed, int limit)
	{
		this.seed = seed;
		this.limit = limit;
		this.queue = queue;
		urls = new HashSet<>();
	}

	@SuppressWarnings("unchecked")
	public HashSet<URL> getResults()
	{
		return (HashSet<URL>) urls.clone();
	}

	public void start()
	{
		queue.execute(new CrawlTask(seed));
		queue.finish();
	}

	public class CrawlTask implements Runnable
	{
		private URL base;

		public CrawlTask(URL base)
		{
			this.base = base;
		}

		@Override
		public void run()
		{
			synchronized (urls)
			{
				if (urls.size() >= limit)
				{
					return;
				}
				urls.add(base);
				System.out.println(urls.size() + ". Crawling " + base);
			}
			ArrayList<URL> list = null;
			try
			{
				list = LinkParser.listLinks(base, HTTPFetcher.fetchHTML(base.toString()));
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			for (URL u: list)
			{
				queue.execute(new CrawlTask(u));
			}

		}
	}
}
