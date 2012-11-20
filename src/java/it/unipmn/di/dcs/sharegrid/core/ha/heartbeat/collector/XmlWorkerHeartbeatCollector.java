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

package it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.collector;

import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.IWorkerHeartbeatCollector;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerStats;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class XmlWorkerHeartbeatCollector implements IWorkerHeartbeatCollector
{
	private PrintWriter pw;

	public XmlWorkerHeartbeatCollector(Writer writer)
	{
		this.pw = new PrintWriter( writer );
	}

	public void startCollect()
	{
		this.pw.println( "<?xml version=\"1.0\"?>" );
		this.pw.println( "<worker-stats-set>" );
	}

	public void collect(WorkerStats stats)
	{
		long now = new Date().getTime();

		this.pw.println( "<worker-stats>" );
		this.pw.println( " <timestamp>" + now + "<timestamp>" );
		this.pw.println( " <check-time>" + stats.getCheckTime() + "<check-time>" );
		this.pw.println( " <change-time>" + stats.getChangeTime() + "<change-time>" );
		this.pw.println( " <worker-status>" + stats.getStatus() + "<worker-status>" );
		this.pw.println( " <worker-address>" + stats.getWorker().getIpAddress() + "<worker-address>" );
		this.pw.println( " <worker-port>" + stats.getWorker().getPort() + "<worker-port>" );
		this.pw.println( "</worker-stats>" );
		this.pw.flush();
	}

	public void stopCollect()
	{
		this.pw.println( "</worker-stats-set>" );
		this.pw.flush();
	}
}
