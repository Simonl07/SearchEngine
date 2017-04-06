import java.io.IOException;
import java.nio.file.Paths;

/**
 * Parses command-line arguments to build an inverted index.
 * 
 * @author Simonl0425
 */
public class Driver
{

	/**
	 * Parses command-line arguments to build an inverted index.
	 * 
	 * @param args the command line parameters
	 */
	public static void main(String[] args)
	{

		ArgumentMap argsMap = new ArgumentMap(args);

		InvertedIndex wordIndex = new InvertedIndex();

		if (argsMap.hasValue("-path"))
		{
			try
			{
				InvertedIndexBuilder.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))), wordIndex);
			} catch (IOException e)
			{
				System.out.println("Encountered error when reading from file and building the Inverted Index.");
				return;
			}
		}

		if (argsMap.hasFlag("-index"))
		{
			String indexPath = argsMap.getString("-index", "index.json");
			try
			{
				wordIndex.toJSON(Paths.get(indexPath));
			} catch (IOException e)
			{
				System.out.println("Encountered erroe when writing index into JSON file.");
				return;
			}
		}
	}
}
