import java.io.IOException;
import java.nio.file.Paths;

// TODO Always remove old TODO comments once addressed

// TODO Always resolve your warnings
// TODO Configure Eclipse to "Organize Imports" on save, and you'll never again see that warning
// TODO Add Javadoc comments to ALL classes and ALL methods

/**
 * Parses command-line arguments to build an inverted index.
 * 
 * @author Simonl0425
 */
public class Driver {

	/**
	 * Parses command-line arguments to build an inverted index.
	 * 
	 * @param args the command line parameters
	 */
	public static void main(String[] args) {

		ArgumentMap argsMap = new ArgumentMap(args);
		
		InvertedIndex wordIndex = new InvertedIndex();
		
		if(argsMap.hasValue("-path"))
		{
			try
			{
				wordIndex.build(DirectoryTraverser.findHTML(Paths.get(argsMap.getString("-path"))));
			} catch(IOException e)
			{
				// TODO Encountered error when building the inverted index.
				System.out.println("IOException when building wordIndex.");
				return;
			}
		}
		
		
		if(argsMap.hasFlag("-index"))
		{
			String indexPath = argsMap.getString("-index","index.json");
			try 
			{
				wordIndex.toJSON(Paths.get(indexPath));
			} catch (IOException e) 
			{
				// TODO Make a little more user friendly.
				System.out.println("IOException when writing JSON file.");
				return;
			}
		}
	}		
	
	// TODO Remove this
	/*
	 * Driver is project specific, all other "generally useful" code should be in other classes.
	 * 
	 * ArgumentMap map = new ArgumentMap(args);
	 * InvertedIndex index = new InvertedIndex();
	 * 
	 * if (-path) {
	 * 
	 * }
	 * 
	 * if (-index) {
	 * 		String path = map.getString("-index", "index.json");
	 * }
	 */
}
