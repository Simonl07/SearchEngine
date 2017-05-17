import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Crawler extends HttpServlet
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
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("GET " + request.getRequestURL().toString());
		PrintWriter out = response.getWriter();
		out.printf("<html>%n");
		out.printf("<head><title>Search Engine Crawler</title></head>%n");
		out.printf("<body>%n");
		
		out.println("<h2>Crawler</h2>");
		
		out.print("<form id=\"form1\" name=\"form1\" method=\"post\" action=\"/crawler\">"
				+ "<p>url: <input type=\"text\" name=\"url\" size=\"20\" maxlength=\"70\"> limit "
				+ "<input type=\"text\" name=\"limit\" size=\"20\" maxlength=\"70\">"
				+ "<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Crawl\" /></p></form>");
		
		out.println("<p>" + urls.size() + " links crawled. </p>");
		out.println("<p>");
		synchronized(urls)
		{
			for(URL u: urls)
			{
				out.println(u + "<br/>");
			}
		}
		out.println("</p>");
		
		out.printf("</body>%n");
		out.printf("</html>%n");
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("POST " + request.getRequestURL().toString());
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
