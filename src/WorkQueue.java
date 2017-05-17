import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * A WorkQueue consist of a pool of worker thread and a list of Runnable work
 * 
 * @author Simonl0425
 *
 */
public class WorkQueue
{
	private final Worker[] workers;
	private final LinkedList<Runnable> queue;

	private volatile boolean shutdown;

	private int pending;

	private final Logger log = LogManager.getLogger();

	/**
	 * Starts a work queue with the default number of threads.
	 *
	 */
	public WorkQueue()
	{
		this(5);
	}

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 */
	public WorkQueue(int threads)
	{
		workers = new Worker[threads];
		queue = new LinkedList<Runnable>();
		shutdown = false;
		pending = 0;

		for (int i = 0; i < threads; i++)
		{
			workers[i] = new Worker();
			workers[i].start();
		}

		log.info("WorkQueue with " + threads + " workers initialized");
	}

	/**
	 * Adds a work request to the queue. A thread will process this request when
	 * available.
	 *
	 * @param r runnable object
	 */
	public void execute(Runnable r)
	{
		synchronized (queue)
		{
			pending++;
			queue.addLast(r);
			log.info("Executed " + r.getClass());
			queue.notifyAll();
		}
	}

	/**
	 * decrease the pending work count by 1
	 */
	private void decrementPending()
	{
		synchronized (queue)
		{
			pending--;
			if (pending <= 0)
			{
				queue.notifyAll();
			}
		}
	}

	/**
	 * Wait until all pending work is finished in the work queue.
	 */
	public void finish()
	{
		synchronized (queue)
		{
			while (pending > 0)
			{
				try
				{
					queue.wait();
				} catch (InterruptedException e)
				{
					System.err.println("Warning: Work queue interrupted " + "while waiting.");
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * return the current pending work count.
	 * 
	 * @return pending work count
	 */
	public int getPending()
	{
		synchronized (queue)
		{
			return pending;
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work will not be finished,
	 * but threads in-progress will not be interrupted.
	 */
	public void shutdown()
	{
		shutdown = true;
		synchronized (queue)
		{
			queue.notifyAll();
		}
	}

	/**
	 * Worker for work queue, execute work, if there is no work, wait.
	 * 
	 */
	private class Worker extends Thread
	{
		@Override
		public void run()
		{
			while (true)
			{
				Runnable r = null;
				synchronized (queue)
				{
					while (queue.isEmpty() && shutdown == false)
					{
						try
						{
							queue.wait();
						} catch (InterruptedException e)
						{
							System.err.println("Warning: Worker queue interrupted " + "while waiting.");
							Thread.currentThread().interrupt();
						}
					}

					if (shutdown)
					{
						System.out.println("SHHUTDOWN!");
						break;
					} else
					{
						r = queue.removeFirst();
						System.out.println("R obtained " + r.getClass());
					}
				}

				try
				{
					log.warn("WorkQueue Excecuting a " + r.getClass());
					r.run();
				} catch (RuntimeException e)
				{
					System.err.println("Encounter err when running runnable r");
					log.warn("Warning: Work queue encountered an " + "exception while running.");
					e.printStackTrace();
				}

				log.trace("Completed a " + r.getClass() + ", "+ pending + " tasks are left in workqueue.");
				decrementPending();
			}
		}
	}
}