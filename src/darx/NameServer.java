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
 * This is the global naming service's interface.<BR>
 * It provides the means for locating replication group leaders.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 **/
public interface NameServer extends Remote {

	/**
	 * The URL of the location of the centralized name server.
	 */
	public final String DEFAULT_URL = "plume.rsr.lip6.fr";

	/**
	 * The port number of the location of the centralized name server.
	 */
	public final int DEFAULT_PORT_NB = 7777;

	/**
	 * The RMI identifier of the centralized name server.
	 */
	public final String SERVICE_NAME = "NameServer";

	/**
	 * Returns the information necessary for obtaining the handle of the current
	 * leader of the given replication group.
	 *
	 * @param task_name
	 *            the generic task name
	 * @return the info concerning the current replication group leader
	 */
	public ReplicantInfo getLeaderOfTask(String task_name)
			throws RemoteException, NoMoreReplicantsException,
			InexistentNameException;

	/**
	 * Selects a replicant as the new leader within its replication group info.
	 * NB: this method implicitly assumes that the current leader is faulty; its
	 * information will consequently be removed from the group info.
	 *
	 * @param old_leader
	 *            the info defining the leader to be replaced
	 * @return the info concerning the new replication group leader
	 */
	public ReplicantInfo selectAnotherLeader(ReplicantInfo old_leader)
			throws RemoteException, NoMoreReplicantsException,
			InexistentNameException;

	/**
	 * Selects a replicant as the new leader within its replication group info.
	 * NB: no failure is assumed when using this method; the change in the
	 * leadership here is an explicit decision from the developer.
	 *
	 * @param rep_info
	 *            the info concerning the replicant to be set as the new leader
	 */
	public void setLeaderOfTask(ReplicantInfo rep_info)
			throws InexistentNameException, RemoteException,
			UnknownReplicantException;

	/**
	 * Selects a replicant as the new leader within its replication group info.
	 * NB: no failure is assumed when using this method; the change in the
	 * leadership here is an explicit decision from the developer.
	 *
	 * @param url
	 *            the host of the replicant to be set as new leader
	 * @param port_nb
	 *            the port nb of the replicant to be set as new leader
	 * @param task_name
	 *            the generic name of the concerned task
	 */
	public ReplicantInfo setLeaderOfTask(String url, int port_nb,
			String task_name) throws InexistentNameException, RemoteException,
			UnknownReplicantException;

	/**
	 * Registers the info concerning a new replicant for a given task. If no
	 * replicant was previously registered, that is if there is no entry
	 * corresponding to the given task_name, the replication group info is
	 * created and the new replicant is assumed to be the group leader.
	 *
	 * @param new_replicant
	 *            the replicant info to register
	 */
	public void register(ReplicantInfo new_replicant) throws RemoteException,
			InexistentNameException;

	/**
	 * Removes the info concerning a replicant for a given task. The killed
	 * replicant MUST NOT be a leader; killing a task is implemented differently
	 * (see method pointer)
	 *
	 * @param task_name
	 *            the name of the task to which the replicant belongs
	 * @param url
	 *            the url of the server hosting the doomed replica
	 * @param port_nb
	 *            the port nb of the server hosting the doomed replica
	 */
	public ReplicantInfo unregister(String task_name, String url, int port_nb)
			throws RemoteException, UnknownReplicantException,
			InexistentNameException;

	/**
	 * Removes the info concerning a whole replication group for a given task.
	 *
	 * @param task_name
	 *            the name of the task being stopped
	 * @return the replication group information for the task being stopped
	 */
	public ReplicationGroupInfo unregisterGroup(String task_name)
			throws RemoteException, InexistentNameException;

}
