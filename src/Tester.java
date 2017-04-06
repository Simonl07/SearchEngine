import java.nio.file.Paths;

public class Tester
{

	public static void main(String[] args)
	{
		try
		{
			InvertedIndex index = new InvertedIndex();
			String path = "C:\\Users\\Simon Lu\\Desktop\\Repositories\\project-tests\\html\\simple";
			
			index = InvertedIndexBuilder.build((DirectoryTraverser.findHTML(Paths.get(path))));
			
			
			//index.display();
			
			
			String queries[] = "aar alpaca elephant".split(" ");
			System.out.println(index.partialSearch(queries));
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
