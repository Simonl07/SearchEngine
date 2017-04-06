import java.nio.file.Paths;

/**
 * @author simon
 *
 */
public class SearchResult implements Comparable<SearchResult>
{
	private String word;
	private int frequency;
	private int initialPosition;
	private String path;

	public SearchResult(String word, String path, int frequency, int initialPosition)
	{
		this.word = word;
		this.path = path;
		this.frequency = frequency;
		this.initialPosition = initialPosition;
	}

	public int compareTo(SearchResult s)
	{
		if (this.frequency == s.frequency)
		{
			if (this.initialPosition == s.initialPosition)
			{
				return getFileName(path).compareTo(getFileName(s.path));
			} else
			{
				return Integer.compare(initialPosition, s.initialPosition);
			}
		} else
		{
			return Integer.compare(frequency, s.frequency);
		}
	}

	public void addFrequency(int frequency)
	{
		this.frequency += frequency;
	}

	public void setInitialPosition(int position)
	{
		this.initialPosition = position;
	}

	private String getFileName(String path)
	{
		return Paths.get(path).getFileName().toString();
	}

	/**
	 * @return the word
	 */
	public String getWord()
	{
		return word;
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
		return "SearchResult [word=" + word + ", frequency=" + frequency + ", initialPosition=" + initialPosition + ", path=" + path + "]\n";
	}
}
