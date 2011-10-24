/****
 *	Copyright (c) 2007, Olivier Marin, Laboratoire d'Informatique de Paris 6
 *	All rights reserved.
 ****/

/*
 This file is part of DARX.

 DARX is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 DARX is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with DARX. If not, see <http://www.gnu.org/licenses/>.
 */

package darx;

/**
 * This exception is thrown when no replicant of a given task can be found at a
 * specified location.<BR>
 * <BR>
 * It is originally conceived to be used only <B>within</B> replication groups.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class UnknownReplicantException extends DarxException {

	/**
	 *
	 */
	private static final long serialVersionUID = 5680097986094445089L;

	/**
	 * The URL of the host where the replicant is being looked for.
	 **/
	private final String location;

	/**
	 * The name of the task which corresponds to the replication group of the
	 * searched replicant.
	 **/
	private final String task_name;

	/**
	 * Constructs a new instance of the class.
	 *
	 * @param info
	 *            the information concerning the unknown replicant.
	 **/
	public UnknownReplicantException(final ReplicantInfo info) {
		this.location = info.getURL() + ":" + info.getPortNb();
		this.task_name = info.getTaskName();
	}

	/**
	 * @return the URL of the host where the replicant is being looked for.
	 **/
	public String getLocation() {
		return this.location;
	}

	/**
	 * @return the name of the replicated task in concern.
	 **/
	public String getTaskName() {
		return this.task_name;
	}

	/**
	 * @return the information conveyed by this exception, in String format.
	 **/
	@Override
	public String toString() {
		return "The server at location " + this.location
				+ " has not registered any replicant for the task "
				+ this.task_name;
	}
}
