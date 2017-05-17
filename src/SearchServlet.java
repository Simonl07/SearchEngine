import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
	private boolean exact;

	public SearchServlet(InvertedIndex index, QueryHandler queryHandler)
	{
		this.index = index;
		this.queryHandler = queryHandler;
		exact = false;
	}

	private static Logger log = LogManager.getLogger();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info(Thread.currentThread().getName() + ": " + request.getRequestURI());

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String query = request.getParameter("query");
		String visitURL = request.getParameter("visit");
		Map<String, Cookie> cookies = CookiesConfigServlet.getCookieMap(request);

		out.printf("<html>%n");
		out.printf("<head><title>Search Engine</title></head>%n");
		out.printf("<body>%n");
		out.print("<h1>Search Engine</h1>");
		
		out.print(CookiesConfigServlet.getDNT() ? "<p>Your activities will not be tracked.</p>" : "");

		if (CookiesConfigServlet.getDNT() == false)
		{
			Cookie queries = cookies.get("queries");
			if (queries != null)
			{
				String decoded = URLDecoder.decode(queries.getValue(), StandardCharsets.UTF_8.name());
				String encoded = URLEncoder.encode(decoded + "," + (query == null ? "" : query), StandardCharsets.UTF_8.name());
				queries.setValue(encoded);
			} else
			{
				String encoded = URLEncoder.encode(",", StandardCharsets.UTF_8.name());
				queries = new Cookie("queries", encoded);

			}
			response.addCookie(queries);

			if (visitURL != null)
			{
				Cookie visited = cookies.get("visited");
				if (visited != null)
				{
					String decoded = URLDecoder.decode(visited.getValue(), StandardCharsets.UTF_8.name());
					String encoded = URLEncoder.encode(decoded + "," + visitURL, StandardCharsets.UTF_8.name());
					visited.setValue(encoded);
				} else
				{
					String encoded = URLEncoder.encode(",", StandardCharsets.UTF_8.name());
					visited = new Cookie("visited", encoded);

				}
				response.addCookie(visited);
				response.sendRedirect(visitURL);
			}

			Cookie lastVisit = cookies.get("lastVisit");
			Cookie visitCount = cookies.get("visitCount");
			if (lastVisit != null && visitCount != null)
			{
				String decodedLastVisit = URLDecoder.decode(lastVisit.getValue(), StandardCharsets.UTF_8.name());
				int count = Integer.parseInt(visitCount.getValue());
				out.println("<p>Welcome, this is your " + count + " visit, your last visit was on " + decodedLastVisit + "</p>");
				String encodedLastVisit = URLEncoder.encode(getLongDate(), StandardCharsets.UTF_8.name());
				lastVisit.setValue(encodedLastVisit);
				visitCount.setValue(Integer.toString(count + 1));
			} else
			{
				out.println("<p>Welcome this is your first time visit</p>");
				String encoded = URLEncoder.encode(getLongDate(), StandardCharsets.UTF_8.name());
				lastVisit = new Cookie("lastVisit", encoded);
				visitCount = new Cookie("visitCount", "1");
			}

			response.addCookie(lastVisit);
			response.addCookie(visitCount);
		}

		printForm(request, response);

		if (query != null)
		{
			long start = System.currentTimeMillis();
			List<SearchResult> results = search(query);
			long end = System.currentTimeMillis();
			if(results != null){
			out.println("<p>" + results.size() + " results. (" + (end - start) / 1000.0 + " seconds)</p>");

			for (SearchResult result: results)
			{
				out.println("<a href=\"" + request.getServletPath() + "?visit=" + result.getPath() + "\">" + result.getPath() + "</a><br/>\n");
			}
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

		if (request.getParameterValues("mode") != null)
		{
			List<String> mode = Arrays.asList(request.getParameterValues("mode"));
			exact = mode.contains("partial") ? false : true;
			CookiesConfigServlet.setDNT(mode.contains("private") ? true : false);
		} else
		{
			exact = true;
			CookiesConfigServlet.setDNT(false);
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath() + "?query=" + query);

	}

	private void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();

		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"2\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"checkbox\" name=\"mode\" value=\"partial\" " + (exact ? "" : "checked") + ">Partial Search %n");
		out.printf("\t</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t&nbsp;&nbsp;<input type=\"checkbox\" name=\"mode\" value=\"private\" " + (CookiesConfigServlet.getDNT() ? "checked" : "") + "> Private Search%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Query:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"query\" maxlength=\"50\" size=\"40\"> %n");
		out.printf("\t</td>%n");
		out.printf("\t<td>%n");
		out.printf("<input type=\"submit\" value=\"Search\">");
		out.printf("\t</td>%n");
		out.printf("\t<td>%n");
		out.printf("<p> <a href=\"/history\">view history</a>&nbsp;&nbsp;&nbsp;<a href=\"/crawler\">web crawler</a></p>\n%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");

		out.printf("</form>\n%n");
	}

	private List<SearchResult> search(String query)
	{
		queryHandler.parse(query, exact);
		
		String queries[] = WordParser.parseWords(query);
		Arrays.sort(queries);
		query = String.join(" ", queries);

		List<SearchResult> results = queryHandler.getResultsMap().get(query);

		System.out.println(queryHandler.getResultsMap());
		
		return results;
	}

	/**
	 * Returns the current date and time in a long format.
	 *
	 * @return current date and time
	 * @see #getShortDate()
	 */
	public static String getLongDate()
	{
		String format = "hh:mm:ss a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	/**
	 * Returns the current date and time in a short format.
	 *
	 * @return current date and time
	 * @see #getLongDate()
	 */
	public static String getShortDate()
	{
		String format = "yyyy-MM-dd hh:mm a";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(Calendar.getInstance().getTime());
	}

}
