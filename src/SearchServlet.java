import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchServlet extends HttpServlet
{

	private InvertedIndex index;
	private QueryHandler queryHandler;

	public SearchServlet(InvertedIndex index, QueryHandler queryHandler)
	{
		this.index = index;
		this.queryHandler = queryHandler;
	}

	private static Logger log = LogManager.getLogger();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println(Thread.currentThread().getName() + ": " + request.getRequestURI());

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String query = request.getParameter("query");
		
		
		out.printf("<html>%n");
		out.printf("<head><title>Search Engine V1.0</title></head>%n");
		out.printf("<body>%n");
		
		printForm(request, response);
		
		
		
		if (query != null)
		{
			TreeMap<String, List<SearchResult>> results = search(query);

			for (String q: results.keySet())
			{
				for (SearchResult result: results.get(q))
				{
					out.printf("<a href=\"" + result.getPath() + "\">" + result.getPath() + "</a><br/>");
				}
			}
			if(CookiesConfigServlet.getDNT() == false)
			{
				Map<String, Cookie> cookies = CookiesConfigServlet.getCookieMap(request);
				Cookie queries = cookies.get("query");
				if(queries != null)
				{
					queries.setValue(queries.getValue() + "," + query);
				}else{
					queries = new Cookie("query", "");
				}
				response.addCookie(queries);
			}
			
		}
		
		out.printf("</body>%n");
		out.printf("</html>%n");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String query = request.getParameter("query") == null ? "" : request.getParameter("query");

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath() + "?query=" + query);
	}

	private void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Query:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"query\" maxlength=\"50\" size=\"50\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\" value=\"Search\"><a href=\"/history\">view history</a></p>\n%n");
		out.printf("</form>\n%n");
	}

	private TreeMap<String, List<SearchResult>> search(String query)
	{
		queryHandler.parse(query, false);
		
		TreeMap<String, List<SearchResult>> results = queryHandler.getResultsMap();

		return results;
	}

}
