/**
 * Clean all HTML elements in HTML String.
 * 
 * @author Simonl0425
 *
 */
public class HTMLCleaner {
	
	// TODO Uh oh... We should debug what happened that you had to modify stripHTML
	
	/**
	 * Removes all HTML (including any CSS and JavaScript).
	 *
	 * @param html	text including HTML to remove
	 * @return text without any HTML, CSS, or JavaScript
	 */
	public static String stripHTML(String html)
	{
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");

		html = stripTags(html);
		
		html = stripEntities(html);
		
		html = stripNonAlpha(html); // TODO Remove

		return html.toLowerCase(); // TODO Remove tolowercase
		
	}
	
	/**
	 * Clean an array of HTML String.
	 * 
	 * @param html	String array of HTML to be cleaned
	 * @return Cleaned array of HTML in the given order.
	 */
	public static String[] stripHTML(String[] html)
	{
		for(int i = 0; i < html.length;i++)
		{
			html[i] = stripHTML(html[i]);
		}
		return html;
	}
	
	/**
	 * Replaces all HTML entities with a single space.
	 *
	 * @param html	text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) 
	{
		String output = html.replaceAll("&[^\\s]+?;", " ");
		return output;	
	}
	
	/**
	 * Replaces all HTML comments with a single space.
	 *
	 * @param html	text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) 
	{
		String output = html.replaceAll("(?s)<!-.*?->", " ");
		return output;
	}
	
	/**
	 * Replaces all HTML tags with a single space.
	 *
	 * @param html	text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) 
	{	
		String output = html.replaceAll("<[^>]*?>", " ");
		return output;
	}

	/**
	 * Replaces everything between the element tags and the element tags
	 * themselves with a single space. 
	 * 
	 * @param html	text including HTML elements to remove
	 * @param name	name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) 
	{
		String output = html.replaceAll("(?s)(?i)<"+name+".*?</"+name+".*?>", " ");
		return output;
	}

	
	// TODO Include WordParser in your project (its also useful for project 2 and 3)
	// TODO Remove this method here
	/**
	 * Replace non-alphabetic character inside the HTML with a single space
	 * 
	 * @param html text include non-alphabetic character to be replaced
	 * @return text without nonalpha characters.
	 */
	public static String stripNonAlpha(String html)
	{
		String output = html.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+"," ");
		return output;
	}
	
}
