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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the remote interface used by application-level entities to access a
 * distant <code>TaskShell</code>.<BR>
 * <BR>
 * The application-level entities include unreplicated agents, leaders of
 * replication groups which do not include the accessed <code>TaskShell</code>,
 * as well as application programmers.<BR>
 * The <code>DarxHandle</code> is designed to be encapsulated in a
 * <code>RemoteTask</code> instance; therefore it ought to be the stub of a
 * replication group leader.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @see RemoteTask
 * @see TaskShell
 *
 * @version %I%, %G%
 **/
public interface DarxHandle extends Remote {

	// Message sending methods

	/**
	 * Sends an asynchronous message to the remote task.
	 *
	 * @param msg
	 *            the message destined to the remote task.
	 * @throws RemoteException
	 *
	 * @see TaskShell#deliverAsyncMessage(DarxMessage msg)
	 **/
	void deliverAsyncMessage(DarxMessage msg) throws RemoteException;

	/**
	 * Sends a synchronous message to the remote task.
	 *
	 * @param msg
	 *            the message destined to the remote task.
	 * @return the message sent in reply.
	 * @throws RemoteException
	 *
	 * @see TaskShell#deliverSyncMessage(DarxMessage msg)
	 **/
	Serializable deliverSyncMessage(DarxMessage msg) throws RemoteException;

	// Replication management

	/**
	 * Creates a new replicant inside the replication group at a given location.
	 *
	 * @param url
	 *            the location where the new replicant is to be created
	 * @param port_nb
	 *            the port corresponding to the server
	 * @param rs
	 *            the replication strategy applied to the new replica
	 * @throws RemoteException
	 * @throws IllegalReplicationException
	 *             if a replica of the same group already exists at the location
	 **/
	void replicateTo(String url, int port_nb, ReplicationStrategy rs)
			throws RemoteException, IllegalReplicationException;

	/**
	 * Deletes the replicant residing at the given location, provided it belongs
	 * to the replication group.
	 *
	 * @param url
	 *            the location where the new replicant is to be created
	 * @param port_nb
	 *            the port corresponding to the server
	 * @throws RemoteException
	 * @throws UnknownReplicantException
	 *             if there is no replicant from the group at the given location
	 **/
	void killReplicantAt(String url, int port_nb) throws RemoteException,
	UnknownReplicantException;

	/**
	 * Ends the generic task execution. This method spreads the termination
	 * process throughout the replication group.
	 *
	 * @throws RemoteException
	 **/
	void killTask() throws RemoteException;

	/**
	 * Modifies the current replication policy of the group, by applying a new
	 * strategy to a given replica.
	 *
	 * @param url
	 *            the URL of the replica to which the new strategy applies
	 * @param port_nb
	 *            the port number of the replica to which the new strategy
	 *            applies
	 * @param strategy
	 *            the new strategy to be applied
	 * @see DarxHandle
	 * @see ReplicationStrategy
	 * @see TaskShell#switchReplicationStrategy
	 * @throws RemoteException
	 */
	public void switchReplicationStrategy(String url, int port_nb,
			ReplicationStrategy strategy) throws RemoteException;

	/**
	 * @return the current replication policy of the related replication group.
	 * @see ReplicationPolicy
	 * @throws RemoteException
	 */
	ReplicationPolicy getReplicationPolicy() throws RemoteException;
}
