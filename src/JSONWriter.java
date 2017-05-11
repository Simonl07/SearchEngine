import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Writing data structures into specified path in JSON format.
 * 
 * @author Simonl0425
 *
 */
public class JSONWriter
{

	private static Logger log = LogManager.getLogger();

	/**
	 * A private helper method that returns the String of indentation .
	 * 
	 * @param times how many time is the String indented.
	 * @return The indenting empty String that fill with times amount of \t
	 * 
	 */
	private static String indent(int times)
	{
		char[] tabs = new char[times];
		Arrays.fill(tabs, '\t');
		return String.valueOf(tabs);
	}

	/**
	 * Writes the set of elements as a JSON array at the specified indent level.
	 *
	 * @param writer writer to use for output
	 * @param elements to write as JSON array
	 * @param level number of times to indent the array itself
	 * @throws IOException
	 */
	public static void asArray(Writer writer, SortedSet<Integer> elements, int level) throws IOException
	{
		writer.write("[\n");
		Iterator<Integer> it = elements.iterator();
		writer.write(indent(level) + it.next());
		while (it.hasNext())
		{
			writer.write(",\n" + indent(level) + it.next());
		}
		writer.write( "\n" + indent(level-1) +"]");
	}

	/**
	 * Writes the set of elements as a JSON object with a nested array to the
	 * path, using asArray method
	 *
	 * @param elements to write as a JSON object with a nested array
	 * @param path path of the file
	 * @throws IOException
	 */
	public static void asNestedObject(Writer writer, TreeMap<String, TreeSet<Integer>> elements, int level) throws IOException
	{
		writer.write("{\n");
		for (String p: elements.keySet())
		{
			writer.write(indent(level) + "\"" + p + "\"" + ": ");
			asArray(writer, elements.get(p), level + 1);
			if (p == elements.lastKey())
			{
				writer.write("\n");
			} else
			{
				writer.write(",\n");
			}
		}
		writer.write(indent(level - 1) + "}");
	}

	/**
	 * Write the nested TreeMap<String, TreeMap<String, TreeSet<Integer>>>
	 * structure to the path in JSON format, using asNestedObject and asArray
	 * method.
	 * 
	 * @param map the TreeMap that contains all the elements
	 * @param path the path to write in
	 * @throws IOException
	 */
	public static void writeInvertedIndex(TreeMap<String, TreeMap<String, TreeSet<Integer>>> map, Path path) throws IOException
	{
		log.trace("Writing invertedIndex into " + path.toString());
		try (BufferedWriter output = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			output.write("{\n");

			for (String s: map.keySet())
			{
				if (s.equals(""))
				{
					continue;
				} else
				{
					output.write(indent(1) + "\"" + s + "\": ");
					asNestedObject(output, map.get(s), 2);
					if (s == map.lastKey())
					{
						output.write("\n");
					} else
					{
						output.write(",\n");
					}
				}
			}
			output.write("}\n");
			output.flush();
		}
	}

	/**
	 * Write the TreeMap of query and a list of SearchResult objects into the
	 * given path in JSON format.
	 * 
	 * @param path to write JSON file
	 * @param input the data structure for writing search results
	 * @throws IOException
	 */
	public static void writeSearchResults(Path path, TreeMap<String, List<SearchResult>> input) throws IOException
	{
		log.trace("Writing search results into " + path.toString());
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			writer.write("[\n");
			for (String queries: input.keySet())
			{
				asSingleSearch(writer, queries, input.get(queries), 1);
				if (queries == input.lastKey())
				{
					writer.write("\n");
				} else
				{
					writer.write(",\n");
				}
			}
			writer.write("]\n");
		}
	}

	/**
	 * Helper method to write a single Search with queries.
	 * 
	 * @param writer
	 * @param queries String of queries used in this search
	 * @param results a list of SearchResult objects.
	 * @param level indentation level
	 * @throws IOException
	 */
	public static void asSingleSearch(Writer writer, String queries, List<SearchResult> results, int level) throws IOException
	{
		writer.write(indent(level) + "{\n");
		writer.write(indent(level + 1) + "\"queries\": " + "\"" + queries + "\",\n");
		writer.write(indent(level + 1) + "\"results\": ");
		writer.write("[");
		Iterator<SearchResult> iterator = results.iterator();
		if (iterator.hasNext())
		{
			writer.write("\n");
			asSearchResult(writer, iterator.next(), level + 2);
			while (iterator.hasNext())
			{
				writer.write(",\n");
				asSearchResult(writer, iterator.next(), level + 2);
			}
		}
		writer.write("\n" + indent(level + 1) + "]");
		writer.write("\n" + indent(level) + "}");
	}

	/**
	 * write individual search results in JSON format
	 * 
	 * @param writer
	 * @param result SearchResult object
	 * @param level indentation level
	 * @throws IOException
	 */
	public static void asSearchResult(Writer writer, SearchResult result, int level) throws IOException
	{
		writer.write(indent(level) + "{\n");
		writer.write(indent(level + 1) + "\"where\": " + "\"" + result.getPath() + "\"" + ",\n");
		writer.write(indent(level + 1) + "\"count\": " + result.getFrequency() + ",\n");
		writer.write(indent(level + 1) + "\"index\": " + result.getInitialPosition() + "\n");
		writer.write(indent(level) + "}");
	}

}
