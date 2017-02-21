import java.io.IOException;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class HTMLLocator {
	
	public static ArrayList<Path> find(Path path)
	{
		ArrayList<Path> HTMLs = new ArrayList<>();
		try
		{
			find(path, HTMLs);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return HTMLs;
		
		
		
	}
	
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
