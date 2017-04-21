import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkQueue
{
	private final Worker[] workers;
	private final LinkedList<Runnable> queue; 
	
	private volatile boolean SHUTDOWN;
	
	private int pending;
	
	private final Logger log = LogManager.getLogger();
	
	public WorkQueue()
	{
		this(5);
	}
	
	
	public WorkQueue(int threads)
	{
		//log.info("Begin initializing work queue.");
		workers = new Worker[threads];
		queue = new LinkedList<Runnable>();
		SHUTDOWN = false;
		pending = 0;
		
		for(int i = 0; i < threads;i++)
		{
			workers[i] = new Worker();
			workers[i].start();
		}
		//log.info("WorkQueue initialization complete.");
	}
	
	
	
	public void execute(Runnable r)
	{
		synchronized(queue)
		{
			pending++;
			queue.addLast(r);
			queue.notifyAll();
			//log.info("add r into queue, pending increased to " + pending + " queue size: " + queue.size());
		}
	}

	
	public void decrementPending()
	{
		synchronized(queue)
		{
			pending--;
			if(pending <= 0)
			{
				queue.notifyAll();
			}
		}
	}
	
	public void finish()
	{
		synchronized(queue)
		{
			while(pending > 0)
			{
				try
				{
					queue.wait();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void shutdown()
	{
		SHUTDOWN = true;
		synchronized(queue)
		{
			queue.notifyAll();
		}
	}
	
	private class Worker extends Thread
	{
		
		Runnable r = null;
		@Override
		public void run()
		{
			while(true)
			{
				synchronized(queue)
				{
					while(queue.isEmpty() && SHUTDOWN == false)
					{
						try
						{
							queue.wait();
						} catch (InterruptedException e)
						{
							Thread.currentThread().interrupt();
						}
					}
					
					if(SHUTDOWN)
					{
						break;
					}else{
						r = queue.removeFirst();
					}
				}
				
				try{
					r.run();
					decrementPending();
				}catch(RuntimeException e)
				{
					System.err.println("Encounter err when running runnable r");
				}	
				
				
				
			}
		}
	}
}