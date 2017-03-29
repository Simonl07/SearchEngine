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
		
		html = stripNonAlpha(html);

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

	
	// TODO Include WordParser in your project (its also useful for project 2 and 3)
	
	public static String stripNonAlpha(String html)
	{
		String output = html.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+"," ");
		return output;
	}
	
}
