
public class ThreadedQueryHandler extends QueryHandler
{
	private WorkQueue queue;
	
	public ThreadedQueryHandler(InvertedIndex index, WorkQueue queue)
	{
		super(index);
		this.queue = queue;
	}
	
	

}
