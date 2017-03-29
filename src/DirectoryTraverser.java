import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

// TODO Refactor DirectoryTraverser
/**
 * Traverse through a given path and locate specfic files.
 * 
 * @author Simonl0425
 */
public class DirectoryTraverser {
	
	/**
	 * Traverse through given path and return Path of all HTML files found.
	 * 
	 * @param path	the root path for traversing
	 * @return ArrayList of Path of all the HTML located.
	 * @throws IOException
	 */
	public static ArrayList<Path> findHTML(Path path) throws IOException// TODO findHTML
	{
		ArrayList<Path> paths = new ArrayList<>(); // TODO refactor paths
		find(path, paths);
		return paths;
	}
	
	// TODO Format code more consistently
	/**
	 * Recursive helper method for locating HTML files.
	 * 
	 * @param path	the path to look for.
	 * @param paths	A list with HTML paths that are already found.
	 * 
	 * @throws IOException
	 */
	public static void find(Path path, ArrayList<Path> paths) throws IOException
	{
		
		if(!Files.isDirectory(path) && !Files.isHidden(path))
		{
			if(path.getFileName().toString().toLowerCase().endsWith(".html") || path.getFileName().toString().toLowerCase().endsWith(".htm"))
			{
				paths.add(path);
			}
		}else if(Files.isDirectory(path) && !Files.isHidden(path))
		{
			try(DirectoryStream<Path> stream = Files.newDirectoryStream(path)) 
			{
			    for (Path file: stream) 
			    {
			        find(file,paths);
			    }
			}
		}
		
	}
	
}
