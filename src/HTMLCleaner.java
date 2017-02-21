import java.util.*;

public class HTMLCleaner {
	
	
	public static String stripHTML(String html)
	{
		html = stripComments(html);

		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");

		html = stripTags(html);
		
		html = stripEntities(html);
		
		html = stripPunct(html);

		html = stripNumbers(html);
		
		html = stripInWordNumbers(html);
		
		html = stripA0(html);
		
		html = stripAp(html);
		
		html = stripNonWord(html);
		
		html = stripSpaces(html);
		
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
	
	public static String stripNumbers(String html)
	{
		String output = html.replaceAll("\\b\\d+\\b", " ");
		return output;
	}
	
	public static String stripInWordNumbers(String html)
	{
		String output = html.replaceAll("\\d", " ");
		return output;
	}
	
	public static String stripA0(String html)
	{
		String output = html.replaceAll("\\xa0", " ");
		return output;
	}

	public static String stripAp(String html)
	{
		String output = html.replaceAll("[\"\'\"]", " ");
		return output;
	}
	
	public static String stripNonWord(String html)
	{
		String output = html.replaceAll("[\\W&&[^\\p{L}]]"," ");
		return output;
	}
	
}
