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

package it.unipmn.di.dcs.common.design;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IObservable<T>
{
	/**
	 * Adds an observer to the set of observers for this object, provided
	 * that it is not the same as some observer already in the set.
	 */
	void addObserver(IObserver<T> o);

	/**
	 * Indicates that this object has no longer changed, or that it has
	 * already notified all of its observers of its most recent change, so
	 * that the hasChanged method will now return false.
	 */
//	protected void clearChanged();

	/**
	 * Returns the number of observers of this Observable object.
	 */
	int countObservers();

	/**
	 * Deletes an observer from the set of observers of this object.
	 */
	void deleteObserver(IObserver<T> o);

	/**
	 * Clears the observer list so that this object no longer has any
	 * observers.
	 */
	void deleteObservers();

	/**
	 * Tests if this object has changed.
	 */
	boolean hasChanged();

	/**
	 * If this object has changed, as indicated by the hasChanged method,
	 * then notify all of its observers and then call the clearChanged
	 * method to indicate that this object has no longer changed.
	 */
	void notifyObservers();

	/**
	 * If this object has changed, as indicated by the hasChanged method,
	 * then notify all of its observers and then call the clearChanged
	 * method to indicate that this object has no longer changed.
	 */
	void notifyObservers(T arg);

	/**
	 * Marks this Observable object as having been changed; the hasChanged
	 * method will now return true.
	 */
//	protected void setChanged();
}
