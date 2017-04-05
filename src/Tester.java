import java.nio.file.Paths;

public class Tester
{

	public static void main(String[] args)
	{
		try
		{
			InvertedIndex index = new InvertedIndex();
			String path = "/home/simon/Desktop/Repositories/project-tests/html/simple";
			
			index = InvertedIndexBuilder.build((DirectoryTraverser.findHTML(Paths.get(path))));
			
			
			//index.display();
			
			
			String queries[] = "four".split(" ");
			System.out.println(index.exactSearch(queries));
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
