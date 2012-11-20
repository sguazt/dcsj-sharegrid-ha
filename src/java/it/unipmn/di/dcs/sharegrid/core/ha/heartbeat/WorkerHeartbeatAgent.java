/*
 * Copyright (C) 2007-2012  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.core.ha.heartbeat;

import it.unipmn.di.dcs.common.design.IObservable;
import it.unipmn.di.dcs.common.design.IObserver;
import it.unipmn.di.dcs.common.net.HostPinger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class WorkerHeartbeatAgent extends AbstractWorkerHeartbeatAgent
//implements IWorkerHeartbeatAgent, IObservable<WorkerStatus>
{
	private static final int DEFAULT_SLEEP_TIME = 1 * 1000; // 1 min
	private List< IObserver<WorkerStats> > observers = new ArrayList< IObserver<WorkerStats> >();
//	private List<IWorkerHeartbeatPlugin> plugins = new ArrayList<IWorkerHeartbeatPlugin>();
	private boolean statusChanged = false;
	private WorkerHeartbeatAgent.WorkerStatsWrapper workerStats;
	//private boolean isRunning = false;
	///private boolean runnerCanceled = false;
	private WorkerAddress workerAddr = null;
	private Thread runner = null;
	private IWorkerHeartbeatProber prober = null;
	//private volatile boolean threadSuspended = false;

	public WorkerHeartbeatAgent(WorkerAddress workerAddr)
	{
		this.workerAddr = workerAddr;
		this.workerStats = new WorkerHeartbeatAgent.WorkerStatsWrapper( this.workerAddr );
	}

	//@{ IWorkerHeartbeatAgent implementation

	public void start()
	{
		//OuterClass.InnerClass innerObject = outerObject.new InnerClass();
		this.runner = new Thread( this.new WorkerHeartbeatAgentRunner() );
System.err.println( "[WorkerHeartbeatAgent>> Starting runner: " + this.workerAddr );//XXX
		this.runner.start();
		//this.isRunning = true;
System.err.println( "[WorkerHeartbeatAgent>> Started runner: " + this.workerAddr );//XXX
	}

	public synchronized void stop()
	{
System.err.println( "[WorkerHeartbeatAgent>> Stopping runner: " + this.workerAddr );//XXX
		this.runner.interrupt();
		try
		{
			this.runner.join();
		}
		catch (InterruptedException ie)
		{
			System.err.println(" Unexpected interruption on main thread");
		}
		//this.isRunning = false;
		this.runner = null;
		this.notify();
System.err.println( "[WorkerHeartbeatAgent>> Stopped runner: " + this.workerAddr );//XXX
	}

/*
	public WorkerStatus lastStatus()
	{
		return this.lastStatus;
	}

	public long lastExecutionTime()
	{
		return this.lastExecTime;
	}

	public long lastChangeTime()
	{
		return this.lastChangeTime;
	}
*/

	public WorkerAddress getWorker()
	{
		return this.workerAddr;
	}

	public WorkerStats getWorkerStats()
	{
		return this.workerStats;
	}

	protected WorkerStatsWrapper getWorkerStatsWrapper()
	{
		return this.workerStats;
	}

	public boolean isRunning()
	{
		return	this.runner != null
			&& this.runner.getState() != Thread.State.NEW
			&& this.runner.getState() != Thread.State.TERMINATED;
	}

	//@} IWorkerHeartbeatAgent implementation

