import java.util.*;


public class ArgumentMap {

	// TODO Make this final
	private HashMap<String, String> map;
	
	public ArgumentMap()
	{
		map = new HashMap<>();
	}
	
	public ArgumentMap(String[] args) 
	{
		this();
		parse(args);
	}
	
	
	public int numFlags()
	{
		return map.size();
	}

	public boolean hasFlag(String flag)
	{
		return map.containsKey(flag);
	}
	
	public boolean hasValue(String flag)
	{
		if(hasFlag(flag))
		{
			return map.get(flag) != null; // TODO The only line you need in this method
		}
		return false;
	}
	
	public boolean hasBoth(String flag) // TODO Remove?
	{
		return hasFlag(flag) && hasValue(flag); 
	}
	
	
	
	public void parse(String[] args)
	{
		for(int i = 0; i < args.length;i++)
		{
			String input = args[i];
			
			if(isFlag(input))
			{
				map.put(input, null);
			}else if(isValue(input) && i != 0 && isFlag(args[i-1])){
				map.put(args[i-1], input);
			}
		}
	}
	
	private boolean isFlag(String input)
	{
		// TODO Always use braces with if/else even if a 1 line block 
		// TODO See the goto fail; apple bug
		if(input == null)
			return false;
		else
			return (input.startsWith("-")) && (input.length() >= 2) && (input.indexOf(" ") == -1);
		
		// TODO Rethink this and isValue
//		input = input.trim();
//		return input.startsWith("-") && input.length() >= 2; 
	}
	
	private boolean isValue(String input)
	{
		if(input == null)
			return false;
		else
			return (!input.startsWith("-")) && (input.length() >= 1) && !(input.startsWith(" ")) && !input.contains("\t");
	}
	
	// TODO Add back in... getString(String flag, String defaultValue) and the getInteger version
	
	public String getValue(String flag)
	{
		return map.get(flag);
	}
	
	// TODO @Override 
	// TODO map.toString() or... use a StringBuilder
	public String toString()
	{
		String output = "";
		for(String key: map.keySet())
		{
			output += "Flag: " + key + "  Value: " + map.get(key) + "\n";
		}
		return output;
	}
}
