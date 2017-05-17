import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CookiesConfigServlet extends HttpServlet
{
	private static boolean DNT = false;
	private static Logger log = LogManager.getLogger();
	
	public static boolean getDNT()
	{
		return DNT;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("GET " + request.getRequestURL().toString());

		if (request.getRequestURI().endsWith("favicon.ico")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.printf("<html>%n");
		out.printf("<head><title>Search Engine V1.0</title></head>%n");
		out.printf("<body>%n");
		
		Map<String, Cookie> cookies = getCookieMap(request);
		System.out.println("Cookies: " + cookies);
		
		
		Cookie queries = cookies.get("query");
		System.out.println("Queries: " + queries);
		out.print("<h2>Search History: </h2>");
		if(queries != null)
		{
			out.println("<p>");
			for(String query: queries.getValue().split(","))
			{
				out.printf("[%s]<br/>", query);
			}
			out.println("</p>");
		}else{
			out.print("<p>No search history</p>");
			queries = new Cookie("query", "");
		}
		System.out.println( request.getRequestURI());
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getRequestURI());
		out.printf("\t<input type=\"submit\" value=\"Clear Search History\">%n");
		out.printf("</form>%n");
		
		out.printf("</body>%n");
		out.printf("</html>%n");
		response.addCookie(queries);
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log.info("POST " + request.getRequestURL().toString());
		
		clearCookies(request, response);
		
		response.sendRedirect(request.getServletPath());
		response.setStatus(HttpServletResponse.SC_OK);
	}
	/**
	 * Gets the cookies form the HTTP request, and maps the cookie name to the
	 * cookie object.
	 *
	 * @param request
	 *            - HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
		HashMap<String, Cookie> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie);
			}
		}

		return map;
	}
	
	/**
	 * Clears all of the cookies included in the HTTP request.
	 *
	 * @param request
	 *            - HTTP request
	 * @param response
	 *            - HTTP response
	 */
	public void clearCookies(HttpServletRequest request, HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

}
