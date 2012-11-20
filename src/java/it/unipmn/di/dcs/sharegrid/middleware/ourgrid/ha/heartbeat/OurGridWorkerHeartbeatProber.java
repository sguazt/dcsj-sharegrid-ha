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

package it.unipmn.di.dcs.sharegrid.middleware.ourgrid.ha.heartbeat;

import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.IWorkerHeartbeatProber;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerAddress;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.Level;

//import org.ourgrid.common.id.ObjectID;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.gum.Gum;
import org.ourgrid.common.gum.GumStatus;
import org.ourgrid.common.spec.GumSpec;
import org.ourgrid.common.spec.RequestSpec.RequestSource;
import org.ourgrid.common.url.UserAgentURLProvider;
//import org.ourgrid.common.url.URLProvider;
//import org.ourgrid.gridmachine.useragent.ui.UIManager;
import org.ourgrid.gridmachine.useragent.ui.UserAgentUIManager;
import org.ourgrid.gridmachine.useragent.UserAgentClient;
import org.ourgrid.peer.manager.allocation.AllocationStatus;
import org.ourgrid.peer.manager.status.StatusEntry;
import org.ourgrid.peer.ui.PeerUIManager;
//import org.ourgrid.peer.ui.UIManager;
//import org.ourgrid.peer.ui.UIManager;
//import org.ourgrid.peer.ui.exception.PeerUIException;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OurGridWorkerHeartbeatProber implements IWorkerHeartbeatProber
{
	private static Logger logger = Logger.getLogger( OurGridWorkerHeartbeatProber.class.getName() );

	private WorkerAddress workerAddr;

	public OurGridWorkerHeartbeatProber(WorkerAddress workerAddr)
	{
		this.workerAddr = workerAddr;
		//this.initRMIRegistry();
	}

	private void initRMIRegistry()
	{
		Configuration.reset();
		Configuration conf = Configuration.getInstance( Configuration.PEER );
		int registryPort = Integer.parseInt(
			conf.getProperty( Configuration.PROP_PORT )
		);

		if ( logger.isLoggable( Level.FINE ) )
		{
			logger.fine( "[OurGridWorkerHeartbeatProber::InitRMIRegistry>> registry port: " + registryPort );
		}

		System.setProperty(
			"java.rmi.server.hostname",
			conf.getProperty( Configuration.PROP_EXTERNAL_NAME )
		);

		try
		{
			// Starting registry
			LocateRegistry.createRegistry( registryPort );
		}
		catch (RemoteException re)
		{
			try
			{
				LocateRegistry.getRegistry( registryPort );
			}
			catch (RemoteException re2)
			{
				logger.fine( "Failed to initialized RMI registry: " + re2 );
				re2.printStackTrace();
			}
		}
	}

	public WorkerStatus probe()
	{
		WorkerStatus statusPeer = this.probeFromPeer();
		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine( "[OurGridWorkerHeartbeatProber>> Peer says 'Worker is " + statusPeer + "'" );
		}

		WorkerStatus statusUA = this.probeFromUserAgent();
		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine( "[OurGridWorkerHeartbeatProber>> UserAgent says 'Worker is " + statusUA + "'" );
		}

		WorkerStatus status = OurGridWorkerHeartbeatProber.bestStatus( statusPeer, statusUA );
		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine( "[OurGridWorkerHeartbeatProber>> Best Status says 'Worker is " + status + "'" );
		}

		return status;
	}

	protected WorkerStatus probeFromUserAgent()
	{
		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine("[OurGridWorkerHeartbeatProber>> Going to probe from useragent...");//XXX
		}

		Configuration.reset();
		Configuration conf = Configuration.getInstance( Configuration.USERAGENT );

		try
		{
			//String uaName = conf.getProperty( Configuration.PROP_NAME );
			//int uaPort = Integer.parseInt( conf.getProperty( Configuration.PROP_PORT ) );
			String uaName = this.workerAddr.getIpAddress().getHostName();
			int uaPort = this.workerAddr.getPort();
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "[OurGridWorkerHeartbeatProber>> Trying to contact UserAgent '" + uaName + "' - '" + uaPort + "'." );
			}
			Registry registry = LocateRegistry.getRegistry( uaName, uaPort );
			Gum gum = (Gum) registry.lookup( UserAgentURLProvider.USERAGENT );
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "[OurGridWorkerHeartbeatProber>> USERAGENT remote service says status is '" + gum.getStatus() + "'." );
			}

			return OurGridWorkerHeartbeatProber.GumStatusToWorkerStatus( gum.getStatus() );
		}
		catch (RemoteException re)
		{
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "Error while contacting the UserAgent remote service: " + re.getMessage() );
			}
			re.printStackTrace();
		}
		catch (NotBoundException nbe)
		{
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "Error while binding the UserAgent remote service: " + nbe.getMessage() );
			}
			nbe.printStackTrace();
		}

		return WorkerStatus.Unknown;
	}

	@Deprecated
	protected WorkerStatus _DEPRECATED_probeFromUserAgent()
	{
		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine("[OurGridWorkerHeartbeatProber>> Going to probe from useragent...");//XXX
		}

		try
		{
			org.ourgrid.gridmachine.useragent.ui.UIManager manager = null;

			Configuration.reset();
			Configuration conf = Configuration.getInstance( Configuration.USERAGENT );
			//conf.setProperty( Configuration.PROP_NAME, this.workerAddr.getIpAddress().getHostName() );
			//conf.setProperty( Configuration.PROP_EXTERNAL_NAME, this.workerAddr.getIpAddress().getHostName() );
			//conf.setProperty( Configuration.PROP_PORT, new Integer( this.workerAddr.getPort() ).toString() );
			//System.setProperty( "java.rmi.server.hostname", this.workerAddr.getIpAddress().getHostName() );

			manager = UserAgentUIManager.getInstance();

			GumStatus status = manager.statusUserAgentService();

			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Useragent returns status '" + status + "'");//XXX
			}
