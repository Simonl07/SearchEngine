import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Builder for building an invertedIndex Object.
 * 
 * @author Simonl0425
 *
 */
public class InvertedIndexBuilder
{
	/**
	 * Take any iterable collection of HTML files, read the content, clean the
	 * HTML and construct the inverted Index.
	 * 
	 * @param htmlFiles ArrayList of HTML Path to read from.
	 * @throws IOException
	 */
	public static void build(Iterable<Path> htmlFiles, InvertedIndex index) throws IOException
	{
		for (Path p: htmlFiles)
		{
			build(p, index);
		}
		return;
	}

	/**
	 * Take a path and an invertedIndex, read through the content, clean HTML
	 * and add words from the file to the given InvertedIndex
	 * 
	 * @param path path for the File
	 * @param index InvertedIndex for adding
	 * 
	 * @return the InvertedIndex with new words added form the file.
	 * @throws IOException
	 */
	public static void build(Path path, InvertedIndex index) throws IOException
	{
		try (BufferedReader input = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			String line = "";
			StringBuilder content = new StringBuilder("");
			while ((line = input.readLine()) != null)
			{
				content.append(line);
				content.append("\n");
			}

			String[] words = WordParser.parseWords(HTMLCleaner.stripHTML(content.toString()));

			index.addAll(path.toString(), words);
		}
		return;
	}
}
