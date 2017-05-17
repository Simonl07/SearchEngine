import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser
{

	// https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a
	// https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html
	// https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL

	/**
	 * Removes the fragment component of a URL (if present), and properly
	 * encodes the query string (if necessary).
	 *
	 * @param url url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url)
	{
		try
		{
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e)
		{
			return url;
		}
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 */
	public static String fetchHTML(URL url)
	{
		int defaultPort = 80;
		String host = url.getHost();
		String resource = url.getFile();
		int port = url.getPort() < 0 ? defaultPort : url.getPort();
		String output = "";
		try (Socket socket = new Socket(host, port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());)
		{

			writer.write("GET " + resource + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n");
			writer.flush();

			int start = 0;
			while (!reader.readLine().trim().equals(""))
			{
				start++;
			}

			String line = "";
			while ((line = reader.readLine()) != null)
			{
				output += line;
			}

		} catch (Exception e)
		{
			return null;
		}
		return output;
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to
	 * absolute using the base URL and cleaned (removing fragments and encoding
	 * special characters as necessary).
	 *
	 * @param base base url used to convert relative links to absolute3
	 * @param html raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html)
	{

		if(html == null || html.length() <= 0)
		{
			return new ArrayList<URL>();
		}
		
		ArrayList<URL> links = new ArrayList<URL>();

		Matcher m = Pattern.compile("(?i)(?s)<a[^>]+?href\\s*?=\\s*?.*?>").matcher(html);

		while (m.find())
		{
			String tag = m.group().replaceAll("(?s)[\\s]+?", "");

			Matcher urlMatcher = Pattern.compile("(?i)(?s)(?<=href=\")((https?)?[^<\\>;]+?\\.?(html?)?)(?=\\\")").matcher(tag);
			String url = urlMatcher.find() ? urlMatcher.group().replaceAll("(?s)#.*", "") : "";

			URL temp = null;
			try
			{
				temp = new URL(base, url);
			} catch (MalformedURLException e)
			{
				System.out.println("Malformed URL: " + url);
				return new ArrayList<URL>();
			}

			if (!(temp.getProtocol().equals("mailto") || temp.getProtocol().equals("javascript")))
			{
				temp = clean(temp);
				links.add(temp);
			}
		}

		return links;
	}
}
