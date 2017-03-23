import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JSONWriter 
{
	
	private static String indent(int times) 
	{
		char[] tabs = new char[times];
		Arrays.fill(tabs, '\t');
		return String.valueOf(tabs);
	}
	
	
	private static void asArray(Writer writer, TreeSet<Integer> elements, int level) throws IOException 
	{
		writer.write("[\n");
		Iterator<Integer> it = elements.iterator();
		while(it.hasNext())
		{
			writer.write(indent(level) + it.next());
			
			if(it.hasNext())
			{
				writer.write(",\n");
			}else{
				writer.write("\n");
			}
		}
		writer.write(indent(level -1) + "]");
	}
		
	
	public static void write(TreeMap<String, TreeMap<Path, TreeSet<Integer>>> map, Path path) throws IOException
	{
		try(BufferedWriter output = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			output.write("{\n");
				
			for(String s: map.keySet())
			{
				if(s.equals(""))
				{continue;}else
				{
					output.write(indent(1) + "\"" + s + "\": {\n");
					for(Path p: map.get(s).keySet())
					{
						output.write(indent(2) + "\"" + p.toString() + "\": ");
						asArray(output, (TreeSet<Integer>)map.get(s).get(p),3);
						if(p == map.get(s).lastKey())
						{
							output.write("\n");
						}else{
							output.write(",\n");
						}
					}
					output.write(indent(1)+ "}");
					if(s== map.lastKey())
					{
						output.write("\n");
					}else{
						output.write(",\n");
					}
				}
			}
			output.write("}\n");
			output.flush();
		}
	}
		
		
	
}
