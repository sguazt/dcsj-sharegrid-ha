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

package it.unipmn.di.dcs.sharegrid.core.ha.test;

import it.unipmn.di.dcs.common.design.IObservable;
import it.unipmn.di.dcs.common.design.IObserver;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.AbstractWorkerHeartbeatAgent;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.collector.XmlWorkerHeartbeatCollector;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.IWorkerHeartbeatAgent;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.IWorkerHeartbeatCollector;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerAddress;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerHeartbeatAgent;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerHeartbeatManager;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerStatus;
import it.unipmn.di.dcs.sharegrid.core.ha.heartbeat.WorkerStats;
import it.unipmn.di.dcs.sharegrid.middleware.ourgrid.ha.heartbeat.OurGridWorkerHeartbeatProber;
import it.unipmn.di.dcs.sharegrid.middleware.ourgrid.OurGridEnv;

import java.net.InetAddress;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.*;
import static org.junit.Assert.*;

//BEGIN XXX
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.url.PeerURLProvider;
import org.ourgrid.common.url.UserAgentURLProvider;
//END XXX

public class WorkerHeartbeatAgentTester
/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
{
	private static Logger logger = null;
	private static final int LOCAL_AGENT_PORT = 3090;

	static
	{
		//Handler lh = new FileHandler("%t/test.log");
		//lh = new StreamHandler( System.err, new SimpleFormatter() );
		try
		{
			Handler lh = new FileHandler("%t/test.log", false );
			lh.setFormatter( new SimpleFormatter() );
			Logger.getLogger("it.unipmn.di.dcs").addHandler( lh );
			Logger.getLogger("it.unipmn.di.dcs").setLevel(Level.ALL);
			logger = Logger.getLogger( WorkerHeartbeatAgentTester.class.getName() );
		}
		catch (Exception e)
		{
			System.err.println("Unexpected exception while initializing logger: " + e );
			System.exit(1);
		}
	}

        /**
	 * Sets up the test fixture. 
	 * (Called before every test case method.)
	 */
	@Before
	public void setUp()
	{
		logger.entering( WorkerHeartbeatAgentTester.class.getName(), "setUp" );

		logger.fine( "Getting env instance ..." );
		OurGridEnv env = OurGridEnv.GetInstance();

		logger.fine( "MGROOT Env: " + System.getenv( "MGROOT" ) );
		logger.fine( "MGROOT Prop: " + System.getProperty( "MGROOT" ) );
		logger.fine( "OGROOT Env: " + System.getenv( "OGROOT" ) );
		logger.fine( "OGROOT Prop: " + System.getProperty( "OGROOT" ) );

		logger.exiting( WorkerHeartbeatAgentTester.class.getName(), "setUp" );
	}

	//@Test
	public void testLocalAgent()
	{
		boolean ok = true;

		logger.entering( WorkerHeartbeatAgentTester.class.getName(), "testLocalAgent" );

		try
		{
			WorkerHeartbeatManager manager = new WorkerHeartbeatManager();

			// Set agent
			WorkerAddress worker = new WorkerAddress( InetAddress.getLocalHost(), LOCAL_AGENT_PORT );

			AbstractWorkerHeartbeatAgent agent = new WorkerHeartbeatAgent( worker );
			agent.addObserver( this.new TestObserver() );

			manager.addAgent( agent );

			// Set collector
			IWorkerHeartbeatCollector collector = new XmlWorkerHeartbeatCollector( new FileWriter( File.createTempFile( "SharegridHA", ".xml" ) ) );

			manager.addCollector( collector );

			// Start
			manager.startAgents();
			//Thread.currentThread().sleep(190*1000);
			for ( int i = 0; i <= 1000; i++ )
			{
				;
			}
			manager.stopAgents();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ok = false;
		}

		logger.exiting( WorkerHeartbeatAgentTester.class.getName(), "testLocalAgent" );

		assertTrue( ok );
	}

	@Test
	public void testOurGridPeer()
	{
		Configuration.reset();
		//System.setProperty( "MGROOT", System.getenv("MGROOT") );
		//System.setProperty( "OGROOT", System.getenv("OGROOT") );
		//System.setProperty( "UAROOT", System.getenv("UAROOT") );
		Configuration conf = Configuration.getInstance( Configuration.PEER );
		PeerURLProvider urlProv = new PeerURLProvider();
		logger.fine( "[TestOurGridPeer>> Peer name: " + urlProv.getName() );
		logger.fine( "[TestOurGridPeer>> Peer port: " + urlProv.getPort() );
		logger.fine( "[TestOurGridPeer>> Peer: " + urlProv.localAccess() );
		logger.fine( "[TestOurGridPeer>> Peer published objects: " + urlProv.publishedObjects() );
	}

	@Test
	public void testOurGridUserAgent()
	{
		Configuration.reset();
		//System.setProperty( "MGROOT", System.getenv("MGROOT") );
		//System.setProperty( "OGROOT", System.getenv("OGROOT") );
		//System.setProperty( "UAROOT", System.getenv("UAROOT") );
		Configuration conf = Configuration.getInstance( Configuration.USERAGENT );
		UserAgentURLProvider urlProv = new UserAgentURLProvider();
		logger.fine( "[TestOurGridUserAgent>> UserAgent name: " + urlProv.getName() );
		logger.fine( "[TestOurGridUserAgent>> UserAgent port: " + urlProv.getPort() );
		logger.fine( "[TestOurGridUserAgent>> UserAgent: " + urlProv.userAgent() );
		logger.fine( "[TestOurGridUserAgent>> UserAgent published objects: " + urlProv.publishedObjects() );
	}

	@Test
	public void testLocalOurGridAgent()
	{
		boolean ok = true;

		System.err.println( "Entering TestLocalOurGridAgent ..." );

		try
		{
			WorkerHeartbeatManager manager = new WorkerHeartbeatManager();

			// Set agent
			WorkerAddress worker = new WorkerAddress( InetAddress.getLocalHost(), LOCAL_AGENT_PORT );

			AbstractWorkerHeartbeatAgent agent = new WorkerHeartbeatAgent( worker );
			agent.addObserver( this.new TestObserver() );
			agent.setProber( new OurGridWorkerHeartbeatProber( worker ) );

			manager.addAgent( agent );

			// Set collector
			IWorkerHeartbeatCollector collector = new XmlWorkerHeartbeatCollector( new FileWriter( File.createTempFile( "SharegridHA", ".xml" ) ) );

			manager.addCollector( collector );

			// Start
			manager.startAgents();
			//agent.start();
			Thread.currentThread().sleep(190*1000);
			//agent.stop();
			manager.stopAgents();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ok = false;
		}

		logger.fine( "Leaving TestLocalOurGridAgent ..." );

		assertTrue( ok );
	}

        /**
	 * Tears down the test fixture.
	 * (Called after every test case method.)
	 */
	@After
	public void tearDown()
	{
		// empty
	}

	//@{ TestObserver class

	private class TestObserver implements IObserver<WorkerStats>
	{
		//@{ IObserver implementation

		public void update(IObservable<WorkerStats> o, WorkerStats arg)
		{
			logger.fine( "[WorkerHeartbeatAgentTester.TestObserver>> Worker status changed to '" + arg.getStatus() + "' (" + arg.getChangeTime() + "/" + arg.getCheckTime() + ")." );
		}

		//@} IObserver implementation
	}

	//@} TestObserver class
}