//	public void addPlugin(IWorkerHeartbeatPlugin plugin)
//	{
//		this.plugins.add( plugin );
//	}
//
//	protected List<IWorkerHeartbeatPlugin> getPlugins()
//	{
//		return this.plugins;
//	}

	//@{ IObservable implementation

	public void addObserver(IObserver<WorkerStats> o)
	{
		this.observers.add( o );
	}

	protected List< IObserver<WorkerStats> > getObservers()
	{
		return this.observers;
	}

	public void deleteObserver(IObserver<WorkerStats> o)
	{
		this.observers.remove( o );
	}

	public void deleteObservers()
	{
		this.observers.clear();
	}

	protected void setChanged()
	{
		this.statusChanged = true;
	}

	protected void clearChanged()
	{
		this.statusChanged = false;
	}

	public boolean hasChanged()
	{
		return this.statusChanged;
	}

	public void notifyObservers()
	{
		this.notifyObservers( null );
	}

	public void notifyObservers(WorkerStats stats)
	{
		if ( this.hasChanged() )
		{
			for (IObserver<WorkerStats> observer : this.observers)
			{
				observer.update( this, stats );
			}

			this.clearChanged();
		}
	}

	public int countObservers()
	{
		return this.observers.size();
	}

	//@} IObservable implementation

	//@{ class WorkerHeartbeatAgentRunner

	private class WorkerHeartbeatAgentRunner implements Runnable
	{
		//@{ Runnable implementation

		public void run()
		{
			Thread thisThread = Thread.currentThread();
			HostPinger pinger = new HostPinger( WorkerHeartbeatAgent.this.workerAddr.getIpAddress() );
			boolean isInterrupted = false;

			//while ( WorkerHeartbeatAgent.this.runner == thisThread )
			while ( !isInterrupted )
			{
				try
				{
System.err.println( "[WorkerHeartbeatAgentRunner>> Going to sleep (worker <'" + workerAddr.getIpAddress() + "'," + workerAddr.getPort() + ">)" );//XXX
					thisThread.sleep(DEFAULT_SLEEP_TIME);
System.err.println( "[WorkerHeartbeatAgentRunner>> Woken up (worker <'" + workerAddr.getIpAddress() + "'," + workerAddr.getPort() + ">)" );//XXX

//					synchronized(this)
//					{
//						while ( threadSuspended && WorkerHeartbeatAgent.this.runner == thisThread )
//						{
//							wait();
//						}
//					}
					//long now = new Date().getTime();
					long now = System.currentTimeMillis();

System.err.println( "[WorkerHeartbeatAgentRunner>> Check worker <'" + workerAddr.getIpAddress() + "'," + workerAddr.getPort() + ">" );//XXX
					WorkerStatus status = WorkerStatus.Unknown;
					WorkerHeartbeatAgent.this.getWorkerStatsWrapper().setCheckTime( now );

					try
					{
System.err.println( "[WorkerHeartbeatAgentRunner>> Going to ping worker <'" + workerAddr.getIpAddress() + "'," + workerAddr.getPort() + ">" );//XXX
						if ( pinger.isAlive() )
						{
System.err.println( "[WorkerHeartbeatAgentRunner>> Pinger say 'Worker is alive'."); //XXX
							status = WorkerStatus.HostUp;

							if ( WorkerHeartbeatAgent.this.getProber() != null )
							{
System.err.println( "[WorkerHeartbeatAgentRunner>> Going to probe worker <'" + workerAddr.getIpAddress() + "'," + workerAddr.getPort() + ">" );//XXX
								status = WorkerHeartbeatAgent.this.getProber().probe();
System.err.println( "[WorkerHeartbeatAgentRunner>> Prober say 'Worker status is " + status + "'."); //XXX
							}
						}
						else
						{
System.err.println( "[WorkerHeartbeatAgentRunner>> Pinger say 'Worker is not alive'."); //XXX
							status = WorkerStatus.HostDown;
						}
					}
					catch (Exception e)
					{
						status = WorkerStatus.Unknown;
						continue; // skip this round and try later
					}

					if ( WorkerHeartbeatAgent.this.getWorkerStatsWrapper().getStatus() != status )
					{
System.err.println( "[WorkerHeartbeatAgentRunner>> Status changed from '" + WorkerHeartbeatAgent.this.getWorkerStatsWrapper().getStatus() + "' to '" + status + "'" );//XXX
						WorkerHeartbeatAgent.this.getWorkerStatsWrapper().setStatus( status );
						WorkerHeartbeatAgent.this.getWorkerStatsWrapper().setChangeTime( now );

						WorkerHeartbeatAgent.this.setChanged();
						WorkerHeartbeatAgent.this.notifyObservers( WorkerHeartbeatAgent.this.getWorkerStatsWrapper() );
					}
				}
				catch (InterruptedException ie)
				{
					System.err.println("WorkerHeartbeatAgentRunner deferring interruption");
					isInterrupted = true;
				}
			}
			if ( isInterrupted )
			{
				thisThread.interrupt();
			}
		}

		//@} Runnable implementation
	}

	//@} class WorkerHeartbeatAgentRunner

	//@{ class WorkerStatsWrapper

	protected static class WorkerStatsWrapper extends WorkerStats
	{
		public WorkerStatsWrapper(WorkerAddress worker)
		{
			super( worker );
		}
		public void setCheckTime(long value)
		{
			super.setCheckTime( value );
		}
		public void setChangeTime(long value)
		{
			super.setChangeTime( value );
		}
		public void setStatus(WorkerStatus value)
		{
			super.setStatus( value );
		}
	}

	//@} class WorkerStatsWrapper
}
