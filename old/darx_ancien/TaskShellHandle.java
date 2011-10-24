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
import java.rmi.RemoteException;

/**
 * This is the remote interface used within replication groups to access a
 * distant <code>TaskShell</code>.<BR>
 * It is designed to be possessed and used by the replication group leaders for
 * group maintenance purposes.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 *
 * @see TaskShell
 */
interface TaskShellHandle extends DarxHandle {

	/**
	 * Starts the processing of an asynchronous message.
	 *
	 * @param msg
	 *            the message to be processed
	 * @see DarxMessage
	 **/
	void processDeliverAsyncMessage(DarxMessage msg) throws RemoteException;

	/**
	 * Starts the processing of a synchronous message.
	 *
	 * @param msg
	 *            the message to be processed
	 * @return the reply to the message
	 * @see DarxMessage
	 **/
	Serializable processDeliverSyncMessage(DarxMessage msg)
			throws RemoteException;

	/**
	 * @return the information concerning the present replicant
	 */
	public ReplicantInfo getInfo() throws RemoteException;

	/**
	 * @return the encapsulated task
	 */
	public DarxTask getTask() throws RemoteException;

	/**
	 * @return the replication policy of the local task
	 * @see ReplicationPolicy
	 */
	public ReplicationPolicy getReplicationPolicy() throws RemoteException;

	// -------------------//
	// ---- MODIFIERS ----//
	// -------------------//

	/**
	 * Inserts a given task inside the current TaskShell.
	 *
	 * @param task
	 *            the new task to encapsulate
	 * @see DarxTask
	 * @see PassiveReplicationStrategy#update()
	 */
	public void setTask(DarxTask task) throws RemoteException;

	/**
	 * Sets the information concerning the current TaskShell.
	 *
	 * @param info
	 *            the info to set
	 * @see ReplicantInfo
	 */
	public void setInfo(ReplicantInfo info) throws RemoteException;

	/**
	 * Sets the information concerning the replication policy applied to the
	 * current TaskShell.
	 *
	 * @param rep_policy
	 *            the info to set
	 * @see ReplicationPolicy
	 */
	public void setPolicy(ReplicationPolicy rep_policy) throws RemoteException;

	/**
	 * Sets this replicant as the leader of its group. This is an indirect way
	 * of modifying the <code>ReplicationStrategy</code>.
	 *
	 * @see ReplicationStrategy
	 */
	public void setAsGroupLeader() throws RemoteException;

	/**
	 * Sets this replicant as a backup within its group. This is an indirect way
	 * of modifying the <code>ReplicationStrategy</code>.
	 *
	 * @see ReplicationStrategy
	 */
	public void demoteFromGroupLeader() throws RemoteException;

	/**
	 * Suspends the local execution of the encapsulated task. N.B: This does not
	 * involve suspending the local replication policy.
	 */
	void suspend() throws RemoteException;

	/**
	 * Resumes the local execution of the encapsulated task. N.B: This does not
	 * involve resuming the local replication policy.
	 */
	void resume() throws RemoteException;

	/**
	 * Terminates the execution of the encapsulated task. This includes the
	 * removal of the global application information concerning the shell.
	 */
	public void terminate() throws RemoteException;

	/**
	 * Terminates the execution of the encapsulated task. This does not include
	 * the removal of the global application references to the encapsulation
	 * shell.
	 */
	public void terminateTask() throws RemoteException;
}
