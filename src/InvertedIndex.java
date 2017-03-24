import java.nio.file.Path;
import java.util.*;

public class InvertedIndex {
	// TODO final
	private TreeMap<String, TreeMap<Path, TreeSet<Integer>>> invertedMap;
	
	public InvertedIndex()
	{
		invertedMap = new TreeMap<String, TreeMap<Path, TreeSet<Integer>>>();
	}
	
	public void addWord(String word, Path path, int index)
	{
		if(word == null) // TODO Don't check
			return;
		
		if(invertedMap.containsKey(word))
		{
			TreeMap<Path, TreeSet<Integer>> tempMap = invertedMap.get(word);
			if(tempMap.containsKey(path))
			{
				TreeSet<Integer> tempSet = tempMap.get(path);
				tempSet.add(index);
			}else{
				TreeSet<Integer> newSet = new TreeSet<>();
				newSet.add(index);
				tempMap.put(path, newSet);
			}
		}else{
			TreeSet<Integer> indices = new TreeSet<>();
			indices.add(index);
			TreeMap<Path, TreeSet<Integer>> paths =  new TreeMap<>();
			paths.put(path, indices);
			invertedMap.put(word, paths);
		}
		
	}
	
	public int size()
	{
		return invertedMap.size();
	}
	
	// TODO Breaks encapsulation, need to remove
	public TreeMap<String, TreeMap<Path, TreeSet<Integer>>> getStructure()
	{
		return invertedMap;
	}

	// TODO 
//	public void toJSON(Path path) throws IOException {
//		JSONWriter.write(this.invertedMap, path);
//	}
	
	// TODO Either invertedMap.toString() or use a StringBuilder
	public String toString()
	{
		String output = "";
		for(String s: invertedMap.keySet())
		{
			if(s.equals(""))
			{continue;}else{
			output += s + "\n";
			for(Path p: invertedMap.get(s).keySet())
			{
				output += "\t"+p+ "\n";
				for(Integer i: invertedMap.get(s).get(p))
				{
					output += "\t\t"+i+ "\n";
				}
			}}
		}
		return output;
	}
	
	/*
	 * TODO Add some more data-structure like methods to make more general
	 * contains(String word), contains(String word, String path), etc.
	 * size(String word), size(String word, String path), etc. 
	 */
}
