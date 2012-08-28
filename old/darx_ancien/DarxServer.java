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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the server interface which, as main objective, provides the means for
 * running DARX tasks on a specific location. A location is a logical network
 * node defined by its URL and its port number.
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 *         version %I%, %G%
 **/
public interface DarxServer extends Remote {

	/**
	 * Starts the execution of a new replication group leader on the current
	 * server.
	 *
	 * @param task
	 *            the <code>DarxTask</code> to be started on this server.
	 * @return the handle for the started task; to be used for user purposes,
	 *         such as (un)replication or policy modification.
	 * @see DarxTask#activateTask(String, int)
	 */
	public RemoteTask startTask(DarxTask task) throws RemoteException;

	/**
	 * Creates a task replica on the current server. This method is called by
	 * the <code>TaskShell</code> of the group leader when it has to be
	 * replicated.
	 *
	 * @param info
	 *            the <code>ReplicantInfo</code> of the new replica.
	 * @param task
	 *            the <code>DarxTask</code> to be replicated on this server.
	 * @return the remote reference to the newly created replica
	 */
	public TaskShellHandle createReplicant(ReplicantInfo info, DarxTask task)
			throws RemoteException;

	/**
	 * Destroys the local replicant corresponding to the specified task. This
	 * includes unreferencing the doomed replicant on the local RMI server and
	 * on the name server.
	 *
	 * @param task_name
	 *            the name of the task to which the replicant belongs
	 */
	public void killReplicant(String darx_name) throws RemoteException;

	/**
	 * Generates a remote reference to the current leader of the task which
	 * corresponds to the given generic name. If the current leader cannot be
	 * contacted, it is assumed that the host has failed, and therefore a new
	 * leader election is launched through the <code>NameServer</code> services.
	 **/
	public RemoteTask findTask(String task_name) throws RemoteException;
}