/*
GumStatus status = GumStatus.UP;
			GumSpec gumSpec = new GumSpec();
			gumSpec.putAttribute( GumSpec.ATT_NAME, "localhost" );
			gumSpec.putAttribute( GumSpec.ATT_PORT, "3090" );
			gumSpec.putAttribute( GumSpec.ATT_PEERNAME, "localhost" );
			gumSpec.putAttribute( GumSpec.ATT_PEERPORT, "3091" );
			ProberGumClient gumClient = new ProberGumClient( gumSpec );
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "[OurGridWorkerHeartbeatProber>> FakeGumClient says status is '" + gumClient.getStatus() + "'." );
			}
*/

			return OurGridWorkerHeartbeatProber.GumStatusToWorkerStatus( status );
		}
		catch (Exception e)
		{
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "Error while contacting the UserAgent Manager: " + e.getMessage() );
			}
			e.printStackTrace();
		}

		return WorkerStatus.Unknown;
	}

	protected WorkerStatus probeFromPeer()
	{
                Collection<StatusEntry> gums = null;
		org.ourgrid.peer.ui.UIManager manager = null;

		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine("[OurGridWorkerHeartbeatProber>> Going to probe from peer...");//XXX
		}

                try {
			// Check if Peer is running
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Checking running peer.");//XXX
			}
			Configuration.reset();
			Configuration conf = Configuration.getInstance( Configuration.PEER );
			manager = PeerUIManager.getInstance();

			if ( !manager.isPeerRunning() )
			{
				if ( logger.isLoggable(Level.FINE) )
				{
					logger.fine("OurGridWorkerHeartbeatProber>> Peer is not running!" );
				}

				return WorkerStatus.Unknown;
			}

			// Check for IN_USE GuM
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Checking IN_USE GuMs.");//XXX
			}
			gums = manager.getGums( AllocationStatus.IN_USE, RequestSource.FROM_LOCAL_BROKER );
			for (StatusEntry entry : gums)
			{
//				if (
//					entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ).equals( this.workerAddr.getIpAddress().getHostName() )
//					&& entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( this.workerAddr.getPort() )
//				)
//				{
//					return WorkerStatus.WorkerInUse;
//				}

				if ( logger.isLoggable(Level.FINE) )
				{
					logger.fine("[OurGridWorkerHeartbeatProber>> Checking IN_USE GuM: '" + entry + "'.");//XXX
				}
				if ( entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( Integer.toString( this.workerAddr.getPort() ) ) )
				{
					try
					{
						InetAddress[] addresses = InetAddress.getAllByName( entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) );
						for (InetAddress address : addresses )
						{
							if ( address.equals( this.workerAddr.getIpAddress() ) )
							{
								return WorkerStatus.WorkerInUse;
							}
						}
					}
					catch (UnknownHostException uhe)
					{
						if ( logger.isLoggable(Level.FINE) )
						{
							logger.fine( "[OurGridWorkerHeartbeatProber>> Cannot resolve host name " + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ": " + uhe );
						}
					}
				}
			}
			// Check for IDLE GuM
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Checking IDLE GuMs.");//XXX
			}
			gums = manager.getGums( AllocationStatus.IDLE, RequestSource.FROM_LOCAL_BROKER );
			for (StatusEntry entry : gums)
			{
//				if (
//					entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ).equals( this.workerAddr.getIpAddress().getHostName() )
//					&& entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( this.workerAddr.getPort() )
//				)
//				{
//					return WorkerStatus.WorkerIdle;
//				}
				if ( logger.isLoggable(Level.FINE) )
				{
					logger.fine("[OurGridWorkerHeartbeatProber>> Checking IDLE GuM: '" + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ":" + entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ) + "'.");//XXX
				}
				if ( entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( Integer.toString( this.workerAddr.getPort() ) ) )
				{
					if ( logger.isLoggable(Level.FINE) )
					{
						logger.fine("[OurGridWorkerHeartbeatProber>> Checking IDLE GuM: '" + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ":" + entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ) + "' ==> Same Port.");//XXX
					}
					try
					{
						InetAddress[] addresses = InetAddress.getAllByName( entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) );
						for (InetAddress address : addresses )
						{
							if ( logger.isLoggable(Level.FINE) )
							{
								logger.fine("[OurGridWorkerHeartbeatProber>> Checking IDLE GuM: '" + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ":" + entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ) + "' ==> Check for address: '" + address + "'.");//XXX
							}
							if ( address.equals( this.workerAddr.getIpAddress() ) )
							{
								return WorkerStatus.WorkerIdle;
							}
						}
					}
					catch (UnknownHostException uhe)
					{
						if ( logger.isLoggable(Level.FINE) )
						{
							logger.fine( "[OurGridWorkerHeartbeatProber>> Cannot resolve host name " + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ": " + uhe );
						}
					}
				}
			}
			// Check for DONATED GuM
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Checking DONATED GuMs.");//XXX
			}
			gums = manager.getGums( AllocationStatus.DONATED, RequestSource.FROM_LOCAL_BROKER );
			for (StatusEntry entry : gums)
			{
//				if (
//					entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ).equals( this.workerAddr.getIpAddress().getHostName() )
//					&& entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( this.workerAddr.getPort() )
//				)
//				{
//					return WorkerStatus.WorkerDonated;
//				}
				if ( logger.isLoggable(Level.FINE) )
				{
					logger.fine("[OurGridWorkerHeartbeatProber>> Checking DONATED GuM: '" + entry + "'.");//XXX
				}
				if ( entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( Integer.toString( this.workerAddr.getPort() ) ) )
				{
					try
					{
						InetAddress[] addresses = InetAddress.getAllByName( entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) );
						for (InetAddress address : addresses )
						{
							if ( address.equals( this.workerAddr.getIpAddress() ) )
							{
								return WorkerStatus.WorkerDonated;
							}
						}
					}
					catch (UnknownHostException uhe)
					{
						if ( logger.isLoggable(Level.FINE) )
						{
							logger.fine( "[OurGridWorkerHeartbeatProber>> Cannot resolve host name " + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ": " + uhe );
						}
					}
				}
			}
			// Check for CONTACTING GuM
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Checking CONTACTING GuMs.");//XXX
			}
			gums = manager.getGums( AllocationStatus.CONTACTING, RequestSource.FROM_LOCAL_BROKER );
			for (StatusEntry entry : gums)
			{
//				if (
//					entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ).equals( this.workerAddr.getIpAddress().getHostName() )
//					&& entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( this.workerAddr.getPort() )
//				)
//				{
//					return WorkerStatus.WorkerDown;
//				}
				if ( logger.isLoggable(Level.FINE) )
				{
					logger.fine("[OurGridWorkerHeartbeatProber>> Checking CONTACTING GuM: '" + entry + "'.");//XXX
				}
				if ( entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( Integer.toString( this.workerAddr.getPort() ) ) )
				{
					try
					{
						InetAddress[] addresses = InetAddress.getAllByName( entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) );
						for (InetAddress address : addresses )
						{
							if ( address.equals( this.workerAddr.getIpAddress() ) )
							{
								return WorkerStatus.WorkerDown;
							}
						}
					}
					catch (UnknownHostException uhe)
					{
						if ( logger.isLoggable(Level.FINE) )
						{
							logger.fine( "[OurGridWorkerHeartbeatProber>> Cannot resolve host name " + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ": " + uhe );
						}
					}
				}
			}
			// Check for OWNER GuM
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine("[OurGridWorkerHeartbeatProber>> Checking OWNER GuMs.");//XXX
			}
			gums = manager.getGums( AllocationStatus.OWNER, RequestSource.FROM_LOCAL_BROKER );
			for (StatusEntry entry : gums)
			{
//				if (
//					entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ).equals( this.workerAddr.getIpAddress().getHostName() )
//					&& entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( this.workerAddr.getPort() )
//				)
//				{
//					return WorkerStatus.WorkerInhibited;
//				}
				if ( entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( Integer.toString( this.workerAddr.getPort() ) ) )
				{
					try
					{
						InetAddress[] addresses = InetAddress.getAllByName( entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) );
						for (InetAddress address : addresses )
						{
							if ( address.equals( this.workerAddr.getIpAddress() ) )
							{
								return WorkerStatus.WorkerInhibited;
							}
						}
					}
					catch (UnknownHostException uhe)
					{
						if ( logger.isLoggable(Level.FINE) )
						{
							logger.fine( "[OurGridWorkerHeartbeatProber>> Cannot resolve host name " + entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ) + ": " + uhe );
						}
					}
				}
			}
