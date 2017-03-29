import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

// TODO Refactor DirectoryTraverser
public class DirectoryTraverser {
	
	public static ArrayList<Path> findHTML(Path path) throws IOException// TODO findHTML
	{
		ArrayList<Path> paths = new ArrayList<>(); // TODO refactor paths
		find(path, paths);
		return paths;
	}
	
	// TODO Format code more consistently
	
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
			}catch (IOException | DirectoryIteratorException x) 
			{
			    x.printStackTrace();
			}
		}
		
	}
	
}
