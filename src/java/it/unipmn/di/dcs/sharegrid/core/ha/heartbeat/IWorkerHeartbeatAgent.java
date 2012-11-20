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

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IWorkerHeartbeatAgent extends IObservable<WorkerStats>
{
//	/**
//	 * Cancel this agent task.
//	 *
//	 * If the task has been scheduled for one-time execution and has not yet
//	 * run, or has not yet been scheduled, it will never run. If the task
//	 * has been scheduled for repeated execution, it will never run again.
//	 * (If the task is running when this call occurs, the task will run to
//	 * completion, but will never run again.)
//	 *
//	 * Note that calling this method from within the run method of a
//	 * repeating agent task absolutely guarantees that the agent task will
//	 * not run again.
//	 *
//	 * This method may be called repeatedly; the second and subsequent calls
//	 * have no effect.
//	 *
//	 * @return <code>true</code> if this task is scheduled for one-time
//	 * execution and has not yet run, or this task is scheduled for repeated
//	 * execution. Returns <code>false</code> if the task was scheduled for
//	 * one-time execution and has already run, or if the task was never
//	 * scheduled, or if the task was already cancelled. (Loosely speaking,
//	 * this method returns true if it prevents one or more scheduled
//	 * executions from taking place.)
//	 */
//	boolean cancel();
//
//	WorkerStatus lastStatus();
//
//	/**
//	 * Returns the <em>last</em> execution time of the most recent
//	 * execution of this task.
//	 *
//	 * (If this method is invoked while task execution is in progress, the
//	 * return value is the execution time of the last task execution.)
//	 *
//	 * @return the time at which the most recent execution of this task was
//	 * scheduled to occur, in the format returned by Date.getTime(). The
//	 * return value is undefined if the task has yet to commence its first
//	 * execution.
//	 */
//	long lastExecutionTime();
//
//	long lastChangeTime();
//
	public void start();
//
	public void stop();

	public WorkerAddress getWorker();

	public WorkerStats getWorkerStats();

	boolean isRunning();
}
