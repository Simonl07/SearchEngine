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
	// TODO build(Iterable<Path> htmlFiles, InvertedIndex index)
	/**
	 * Take any iterable collection of HTML files, read the content, clean the
	 * HTML and construct the inverted Index.
	 * 
	 * @param htmlFiles ArrayList of HTML Path to read from.
	 * @throws IOException
	 */
	public static InvertedIndex build(Iterable<Path> htmlFiles) throws IOException
	{
		InvertedIndex invertedMap = new InvertedIndex(); // TODO Remove

		for (Path p: htmlFiles)
		{
			invertedMap = build(p, invertedMap);
		}
		return invertedMap; // TODO Return void
	}

	/**
	 * Take a path and an invertedIndex, read through the content, clean HTML
	 * and add words from the file to the given InvertedIndex
	 * 
	 * @param p path for the File
	 * @param i InvertedIndex for adding
	 * 
	 * @return the InvertedIndex with new words added form the file.
	 * @throws IOException
	 */
	public static InvertedIndex build(Path p, InvertedIndex i) throws IOException // TODO Better variable names
	{
		try (BufferedReader input = Files.newBufferedReader(p, StandardCharsets.UTF_8))
		{
			String line = "";
			StringBuilder content = new StringBuilder("");
			while ((line = input.readLine()) != null)
			{
				content.append(line);
				content.append("\n");
			}

			// TODO Do  not convert to lowercase...
			String[] words = WordParser.parseWords(HTMLCleaner.stripHTML(content.toString()).toLowerCase());

			i.addAll(p.toString(), words);
		}
		return i;
	}
}
