import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildTask implements Runnable
{
	private Path path;
	private ThreadedInvertedIndex index;
	public BuildTask(Path path, ThreadedInvertedIndex index)
	{
		this.path = path;
		this.index = index;
	}
	public void run()
	{
		try (BufferedReader input = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			String line = "";
			StringBuilder content = new StringBuilder("");
			while ((line = input.readLine()) != null)
			{
				content.append(line);
				content.append("\n");
			}

			String[] words = WordParser.parseWords(HTMLCleaner.stripHTML(content.toString()));

			index.addAll(path.toString(), words);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return;
	}

}