//			// Check for IN_USE_(REMOTE) GuM
//			gums = manager.getGums( AllocationStatus.IN_USE, RequestSource.FROM_REMOTE_BROKER );
//			for (StatusEntry entry : gums)
//			{
//				if (
//					entry.getGumSpec().getAttribute( GumSpec.ATT_NAME ).equals( this.workerAddr.getIpAddress().getHostName() )
//					&& entry.getGumSpec().getAttribute( GumSpec.ATT_PORT ).equals( this.workerAddr.getPort() )
//				)
//				{
//					return WorkerStatus.WorkerInUse;
//				}
//			}
			//TODO

                }
		catch (RemoteException e)
		{
			if ( logger.isLoggable(Level.FINE) )
			{
				logger.fine( "Error while contacting the Peer Manager: " + e.getMessage() );
			}
			e.printStackTrace();
			//TODO
                        //throw new Exception( "Error while contacting the Peer Manager.", e );
                }

		if ( logger.isLoggable(Level.FINE) )
		{
			logger.fine("[OurGridWorkerHeartbeatProber>> GuMs not found in this community.");//XXX
		}

		return WorkerStatus.Unknown;
	}

	private static WorkerStatus GumStatusToWorkerStatus(GumStatus status)
	{
		switch ( status )
		{
			case CONTACTING:
				return WorkerStatus.WorkerDown;
			case READY:
				return WorkerStatus.WorkerIdle;
			case RUNNING:
				return WorkerStatus.WorkerInUse;
			case OWNER:
				return WorkerStatus.WorkerInhibited;
			case UP:
				return WorkerStatus.WorkerUp;
		}
		return WorkerStatus.Unknown;
	}

	protected static WorkerStatus bestStatus(WorkerStatus statusPeer, WorkerStatus statusUA)
	{
		if ( statusPeer != statusUA )
		{
			if (
				statusUA != WorkerStatus.HostUp
				&& statusUA != WorkerStatus.WorkerUp
				&& statusUA != WorkerStatus.WorkerDown
				&& statusUA != WorkerStatus.HostDown
				&& statusUA != WorkerStatus.Unknown
			)
			{
				// Status from UserAgent is sufficiently specific. Returns it.
				return statusUA;
			}
			if ( statusUA == WorkerStatus.WorkerUp )
			{
				if (
					statusPeer == WorkerStatus.HostUp
					|| statusPeer == WorkerStatus.HostDown
					|| statusPeer == WorkerStatus.WorkerDown
					|| statusPeer == WorkerStatus.Unknown
				)
				{
					// Status from UserAgent is still more specific
					return statusUA;
				}
				return statusPeer;
			}
			if ( statusUA == WorkerStatus.HostUp )
			{
				if (
					statusPeer == WorkerStatus.HostDown
					|| statusPeer == WorkerStatus.WorkerDown
					|| statusPeer == WorkerStatus.Unknown
				)
				{
					// In general between Peer and UserAgent we trust UserAgent
					return statusUA;
				}
				return statusPeer;
			}
			if ( statusUA == WorkerStatus.HostDown )
			{
				if ( statusPeer == WorkerStatus.WorkerDown )
				{
					return statusPeer;
				}
				return statusUA;
			}
			if ( statusUA == WorkerStatus.WorkerDown )
			{
				if (
					statusPeer == WorkerStatus.HostDown
					|| statusPeer == WorkerStatus.Unknown
				)
				{
					return statusUA;
				}
				return statusPeer;
			}
			if ( statusUA == WorkerStatus.Unknown )
			{
				return statusPeer;
			}
			if ( statusPeer == WorkerStatus.Unknown )
			{
				return statusUA;
			}
		}
		return statusUA;
	}

