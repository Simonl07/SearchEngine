import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuildTask implements Runnable
{
	private Path path;
	private ThreadedInvertedIndex index;
	private Logger log = LogManager.getLogger();
	
	public BuildTask(Path path, ThreadedInvertedIndex index)
	{
		log.info("Build task for path" + path + "constructed");
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
		log.info("BuildTask " + path + " handled by " + Thread.currentThread().getName() + " is complete");
		return;
	}

}
