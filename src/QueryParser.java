import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class QueryParser
{
	public static ArrayList<String[]> parse(String path) throws IOException
	{
		ArrayList<String[]> output = new ArrayList<>();
		try(BufferedReader reader = Files.newBufferedReader(Paths.get(path),StandardCharsets.UTF_8))
		{
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				output.add(WordParser.parseWords(line));
			}
		}
		return output;
	}
}