/*
	public WorkerStatus probeAllGums()
	{
                Collection<StatusEntry> localGums = null;
                Collection<StatusEntry> remoteGums = null;
                Collection<StatusEntry> donatedGums = null;
                Collection<StatusEntry> idleGums = null;
                Collection<StatusEntry> offlineGums = null;
                Collection<StatusEntry> ownerGums = null;

                try {

                        localGums = PeerUIManager.getInstance().getGums( AllocationStatus.IN_USE, RequestSource.FROM_LOCAL_BROKER );
                        idleGums = PeerUIManager.getInstance().getGums( AllocationStatus.IDLE, RequestSource.FROM_LOCAL_BROKER );
                        remoteGums = PeerUIManager.getInstance().getGums( AllocationStatus.IN_USE, RequestSource.FROM_REMOTE_PEER );
                        donatedGums = PeerUIManager.getInstance().getGums( AllocationStatus.DONATED, RequestSource.FROM_LOCAL_BROKER );
                        offlineGums = PeerUIManager.getInstance().getGums( AllocationStatus.CONTACTING, RequestSource.FROM_LOCAL_BROKER );
                        ownerGums = PeerUIManager.getInstance().getGums( AllocationStatus.OWNER, RequestSource.FROM_LOCAL_BROKER );

                } catch ( RemoteException e ) {
			//TODO
                        //LOG.debug( "Could not contact the Peer UI Manager: " + e.getMessage() );
                        //throw new PeerUIException( e );
                }

                //System.out.println( "\n" + UIMessages.LOCAL_MACHINES );

		// printing idle local gums
		printGums( idleGums, AllocationStatus.IDLE );

                // printing local gums in use by local brokers
                printGums( localGums, AllocationStatus.IN_USE );

                // printing gums donated to the community
                printGums( donatedGums, AllocationStatus.DONATED );

                // printing local offline gums
                printGums( offlineGums, AllocationStatus.CONTACTING );

                // printing local owner gums
                printGums( ownerGums, AllocationStatus.OWNER );

                //System.out.println( "\n" + UIMessages.REMOTE_MACHINES );

                // printing gums from community in use by local brokers
                printGums( remoteGums, AllocationStatus.IN_USE );
	}

        private void printGums( Collection<StatusEntry> gumsList, AllocationStatus status ) {

                if ( gumsList != null ) {

                        Iterator it = gumsList.iterator();

                        while ( it.hasNext() ) {
                                System.out.println( it.next() + " [ " + status + " ]" );
                        }
                }
        }
*/

/*
	private static class ProberGumClient extends UserAgentClient
	{
		public ProberGumClient(GumSpec gumSpec) throws RemoteException
		{
			super( gumSpec );
		}
	}
*/
}
