import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Crawler
{
	private int limit;
	private HashSet<URL> urls;
	private WorkQueue queue;
	private InvertedIndex index;
	private static Logger log = LogManager.getLogger();
	
	public Crawler(InvertedIndex index)
	{
		this.queue = new WorkQueue();
		this.index = index;
		urls = new HashSet<>();
	}
	
	public void crawl(URL seed, int limit)
	{
		this.limit = limit;
		urls.add(seed);
		queue.execute(new CrawlTask(seed));
		queue.finish();
		log.info(urls.size());
	}
	

	public class CrawlTask implements Runnable
	{
		private URL url;

		public CrawlTask(URL url)
		{
			log.info("CrawlTask constructed: " + url );
			this.url = url;
		}

		@Override
		public void run()
		{
			try
			{
				InvertedIndex local = new InvertedIndex();
				String html = HTTPFetcher.fetchHTML(url.toString());
				InvertedIndexBuilder.build(url, html, local);
				ArrayList<URL> links = LinkParser.listLinks(url, html);
				log.info("found " + links.size() + " links on " + url);
				
				synchronized(index)
				{
					index.addAll(local);
				}


				for(URL u: links)
				{
					synchronized(urls)
					{
						if(urls.size() >= limit || urls.contains(u))
						{
							continue;
						}
						urls.add(u);
					}

					queue.execute(new CrawlTask(u));
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}
	}
}
