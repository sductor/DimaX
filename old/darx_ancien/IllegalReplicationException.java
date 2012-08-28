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
 * This exception is thrown when a replication is being attempted at a location
 * where there already exists a replica of the same group.<BR>
 *
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class IllegalReplicationException extends DarxException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6829768613078749959L;

	/**
	 * The URL of the host where the replication is attempted.
	 **/
	private final String url;

	/**
	 * The port number of the host where the replication is attempted.
	 **/
	private final int port_nb;

	/**
	 * The name of the task which corresponds to the replication group for which
	 * the invalid replication is attempted.
	 **/
	private String task_name;

	/**
	 * Constructs a new instance of the class.
	 *
	 * @param info
	 *            the information concerning the unknown replicant.
	 **/
	public IllegalReplicationException(final String url, final int port_nb) {
		this.url = url;
		this.port_nb = port_nb;
	}

	/**
	 * @return the URL of the host where the replication is attempted.
	 **/
	public String getLocation() {
		return this.url;
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
		return "The server at location " + this.url + ":" + this.port_nb
				+ " has already registered a replicant for the task "
				+ this.task_name;
	}
}
