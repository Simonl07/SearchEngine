import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Writing data structures into specified path in JSON format.
 * 
 * @author Simonl0425
 *
 */
public class JSONWriter
{

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
		writer.write(indent(level - 1) + "\n]");
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
	public static void write(TreeMap<String, TreeMap<String, TreeSet<Integer>>> map, Path path) throws IOException
	{
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

}
