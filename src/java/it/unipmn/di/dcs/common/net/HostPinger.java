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

package it.unipmn.di.dcs.common.net;

import java.net.InetAddress;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class HostPinger
{
	private static final int DEFAULT_TIMEOUT = 1000; // timeout in msec

	private InetAddress ip;
	private int timeout;

	public HostPinger(InetAddress addr, int timeout)
	{
		this.ip = addr;
		this.timeout = timeout;
	}

	public HostPinger(InetAddress addr)
	{
		this( addr, DEFAULT_TIMEOUT );
	}

	public boolean isAlive() throws Exception
	{
		if ( this.ip.isReachable( this.timeout ) )
		{
			return true;
		}

		return false;
	}
}
