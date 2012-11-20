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

package it.unipmn.di.dcs.sharegrid.middleware.ourgrid;

//import it.unipmn.di.dcs.sharegrid.core.middleware.sched.IRequirementOp;
//import it.unipmn.di.dcs.sharegrid.core.middleware.sched.RequirementOpType;
//import it.unipmn.di.dcs.sharegrid.core.format.jdf.JdfExporter;
//import it.unipmn.di.dcs.sharegrid.core.middleware.sched.JobRequirementsOps;
//import it.unipmn.di.dcs.common.text.BinaryTextOp;
//import it.unipmn.di.dcs.common.text.ITextOp;
//import it.unipmn.di.dcs.common.text.TextOpType;
//import it.unipmn.di.dcs.common.text.UnaryTextOp;
import it.unipmn.di.dcs.common.util.Strings;

import java.util.ArrayList;
import java.util.List;

//!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!
//FIXME: this is a fragment of original OurGridEnv
//FIXME: All changes must be done in the original version!!!!
//!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!

/**
 * Holds information about OurGrid environment.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class OurGridEnv
{
	private static OurGridEnv instance; /** The singleton instance. */
;
//	private static List<ITextOp> SupportedJobRequirementOps; // supported operators respect to the general middleware layer

	static
	{
//		OurGridEnv.SupportedJobRequirementOps = new ArrayList<ITextOp>();
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.EQ );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.GE );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.GT );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.LE );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.LOGICAL_AND );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.LOGICAL_NOT );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.LOGICAL_OR );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.LT );
//		OurGridEnv.SupportedJobRequirementOps.add( JobRequirementsOps.NE );

System.err.println("Checking MGROOT property...");//XXX
		if ( Strings.IsNullOrEmpty( System.getProperty("MGROOT") ) )
		{
			// Guess a value for MGROOT
System.err.println("Checking MGROOT env...");//XXX
			if ( !Strings.IsNullOrEmpty( System.getenv( "MGROOT" ) ) )
			{
System.err.println("Setting MGROOT from env...");//XXX
//				System.getProperties().setProperty( "MGROOT", System.getenv( "MGROOT" ) );
				System.setProperty( "MGROOT", System.getenv( "MGROOT" ) );
			}
			else
			{
System.err.println("Setting MGROOT to '.'...");//XXX
//				System.getProperties().setProperty( "MGROOT", "." );
				System.setProperty( "MGROOT", "." );
			}
		}
System.err.println("Checking OGROOT property...");//XXX
		if ( Strings.IsNullOrEmpty( System.getProperty("OGROOT") ) )
		{
			// Guess a value for OGROOT
System.err.println("Checking OGROOT env...");//XXX
			if ( !Strings.IsNullOrEmpty( System.getenv( "OGROOT" ) ) )
			{
System.err.println("Setting OGROOT from env...");//XXX
//				System.getProperties().setProperty( "OGROOT", System.getenv( "OGROOT" ) );
				System.setProperty( "OGROOT", System.getenv( "OGROOT" ) );
			}
			else
			{
System.err.println("Setting OGROOT to '.'...");//XXX
//				System.getProperties().setProperty( "OGROOT", "." );
				System.setProperty( "OGROOT", "." );
			}
		}
System.err.println("Checking UAROOT property...");//XXX
		if ( Strings.IsNullOrEmpty( System.getProperty("UAROOT") ) )
		{
			// Guess a value for UAROOT
System.err.println("Checking UAROOT env...");//XXX
			if ( !Strings.IsNullOrEmpty( System.getenv( "UAROOT" ) ) )
			{
System.err.println("Setting UAROOT from env...");//XXX
//				System.getProperties().setProperty( "UAROOT", System.getenv( "UAROOT" ) );
				System.setProperty( "UAROOT", System.getenv( "UAROOT" ) );
			}
			else
			{
System.err.println("Setting UAROOT to '.'...");//XXX
//				System.getProperties().setProperty( "UAROOT", "." );
				System.setProperty( "UAROOT", "." );
			}
		}
	}

	private OurGridEnv()
	{
		//empty
	}

	public static synchronized OurGridEnv GetInstance()
	{
		if ( OurGridEnv.instance == null )
		{
			OurGridEnv.instance = new OurGridEnv();
		}

		return OurGridEnv.instance;
	}
}
