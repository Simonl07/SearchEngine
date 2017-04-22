import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedQueryHandler extends QueryHandler
{
	private WorkQueue queue;
	private static final Logger logger = LogManager.getLogger();

	public ThreadedQueryHandler(InvertedIndex index, WorkQueue queue)
	{
		super(index);
		logger.info("initializing ThreadedQueryHandler");
		this.queue = queue;
	}

	public void parse(String path, boolean exact) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				String queries[] = WordParser.parseWords(line);
				if (queries.length == 0)
				{
					logger.warn("zero length queries detected");
					continue;
				}

				Arrays.sort(queries);
				logger.info("executing search task with " + Arrays.toString(queries));
				queue.execute(new SearchTask(queries,exact, (ThreadedInvertedIndex)index, results));
			}
		}
	}

}
