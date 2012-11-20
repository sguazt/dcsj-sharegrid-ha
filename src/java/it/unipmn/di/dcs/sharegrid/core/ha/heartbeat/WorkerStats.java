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

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class WorkerStats
{
	private WorkerAddress address;
	private long checkTime = -1;
	private long changeTime = -1;
	private WorkerStatus oldStatus = WorkerStatus.Unknown;
	private WorkerStatus status = WorkerStatus.Unknown;

	public WorkerStats(WorkerAddress worker)
	{
		this.address = worker;
	}

	protected void setCheckTime(long value)
	{
		this.checkTime = value;
	}

	public long getCheckTime()
	{
		return this.checkTime;
	}

	protected void setChangeTime(long value)
	{
		this.changeTime = value;
	}

	public long getChangeTime()
	{
		return this.changeTime;
	}

	protected void setStatus(WorkerStatus value)
	{
		this.oldStatus = this.status;
		this.status = value;
	}

	public WorkerStatus getStatus()
	{
		return this.status;
	}

	public WorkerStatus getOldStatus()
	{
		return this.oldStatus;
	}

	public WorkerAddress getWorker()
	{
		return this.address;
	}
}
