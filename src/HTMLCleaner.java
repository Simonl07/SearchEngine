import java.util.*;

public class HTMLCleaner {
	
	// TODO Uh oh... We should debug what happened that you had to modify stripHTML
	
	public static String stripHTML(String html)
	{
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");

		html = stripTags(html);
		
		html = stripEntities(html);
		
		html = stripA0(html);
		
		html = stripNonAlpha(html);
		
		html = stripThinSpace(html);

		return html.toLowerCase();
		
	}
	
	public static String[] stripHTML(String[] html)
	{
		for(int i = 0; i < html.length;i++)
		{
			html[i] = stripHTML(html[i]);
		}
		return html;
	}
	
	public static String stripEntities(String html) 
	{
		String output = html.replaceAll("&[^\\s]+?;", " ");
		return output;	
	}
	
	public static String stripComments(String html) 
	{
		String output = html.replaceAll("(?s)<!-.*?->", " ");
		return output;
	}
	
	public static String stripTags(String html) 
	{	
		String output = html.replaceAll("<[^>]*?>", " ");
		return output;
	}

	public static String stripElement(String html, String name) 
	{
		String output = html.replaceAll("(?s)(?i)<"+name+".*?</"+name+".*?>", " ");
		return output;
	}
	
	public static String stripSpaces(String html)
	{
		String output = html.replaceAll("\\p{Space}+", " ");
		return output;
	}
	
	public static String stripPunct(String html)
	{
		String output= html.replaceAll("\\p{Punct}+", " ");
		return output;
	}
	
	
	public static String stripA0(String html)
	{
		String output = html.replaceAll("\\xa0", " ");
		return output;
	}
	
	public static String stripThinSpace(String html)
	{
		String output = html.replaceAll("\u2009", " ");
		return output;
	}

	public static String stripAp(String html)
	{
		String output = html.replaceAll("[\"\'\"]", " ");
		return output;
	}
	
	// TODO Include WordParser in your project (its also useful for project 2 and 3)
	
	public static String stripNonAlpha(String html)
	{
		String output = html.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+"," ");
		return output;
	}
	
}
