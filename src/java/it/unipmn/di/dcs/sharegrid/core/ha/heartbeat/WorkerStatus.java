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
public enum WorkerStatus
{
	WorkerDown,	// The Worker agent is down (but the host might be up).
	WorkerUp,	// The Worker agent is up.
	HostDown,	// The Worker host is down.
	HostUp,		// The Worker host is up (but the agent might be down).
	WorkerDonated,	// The Worker agent is up and is running a computation
			// belonging to a Virtual Organization different from
			// that to which the Worker belongs.
	WorkerIdle,	// The Worker agent is idle and ready to accept works.
	WorkerInUse,	// The Worker agent is up and is running a computation
			// belonging to the same Virtual Organization.
	WorkerInhibited,// The Worker is up but is unable to accept works.
	Unknown;	// Unknown (fallback status).

	public boolean isUp()
	{
		return	this.equals( HostUp )
			&& !this.equals( WorkerDown )
			&& !this.equals( Unknown );
	}

	public boolean isDown()
	{
		return !this.isUp();
	}

	public boolean isBusy()
	{
		return	this.isUp()
			&& (
				this.equals( WorkerDonated )
				|| this.equals( WorkerInUse )
			);
	}

	public boolean isIdle()
	{
		return this.isUp() && !this.isBusy();
	}
}
