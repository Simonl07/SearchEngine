/**
 * Search Result objects that store data from a single search result
 * 
 * @author Simonl0425
 *
 */
public class SearchResult implements Comparable<SearchResult>
{
	private int frequency;
	private int initialPosition;
	private String path;

	/**
	 * Constructor for building SearchResult object
	 * 
	 * @param path the path of the query
	 * @param frequency how many times query appears
	 * @param initialPosition the first position of query in the file.
	 */
	public SearchResult(String path, int frequency, int initialPosition)
	{
		this.path = path;
		this.frequency = frequency;
		this.initialPosition = initialPosition;
	}

	/**
	 * Compare SearchResult objects based on frequency, initial position and
	 * path.
	 * 
	 * @param s SearchResult Object for comparison
	 * @return comparison result of two SearchResult
	 */
	@Override
	public int compareTo(SearchResult s)
	{
		if (this.frequency == s.frequency)
		{
			if (this.initialPosition == s.initialPosition)
			{
				return path.compareTo(s.getPath());
			} else
			{
				return Integer.compare(initialPosition, s.initialPosition);
			}
		} else
		{
			return (-1) * Integer.compare(frequency, s.frequency);
		}
	}

	/**
	 * increment the frequency of this SearchResult
	 * 
	 * @param frequency increment amount
	 */
	public void addFrequency(int frequency)
	{
		this.frequency += frequency;
	}

	/**
	 * Set the initial position of this SearchResult
	 * 
	 * @param position to set to
	 */
	public void setInitialPosition(int position)
	{
		if(position < this.initialPosition)
		{
			this.initialPosition = position;
		}
	}

	/**
	 * @return the frequency
	 */
	public int getFrequency()
	{
		return frequency;
	}

	/**
	 * @return the initialPosition
	 */
	public int getInitialPosition()
	{
		return initialPosition;
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	@Override
	public String toString()
	{
		return "SearchResult [frequency=" + frequency + ", initialPosition=" + initialPosition + ", path=" + path + "]\n";
	}
}
