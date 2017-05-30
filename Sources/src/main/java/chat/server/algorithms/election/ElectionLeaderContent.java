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
package chat.server.algorithms.election;

import chat.common.AbstractContent;

/**
 * This class defines the content of a leader message of the election algorithm.
 * 
 * TODO add attributes.
 * 
 * TODO implement the constructor.
 * 
 * TODO add getters.
 * 
 * TODO add the toString method.
 * 
 * TODO add your names in the list of authors.
 * 
 * @author Denis Conan, Hamza Hassine, Majdi Haouech
 *
 */
public class ElectionLeaderContent extends AbstractContent {
	/**
	 * version number for serialization.
	 */
	private static final long serialVersionUID = 1L;
	private int caw, parent, win, rec, lrec;
	String status;

	/**
	 * constructs the content of a leader election message.
	 */
	public ElectionLeaderContent() {
		caw = -1;
		parent = -1;
		win = -1;
		rec = 0;
		lrec = 0;
		status = "dormant";
	}
	public int getCaw() {
		return caw;
	}
	public void setCaw(int caw) {
		this.caw = caw;
	}
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public int getLrec() {
		return lrec;
	}
	public void setLrec(int lrec) {
		this.lrec = lrec;
	}
	public int getRec() {
		return rec;
	}
	public void setRec(int rec) {
		this.rec = rec;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String toString() {
		return "";
	}
}
