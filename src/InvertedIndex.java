import java.nio.file.Path;
import java.util.*;

public class InvertedIndex {
	private Map<String, Map<Path, Set<Integer>>> invertedMap;
	
	public InvertedIndex()
	{
		invertedMap = new TreeMap<String, Map<Path, Set<Integer>>>();
	}
	
	public void addWord(String word, Path path, int index)
	{
		if(word == null)
			return;
		
		if(invertedMap.containsKey(word))
		{
			Map<Path, Set<Integer>> tempMap = (TreeMap<Path, Set<Integer>>)invertedMap.get(word);
			if(tempMap.containsKey(path))
			{
				Set<Integer> tempSet = (TreeSet<Integer>)tempMap.get(path);
				tempSet.add(index);
			}else{
				TreeSet<Integer> newSet = new TreeSet<>();
				newSet.add(index);
				tempMap.put(path, newSet);
			}
		}else{
			Set<Integer> indices = new TreeSet<>();
			indices.add(index);
			Map<Path, Set<Integer>> paths =  new TreeMap<>();
			paths.put(path, indices);
			invertedMap.put(word, paths);
		}
		
	}
	
	
	public String toString()
	{
		String output = "";
		for(String s: invertedMap.keySet())
		{
			output += s + "\n";
			for(Path p: invertedMap.get(s).keySet())
			{
				output += "\t"+p+ "\n";
				for(Integer i: invertedMap.get(s).get(p))
				{
					output += "\t\t"+i+ "\n";
				}
			}
		}
		return output;
	}
	
	
}
