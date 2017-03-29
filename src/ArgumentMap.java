import java.util.*;


public class ArgumentMap {

	// TODO Make this final
	private final HashMap<String, String> map;
	
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
		return map.get(flag) != null; // TODO The only line you need in this method
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
		input = input.trim();
		if(input == null)
		{
			return false;
		}else{
			return (input.startsWith("-")) && (input.length() >= 2);
		}
		
		// TODO Rethink this and isValue
//		input = input.trim();
//		return input.startsWith("-") && input.length() >= 2; 
	}
	
	private boolean isValue(String input)
	{
		input = input.trim();
		if(input == null)
		{
			return false;
		}else{
			return (!input.startsWith("-")) && (input.length() >= 1);
		}
	}
	/**
	 * Returns the value for the specified flag as String.
	 *
	 * @param flag flag to get value for
	 * @return value as a String.
	 */
	public String getString(String flag)
	{
		return map.get(flag);
	}
	
	
	/**
	 * Return the value of a specific flag, if the flag has no value, return the default value.
	 * 
	 * @param flag flag to get value
	 * @param defaultValue return default Value if the flag or value is missing
	 * @return the value of the flag, if the flag or the value does not exist, return default value.
	 */
	public String getString(String flag, String defaultValue)
	{
		if(hasValue(flag))
		{
			return getString(flag);
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of a specific flag, if the flag has no value or cannot be parse to int
	 * return default value.
	 *
	 * @param flag flag to get value for
	 * @param defaultValue value to return if the flag or value is missing       
	 * @return value of flag as an int, or the default value.
	 */
	public int getInteger(String flag, int defaultValue)
	{
		if(hasValue(flag))
		{
			try
			{
				return Integer.parseInt(map.get(flag));
			}catch(NumberFormatException e)
			{
				return defaultValue;
			}
		}else{ 
			return defaultValue;
		}
	}
	
	@Override
	public String toString()
	{
		return map.toString();
	}
}
