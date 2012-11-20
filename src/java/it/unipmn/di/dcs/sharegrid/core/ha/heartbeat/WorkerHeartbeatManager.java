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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class WorkerHeartbeatManager
{
	private Map<WorkerAddress,IWorkerHeartbeatAgent> agents = new HashMap<WorkerAddress,IWorkerHeartbeatAgent>();
//	private Map<WorkerAddress,Thread> agentRunners = new HashMap<WorkerAddress,Thread>();
	private List<IWorkerHeartbeatCollector> collectors = new ArrayList<IWorkerHeartbeatCollector>();

	public void addAgent(IWorkerHeartbeatAgent agent)
	{
		this.agents.put( agent.getWorker(), agent );
	}

	public void addCollector(IWorkerHeartbeatCollector collector)
	{
		this.collectors.add( collector );
	}

	public List<IWorkerHeartbeatCollector> getCollectors()
	{
		return this.collectors;
	}

	public void startAgents()
	{
		for (IWorkerHeartbeatCollector collector : this.collectors)
		{
			collector.startCollect();
		}

		for (WorkerAddress worker : this.agents.keySet())
		{
			IWorkerHeartbeatAgent agent = this.agents.get( worker );

			if ( agent != null && !agent.isRunning() )
			{
				agent.addObserver( this.new AgentObserver() );
				agent.start();
//				Thread t = null;
//				t = new Thread( new AgentRunner( agent ) );
//				this.agentRunners.put( worker, t );
//				t.start();
			}
		}
	}

	public void startAgent(WorkerAddress worker)
	{
		IWorkerHeartbeatAgent agent = this.agents.get( worker );

		if ( agent != null && !agent.isRunning() )
		{
			agent.start();
//			Thread t = null;
//			t = new Thread( new AgentRunner( agent ) );
//			this.agentRunners.put( worker, t );
//			t.start();
		}
	}

	public void stopAgents()
	{
		for (WorkerAddress worker : this.agents.keySet())
		{
//			Thread agentThread = this.agentRunners.get( worker );
//
//			if ( agentRunner != null && agentRunner.isAlive() )
//			{
//				t.interrup();
//			}

			IWorkerHeartbeatAgent agent = this.agents.get( worker );

			if ( agent != null && agent.isRunning() )
			{
				agent.stop();
			}
		}

		for (IWorkerHeartbeatCollector collector : this.collectors)
		{
			collector.stopCollect();
		}
	}

	public void stopAgent(WorkerAddress worker)
	{
		IWorkerHeartbeatAgent agent = this.agents.get( worker );

		if ( agent != null && agent.isRunning() )
		{
			agent.stop();
		}
	}

	private class AgentObserver implements IObserver<WorkerStats>
	{
		public void update(IObservable<WorkerStats> o, WorkerStats stats)
		{
			for (IWorkerHeartbeatCollector collector : WorkerHeartbeatManager.this.getCollectors())
			{
				collector.collect( stats );
			}
		}
	}

	private static class AgentRunner implements Runnable
	{
		private IWorkerHeartbeatAgent agent;

		public AgentRunner(IWorkerHeartbeatAgent agent)
		{
			this.agent = agent;
		}

		public void run()
		{
			this.agent.start();
		}

		public IWorkerHeartbeatAgent getAgent()
		{
			return this.agent;
		}
	}
}
