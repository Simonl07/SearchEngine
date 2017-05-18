import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")
/**
 * This Servlet responsible for handling search history and visit history, using
 * cookies.
 * 
 * @author Simonl0425
 *
 */
public class CookiesConfigServlet extends HttpServlet
{
	private static boolean DNT = false;
	private static Logger log = LogManager.getLogger();

	/**
	 * return tracking
	 * 
	 * @return boolean of Do not track, true for private mode, false for
	 *         tracking mode.
	 */
	public static boolean getDNT()
	{
		return DNT;
	}

	/**
	 * Set tracking
	 * 
	 * @param DNT boolean, true for private mode, false for tracking mode.
	 */
	public static void setDNT(boolean DNT)
	{
		CookiesConfigServlet.DNT = DNT;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		log.info("GET " + request.getRequestURL().toString());

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		out.printf("<html>%n");
		out.printf("<head><title>Search Engine V1.0</title></head>%n");
		out.printf("<body>%n");

		Map<String, Cookie> cookies = getCookieMap(request);

		out.print("<h2>Search History: </h2>");
		Cookie queries = cookies.get("queries");
		if (queries != null)
		{
			out.println("<p>");
			String decoded = URLDecoder.decode(queries.getValue(), StandardCharsets.UTF_8.name());
			System.out.println(decoded);
			for (String query: decoded.split(","))
			{
				if (!query.trim().isEmpty())
				{
					out.printf("[%s]<br/>", query);
				}
			}
			out.println("</p>");
		} else
		{
			out.print("<p>No search history</p>");
			String encoded = URLEncoder.encode(",", StandardCharsets.UTF_8.name());
			queries = new Cookie("queries", encoded);

		}
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getRequestURI());
		out.printf("\t<input type=\"submit\" name=\"clear\" value=\"Clear search history\">%n");
		out.printf("</form>%n");

		out.print("<h2>Visit History: </h2>");
		Cookie visited = cookies.get("visited");
		if (visited != null)
		{
			out.println("<p>");
			String decoded = URLDecoder.decode(visited.getValue(), StandardCharsets.UTF_8.name());
			for (String url: decoded.split(","))
			{
				if (!url.trim().isEmpty())
				{
					out.println("<a href=\"" + url + "\"> " + url + "</a><br/>");
				}
			}
			out.println("</p>");
		} else
		{
			out.print("<p>No visit history</p>");
			String encoded = URLEncoder.encode(",", StandardCharsets.UTF_8.name());
			visited = new Cookie("visited", encoded);
		}
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getRequestURI());
		out.printf("\t<input type=\"submit\" name=\"clear\" value=\"Clear visit history\">%n");
		out.printf("\t<br/><br/><br/><input type=\"submit\" name=\"clear\" value=\"Clear all history and cookies\">%n");
		out.printf("\t<a href=\"/\">back</a>%n");
		out.printf("</form>%n");

		out.printf("</body>%n");
		out.printf("</html>%n");
		response.addCookie(queries);
		response.addCookie(visited);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("POST " + request.getRequestURL().toString());
		Map<String, Cookie> cookies = getCookieMap(request);

		String value = request.getParameter("clear");
		if (value.equals("Clear search history"))
		{
			Cookie queries = cookies.get("queries");
			if (queries != null)
			{
				queries.setValue(null);
				queries.setMaxAge(0);
				response.addCookie(queries);
			}
		} else if (value.equals("Clear visit history"))
		{
			Cookie visited = cookies.get("visited");
			if (visited != null)
			{
				visited.setValue(null);
				visited.setMaxAge(0);
				response.addCookie(visited);
			}
		} else if (value.equals("Clear all history and cookies"))
		{
			clearCookies(request, response);
		}

		response.sendRedirect(request.getServletPath());
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Gets the cookies form the HTTP request, and maps the cookie name to the
	 * cookie object.
	 *
	 * @param request - HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public static Map<String, Cookie> getCookieMap(HttpServletRequest request)
	{
		HashMap<String, Cookie> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null)
		{
			for (Cookie cookie: cookies)
			{
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}

	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request - HTTP request
	 * @param response - HTTP response
	 */
	public void clearCookies(HttpServletRequest request, HttpServletResponse response)
	{

		Cookie[] cookies = request.getCookies();

		if (cookies != null)
		{
			for (Cookie cookie: cookies)
			{
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

}
