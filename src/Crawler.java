import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Crawler
{
	private final URL seed;
	private final int limit;
	private HashSet<URL> urls;
	private WorkQueue queue;
	private InvertedIndex index;
	private static Logger log = LogManager.getLogger();
	
	public Crawler(InvertedIndex index, URL seed, int limit)
	{
		this.seed = seed;
		this.limit = limit;
		this.queue = queue;
		this.index = index;
		urls = new HashSet<>();
	}
	
	
	
	public void crawl(URL base)
	{
		if(urls.size() >= limit || urls.contains(base))
		{
			return;
		}
		
		ArrayList<URL> list = null;
		try
		{
			String content = HTTPFetcher.fetchHTML(base.toString());
			if(content == null)
			{
				return;
			}
			urls.add(base);
			
			InvertedIndexBuilder.build(base, content, index);
			list = LinkParser.listLinks(base, content);	
			log.debug("Crawler found " + list.size() + " links in this page");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		for(URL u: list)
		{
			crawl(u);
		}
	}

	@SuppressWarnings("unchecked")
	public HashSet<URL> getResults()
	{
		return (HashSet<URL>) urls.clone();
	}

	public void start(WorkQueue queue)
	{
		this.queue = queue;
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
			log.info("This CrawlTask is handled by " + Thread.currentThread().getName());
			synchronized (urls)
			{
				
				if (urls.size() >= limit || urls.contains(base))
				{
					log.warn("OVERLIMIT or ALREADY FOUND");
					return;
				}
				urls.add(base);
				log.info("URL Size: " + urls.size());
				System.out.println(urls.size() + ". Crawling " + base);
			}
			
			
			ArrayList<URL> list = null;
			try
			{
				String content = HTTPFetcher.fetchHTML(base.toString());
				
				queue.execute(new ThreadedInvertedIndexBuilder.URLBuildTask(base, content == null? "" : content, index));
				list = LinkParser.listLinks(base, content);
				log.debug("Crawler found " + list.size() + " links in this page");
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
