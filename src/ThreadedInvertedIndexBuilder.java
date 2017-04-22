import java.io.IOException;
import java.nio.file.Path;

public class ThreadedInvertedIndexBuilder extends InvertedIndexBuilder
{
	public static void build(Iterable<Path> htmlFiles, ThreadedInvertedIndex index, WorkQueue queue) throws IOException
	{
		for (Path p: htmlFiles)
		{
			queue.execute(new BuildTask(p,index));
		}
	}
}
