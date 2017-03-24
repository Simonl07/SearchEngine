import java.io.IOException;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

// TODO Refactor DirectoryTraverser
public class HTMLLocator {
	
	public static ArrayList<Path> find(Path path) // TODO findHTML
	{
		ArrayList<Path> HTMLs = new ArrayList<>(); // TODO refactor paths
		try
		{
			find(path, HTMLs);
		}catch(IOException e)
		{
			// TODO The easy way (also more general) is to throw everything to Driver and let Driver catch
			e.printStackTrace();
		}
		return HTMLs;
		
		
		
	}
	
	// TODO Format code more consistently
	
	public static void find(Path path, ArrayList<Path> paths) throws IOException
	{
		
		if(!Files.isDirectory(path) && !Files.isSymbolicLink(path) && !Files.isHidden(path))
		{
			if(path.getFileName().toString().toLowerCase().endsWith(".html") || path.getFileName().toString().toLowerCase().endsWith(".htm"))
			{
				paths.add(path);
			}
		}else if(Files.isDirectory(path) && !Files.isSymbolicLink(path) && Files.isExecutable(path)&& !Files.isHidden(path))
		{
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			    for (Path file: stream) {
			        find(file,paths);
			    }
			} catch (IOException | DirectoryIteratorException x) {
			    x.printStackTrace();
			}
		}
		
	}
	
}
