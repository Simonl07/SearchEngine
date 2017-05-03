import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Traverse through a given path and locate specfic files.
 * 
 * @author Simonl0425
 */
public class DirectoryTraverser
{
	private static Logger log = LogManager.getLogger();

	/**
	 * Traverse through given path and return Path of all HTML files found.
	 * 
	 * @param path the root path for traversing
	 * @return ArrayList of Path of all the HTML located.
	 * @throws IOException
	 */
	public static ArrayList<Path> findHTML(Path path) throws IOException
	{
		log.trace("Traversing through path " + path.toString() + " to locate HTML");
		ArrayList<Path> paths = new ArrayList<>();
		find(path, paths);
		return paths;
	}

	/**
	 * Recursive helper method for locating HTML files.
	 * 
	 * @param path the path to look for.
	 * @param paths A list with HTML paths that are already found.
	 * 
	 * @throws IOException
	 */
	public static void find(Path path, ArrayList<Path> paths) throws IOException
	{
		if (!Files.isDirectory(path))
		{
			if (path.getFileName().toString().toLowerCase().endsWith(".html") || path.getFileName().toString().toLowerCase().endsWith(".htm"))
			{
				paths.add(path);
			}
		} else
		{
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path))
			{
				for (Path file: stream)
				{
					find(file, paths);
				}
			}
		}
	}

}
