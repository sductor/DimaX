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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import negotiation.negotiationframework.AbstractCommunicationProtocol.SimpleContractEnvellope;

/* import dimaxx.server.Logger; */

/**
 * This is the local proxy that allows user application tasks to handle other
 * tasks. This class defines the core services Darx provides to user
 * applications for handling the different tasks (or agents) of the distributed
 * system. It includes replication management and message sending. At length,
 * the replication management scheme should become transparent to the user, and
 * therefore might disappear from the current services provided herein.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class RemoteTask implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3368972350581433951L;

	/**
	 * The handle for the leader of the group which represents the task
	 */
	public DarxHandle handle;

	/**
	 * The information concerning the leader of the task
	 */
	private ReplicantInfo leader_info;

	/**
	 * the generic name of the task
	 */
	private String task_name;

	/**
	 * Constructs a user-sufficient definition of a remote task. This is the
	 * unique class constructor. It is not supposed to be called from user
	 * applications, however; instances of this class are obtained when starting
	 * the corresponding DarxTask.
	 *
	 * @param new_leader_info
	 *            the information concerning the leader of the task which this
	 *            instance references.
	 * @param handle
	 *            the interface to access the leader in charge of this task.
	 * @see DarxHandle
	 * @see TaskShell
	 */
	RemoteTask(final ReplicantInfo new_leader_info, final DarxHandle handle) {
		this.leader_info = new_leader_info;
		this.task_name = this.leader_info.getTaskName();
		this.handle = handle;
	}

	/**
	 * Constructs a user-sufficient definition of a remote task. This is the
	 * unique class constructor. It is not supposed to be called from user
	 * applications, however; instances of this class are obtained when starting
	 * the corresponding DarxTask.
	 *
	 * @param new_leader_info
	 *            the information concerning the leader of the task which this
	 *            instance references.
	 * @see DarxHandle
	 * @see TaskShell
	 */
	RemoteTask(final ReplicantInfo new_leader_info) throws RemoteException {
		this.leader_info = new_leader_info;
		this.task_name = this.leader_info.getTaskName();
		this.handle = this.leader_info.getTaskShellHandle();
	}

	/**
	 * Sets the information concerning the leader of the remote task.
	 *
	 * @param new_leader_info
	 *            the information concerning the current leader.
	 */
	void setLeaderInfo(final ReplicantInfo new_leader_info)
			throws RemoteException {
		if (this.leader_info != new_leader_info) {
			this.leader_info = new_leader_info;
			this.task_name = this.leader_info.getTaskName();
			this.handle = this.leader_info.getTaskShellHandle();
		}
	}

	/**
	 * Sends a message asynchronously to the remote task, that is to the leader
	 * of the corresponding replication group. If the first emission fails, the
	 * thread is interrupted for a while and a second emission attempt is
	 * initiated. On a second failure, a new leader selection is asked from the
	 * <code>NameServer</code> and the whole emission procedure is restarted.
	 *
	 * @param msg
	 *            the contents of the emission.
	 *
	 * @see DarxHandle#deliverAsyncMessage(DarxMessage msg)
	 */
	public void sendAsyncMessage(final DarxMessage msg) {
		try {
			this.handle.deliverAsyncMessage(msg);
		} catch (final RemoteException e) {
			Logger.exception(this, "Asynchronous message not delivered "+msg.getContents()+"\n -->"+((SimpleContractEnvellope)msg.getContents()).getMyContract().getClass(), e);
			try {
				Logger.exception(this, "Trying emission once more...");
				// Take a nap
				java.lang.Thread.sleep(4000);
				// Try emitting again
				this.handle.deliverAsyncMessage(msg);
			} catch (final InterruptedException e1) {
				Logger.exception(this, "Can't go to sleep before reemission!",
						e1);
			} catch (final RemoteException e2) {
				Logger.exception(this, "Asynchronous message not delivered");
				Logger.exception(this, "Searching other replicant", e2);
				try {
					this.findNewLeader();
					// New leader was found: restart message emission
					this.sendAsyncMessage(msg);
				} catch (final RemoteException e3) {
					Logger.exception(this, "Can't find new leader!", e3);
					// System.exit(1);
				}
			}
		}
	}

	/**
	 * Sends a message synchronously to the remote task, that is to the leader
	 * of the corresponding replication group.
	 *
	 * @param msg
	 *            the contents of the emission.
	 *
	 * @see DarxHandle#deliverSyncMessage(DarxMessage msg)
	 */
	Object sendSyncMessage(final DarxMessage msg) {
		Object reply = null;
		try {
			reply = this.handle.deliverSyncMessage(msg);
		} catch (final RemoteException e) {
			System.out
			.println("Synchronous message cannot be delivered : " + e);
			System.out.println("Trying emission once more...");
			try {
				// Take a nap
				java.lang.Thread.sleep(4000);
				// Try emitting again
				reply = this.handle.deliverSyncMessage(msg);
			} catch (final InterruptedException e1) {
				System.out.println("Can't go to sleep before reemission!");
				e1.printStackTrace();
			} catch (final RemoteException e2) {
				System.out.println("Asynchronous message not delivered");
				e2.printStackTrace();
				System.out.println("Searching other replicant");
				try {
					this.findNewLeader();
				} catch (final RemoteException e3) {
					System.out.println("Can't find new leader!");
					e3.printStackTrace();
					System.exit(1);
				}
				// New leader was found: restart message emission
				reply = this.sendSyncMessage(msg);
			}
		}
		return reply;
	}

	/**
	 * Starts the mechanism to create a new replica of the remote task at a
	 * specified URL. The default port (6789) is used and the default (active)
	 * replication strategy is applied.
	 *
	 * @param url
	 *            the URL of the remote DARX server where the new replica is
	 *            created
	 * @see DarxHandle
	 * @see Darx#createReplicant
	 * @see TaskShell#replicateTo
	 */
	public void replicateTo(final String url) throws RemoteException {
		this.handle.replicateTo(url, 6789, new ActiveReplicationStrategy());
	}

	/**
	 * Starts the mechanism to create a new replica of the remote task on a
	 * specified server. The default (active) replication strategy is applied.
	 * This method enables to specify the server's port number; it is mandatory
	 * to use it instead of its homonym when there are several DARX servers
	 * concurrently running on the same host.
	 *
	 * @param url
	 *            the URL of the remote DARX server where the new replica is
	 *            created
	 * @param port_nb
	 *            the port number of the remote DARX server where the new
	 *            replica is created
	 * @see DarxHandle
	 * @see Darx#createReplicant
	 * @see TaskShell#replicateTo
	 */
	public void replicateTo(final String url, final int port_nb)
			throws RemoteException {
		this.handle.replicateTo(url, port_nb, new ActiveReplicationStrategy());
	}

	/**
	 * Starts the mechanism to create a new replica of the remote task on a
	 * specified server following a given replication strategy. This method
	 * enables to specify the server's port number; it is mandatory to use it
	 * instead of its homonym when there are several DARX servers concurrently
	 * running on the same host.
	 *
	 * @param url
	 *            the URL of the remote DARX server where the new replica is
	 *            created
	 * @param port_nb
	 *            the port number of the remote DARX server where the new
	 *            replica is created
	 * @param rs
	 *            the replication strategy applied to the new replica
	 * @see DarxHandle
	 * @see Darx#createReplicant
	 * @see TaskShell#replicateTo
	 */
	public void replicateTo(final String url, final int port_nb,
			final ReplicationStrategy rs) throws RemoteException,
			IllegalReplicationException {
		this.handle.replicateTo(url, port_nb, rs);
	}

	/**
	 * Starts the mechanism to destroy a specific replica of the remote task on
	 * a given server.
	 *
	 * @param url
	 *            the URL of the remote DARX server hosting the doomed replica
	 * @exception RemoteException
	 * @see DarxHandle
	 */
	public void killReplicantAt(final String url) throws RemoteException,
	UnknownReplicantException {
		this.handle.killReplicantAt(url, 6789);
	}

	/**
	 * Starts the mechanism to destroy a specific replica of the remote task on
	 * a given server. This method enables to specify the server's port number;
	 * it is mandatory to use it instead of its homonym when there are several
	 * DARX servers concurrently running on the same host.
	 *
	 * @param url
	 *            the URL of the remote DARX server supporting the doomed
	 *            replica
	 * @param port_nb
	 *            the port number of the same remote DARX server
	 * @exception RemoteException
	 * @see DarxHandle
	 */
	public void killReplicantAt(final String url, final int port_nb)
			throws RemoteException, UnknownReplicantException {
		this.handle.killReplicantAt(url, port_nb);
	}

	/**
	 * Terminates the execution of this replication group.
	 */
	public void killTask() throws RemoteException {
		this.handle.killTask();
	}

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
	 */
	public void switchReplicationStrategy(final String url, final int port_nb,
			final ReplicationStrategy strategy) throws RemoteException {
		this.handle.switchReplicationStrategy(url, port_nb, strategy);
	}

	/**
	 * Get the current replication policy of the remote task
	 *
	 * @return the current replication policy of the remote task
	 * @see ReplicationPolicy
	 */
	public ReplicationPolicy getReplicationPolicy() throws RemoteException {
		return this.handle.getReplicationPolicy();
	}

	/**
	 * Launches the election of a new leader. This method is called internally
	 * when the current leader fails to answer. <I>NB: in the absence of a
	 * proper FD, a leader failing to answer is considered as having crashed</I>
	 */
	private void findNewLeader() throws RemoteException {
		System.out.println("Requesting new leader for task " + this.task_name);
		try {
			// Obtain access to naming services
			final Registry nserver_reg = LocateRegistry.getRegistry(
					NameServerImpl.URL, NameServerImpl.PORT_NB);
			final String nserver_name = NameServerImpl.URL + ":"
					+ NameServerImpl.PORT_NB + "/" + NameServer.SERVICE_NAME;
			final NameServer nserver = (NameServer) nserver_reg
					.lookup(nserver_name);
			// Request new leader election
			this.leader_info = nserver.selectAnotherLeader(this.leader_info);
			if (this.leader_info == null)
				throw new DarxException();
			System.out.println("NS elected "
					+ this.leader_info.textifyDarxPath());
			// Retrieve the reference to the new leader & set it as leader
			final Registry host_reg = LocateRegistry.getRegistry(
					this.leader_info.getURL(), this.leader_info.getPortNb());
			final TaskShellHandle new_leader_handle = (TaskShellHandle) host_reg
					.lookup(this.leader_info.textifyDarxName());
			new_leader_handle.setAsGroupLeader();
			this.handle = new_leader_handle;
		} catch (final DarxException e) {
			System.out.println("New leader for task " + this.task_name
					+ " cannot be found");
			e.printStackTrace();
		} catch (final NotBoundException e) {
			System.out
			.println("RMI registration pb while fetching new leader for "
					+ this.task_name);
			e.printStackTrace();
		}
	}

	/**
	 * Sets the replica at the given location as the new leader for its group.
	 * NB: This includes triggering the notification of the new group hierarchy
	 * to the concerned members (the old and the new leader)
	 *
	 * @param url
	 *            the url of the server hosting the new leader
	 * @param port_nb
	 *            the port nb of the server hosting the new leader
	 */
	public void setLeaderAt(final String url, final int port_nb)
			throws RemoteException {
		try {
			// Obtain access to naming services
			final Registry nserver_reg = LocateRegistry.getRegistry(
					NameServerImpl.URL, NameServerImpl.PORT_NB);
			final String nserver_name = NameServerImpl.URL + ":"
					+ NameServerImpl.PORT_NB + "/" + NameServer.SERVICE_NAME;
			final NameServer nserver = (NameServer) nserver_reg
					.lookup(nserver_name);
			// Retrieve info for the old leader
			final ReplicantInfo old_leader_info = nserver
					.getLeaderOfTask(this.task_name);
			// Elect the new leader through its location
			final ReplicantInfo new_leader_info = nserver.setLeaderOfTask(url,
					port_nb, this.task_name);
			// Retrieve the interface for the old leader
			Registry host_reg = LocateRegistry.getRegistry(old_leader_info
					.getURL(), old_leader_info.getPortNb());
			final TaskShellHandle old_handle = (TaskShellHandle) host_reg
					.lookup(old_leader_info.textifyDarxName());
			// Retrieve the interface for the new leader
			host_reg = LocateRegistry.getRegistry(url, port_nb);
			final TaskShellHandle new_handle = (TaskShellHandle) host_reg
					.lookup(new_leader_info.textifyDarxName());
			// Swap roles and notify the concerned replicas
			// (namely the old and the new leader)
			new_handle.setAsGroupLeader();
			old_handle.demoteFromGroupLeader();
			this.handle = new_handle;
		} catch (final DarxException e) {
			System.out.println("New leader for task " + this.task_name
					+ " cannot be elected");
			e.printStackTrace();
		} catch (final NotBoundException e) {
			System.out
			.println("RMI registration pb while setting new leader for "
					+ this.task_name);
			e.printStackTrace();
		}
	}

}
