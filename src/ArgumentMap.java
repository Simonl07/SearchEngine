import java.util.*;

/**
 * Parse command line arguments into a hashmap of flag and value.
 * 
 * @author Simonl0425
 *
 */
public class ArgumentMap
{
	private final HashMap<String, String> map;

	/**
	 * Constructor to Initialize the HashMap.
	 */
	public ArgumentMap()
	{
		map = new HashMap<>();
	}

	/**
	 * Constructor that initialize the map and parse the arguments.
	 * 
	 * @param args String array of arguments for parsing.
	 */
	public ArgumentMap(String[] args)
	{
		this();
		parse(args);
	}

	/**
	 * Return number of flags in the map.
	 * 
	 * @return number of flags in the map.
	 */
	public int numFlags()
	{
		return map.size();
	}

	/**
	 * Return whether the map contains specific flag.
	 * 
	 * @param flag to check.
	 * @return true if map contain the flag, false otherwise.
	 */
	public boolean hasFlag(String flag)
	{
		return map.containsKey(flag);
	}

	/**
	 * Return whether the map contains value of a specific flag.
	 * 
	 * @param flag to check if the map contains value.
	 * @return true if map contain the flag and has value, false otherwise.
	 */
	public boolean hasValue(String flag)
	{
		return map.get(flag) != null;
	}

	/**
	 * parse arguments into HashMap<String, String>, if flag has no value, add
	 * null as value.
	 * 
	 * @param args arguments for parsing.
	 */
	public void parse(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			String input = args[i];

			if (isFlag(input))
			{
				map.put(input, null);
			} else if (isValue(input) && i != 0 && isFlag(args[i - 1]))
			{
				map.put(args[i - 1], input);
			}
		}
	}

	/**
	 * Helper method to check if the input follow the format of a flag.
	 * 
	 * @param input String to check if it is a flag.
	 * @return true if format correct, false otherwise.
	 */
	private boolean isFlag(String input)
	{
		input = input.trim();
		if (input == null)
		{
			return false;
		} else
		{
			return (input.startsWith("-")) && (input.length() >= 2);
		}
	}

	/**
	 * Helper method to check if the input follow the format of a value.
	 * 
	 * @param input String to check if it is a value.
	 * @return true if format correct, false otherwise.
	 */
	private boolean isValue(String input)
	{
		input = input.trim();
		if (input == null)
		{
			return false;
		} else
		{
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
	 * Return the value of a specific flag, if the flag has no value, return the
	 * default value.
	 * 
	 * @param flag flag to get value
	 * @param defaultValue return default Value if the flag or value is missing
	 * @return the value of the flag, if the flag or the value does not exist,
	 *         return default value.
	 */
	public String getString(String flag, String defaultValue)
	{
		if (hasValue(flag))
		{
			return getString(flag);
		} else
		{
			return defaultValue;
		}
	}

	/**
	 * Returns the value of a specific flag, if the flag has no value or cannot
	 * be parse to int return default value.
	 *
	 * @param flag flag to get value for
	 * @param defaultValue value to return if the flag or value is missing
	 * @return value of flag as an int, or the default value.
	 */
	public int getInteger(String flag, int defaultValue)
	{
		try
		{
			return Integer.parseInt(map.get(flag));
		} catch (Exception e)
		{
			return defaultValue;
		}
	}

	public int getInteger(String flag)
	{
		try
		{
			return Integer.parseInt(map.get(flag));
		} catch (Exception e)
		{
			return -1;
		}
	}

	@Override
	/**
	 * Redefined toString method that return the toString() result of HashMap.
	 * 
	 * @return toString result of HashMap.
	 */
	public String toString()
	{
		return map.toString();
	}
}
