/**
This file is part of the muDEBS middleware.

Copyright (C) 2012-2017 Télécom SudParis

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This software platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the muDEBS platform. If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s):
 */
package chat.common;

import static chat.common.Log.GEN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class defines a vector clock with a Map. A vector clock is serializable
 * to be inserted in messages and is cloneable for copying to/from messages.
 */
public class VectorClock implements Serializable, Cloneable {
	/**
	 * serial version unique identifier for serialization.
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * the map of the vector clock. The key is the identity (integer) of the
	 * process and the objec is the value (integer) of the clock of the process.
	 */
	private HashMap<Integer, Integer> vectorClock;

	/**
	 * the constructor.
	 */
	public VectorClock() {
		vectorClock = new HashMap<>();
		assert invariant();
	}

	/**
	 * checks the invariant of the class: scalar clock are greater than or equal
	 * to 0.
	 * 
	 * NB: the method is final so that the method is not overriden in potential
	 * subclasses because it is called in the constructor.
	 * 
	 * @return a boolean stating whether the invariant is maintained.
	 */
	public final boolean invariant() {
		boolean result = true;
		for (Integer value : vectorClock.values()) {
			if (value.intValue() < 0) {
				return false;
			}
		}
		return result;
	}

	/**
	 * gets the value (integer) of the clock of a process (integer key). The
	 * returned value is either the value found or the default value <tt>0</tt>.
	 * 
	 * @param key
	 *            the identifier (integer) of the process.
	 * @return the clock value.
	 */
	public Integer getEntry(final Integer key) {
		return vectorClock.getOrDefault(key, 0);
	}

	/**
	 * sets the value (integer) of the clock of the process (integer key). If
	 * the corresponding key does not already exists in the map, it is inserted.
	 * 
	 * @param key
	 *            the identifier (integer) of the process. An
	 *            IllegalArgumentException is thrown in case of a negative
	 *            value.
	 * @param value
	 *            the value of the clock.
	 * @return the new value.
	 */
	public Integer setEntry(final Integer key, final Integer value) {
		if (key < 0) {
			throw new IllegalArgumentException(
					"identite de processus non valide (" + key + ")");
		}
		vectorClock.put(key, value);
		assert invariant();
		return getEntry(key);
	}

	/**
	 * increments the clock of a given process (integer). If the corresponding
	 * key does not already exists in the map, it is inserted with the value 1,
	 * that is to say as if it were 0 before the call.
	 * 
	 * @param key
	 *            the identifier (integer) of the process.
	 */
	public void incrementEntry(final Integer key) {
		if (key < 0) {
			throw new IllegalArgumentException(
					"identite de processus non valide (" + key + ")");
		}
		vectorClock.put(key, getEntry(key) + 1);
		assert invariant();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		VectorClock clone = null;
		try {
			clone = (VectorClock) super.clone();
			clone.vectorClock = (HashMap<Integer, Integer>) vectorClock.clone();
		} catch (CloneNotSupportedException e) {
			GEN.fatal(e.getLocalizedMessage());
			e.printStackTrace();
		}
		assert invariant();
		assert clone.invariant();
		return clone;
	}

	/**
	 * computes the maximum of the vector and the one provided in the argument.
	 * If a given key exists in one of the vector but not in the other, the
	 * value is set to the one that is present.
	 * 
	 * @param other
	 *            the other vector clock for the computation.
	 */
	public void max(final VectorClock other) {
		if (other != null) {
			List<Integer> keys = new ArrayList<>(vectorClock.keySet());
			keys.addAll(other.vectorClock.keySet());
			for (Integer key : keys) {
				setEntry(key, Math.max(vectorClock.getOrDefault(key, 0),
						other.vectorClock.getOrDefault(key, 0)));
			}
		}
		assert invariant();
	}

	/**
	 * states whether this vector is clock is preceded by the vector clock
	 * provided in the argument <tt>other</tt>, except for the entry
	 * <tt>sender</tt>, for which the corresponding value in this vector clock
	 * is the next one to be received (compared to <tt>other</tt>). This
	 * condition is for testing of the causal relationship (that includes the
	 * FIFO property [no hole]) before the reception.
	 * 
	 * More precisely, considering a message with a vector clock provided in the
	 * argument <tt>other</tt> and the identity of the sender of the message, it
	 * states whether the following condition is <tt>true</tt>:
	 * <ul>
	 * <li>for the entries of the vector clocks that corresponds to the sender
	 * of the message, the value of this vector clock
	 * (<tt>this.getEntry(sender)</tt>) is one step behind the value of the
	 * message vector clock (<tt>other.getEntry(sender)</tt>), and</li>
	 * <li>for all the other entries (<tt>key</tt>) of the vector clocks, the
	 * value of this vector clock (<tt>this.getEntry(key)</tt> is preceded by
	 * the value of the message vector clock (<tt>other.getEntry(sender)</tt>).
	 * </li>
	 * </ul>
	 * 
	 * @param other
	 *            the vector clock of the message.
	 * @param sender
	 *            the sender of the message.
	 * @return the boolean of the condition.
	 */
	public boolean isPrecededByAndFIFO(final VectorClock other,
			final int sender) {
		if (other != null) {
			List<Integer> keys = new ArrayList<>(vectorClock.keySet());
			keys.addAll(other.vectorClock.keySet());
			if (other.getEntry(sender) != (this.getEntry(sender) + 1)) {
				return false;
			}
			for (Integer key : keys) {
				if (key.equals(sender)) {
					continue;
				} else {
					if (other.getEntry(key) > this.getEntry(key)) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return vectorClock.toString();
	}
}