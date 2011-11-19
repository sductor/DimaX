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
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

/* import dimaxx.server.Logger; */

/**
 * This is the wrapper that transparently performs FT-specific operations for
 * each task/agent.<BR>
 * <BR>
 * The FT services provided here are :
 * <ul>
 * <li>access to the task's replication group manager,
 * <li>group communications management via message monitoring & caching,
 * <li>replicant creation & termination.
 * </ul>
 * The TaskShell is also responsible for relaying user commands concerning the
 * task/agent's execution.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class TaskShell extends UnicastRemoteObject implements TaskShellHandle {

	/**
	 *
	 */
	private static final long serialVersionUID = -729661802186609222L;

	/**
	 * The task/agent for which the current shell provides FT support.
	 */
	DarxTask task;

	/**
	 * The replicant information for this task.
	 */
	private ReplicantInfo info;

	/**
	 * This particular task's replication policy.
	 *
	 * @see ReplicationPolicy
	 */
	private ReplicationPolicy policy;

	/**
	 * The element in charge of handling the replication policy for the group
	 * which includes this shell. IMPORTANT: the leader alone possesses a
	 * replication manager.
	 *
	 * @see ReplicationManager
	 */
	private final ReplicationManager rep_manager = null;

	/**
	 * Cache for incoming and outgoing messages.
	 */
	private final Hashtable<String, Serializable> lastMsgs, replyCache;

	// ---------------------//
	// ---- CONSTRUCTOR ----//
	// ---------------------//

	/**
	 * Constructs a shell that provides transparent FT for a given task/agent.
	 * This is the only constructor for this class.<BR>
	 * It is not supposed to be called from user applications, however;
	 * instances of this class are created when starting the corresponding
	 * <code>DarxTask</code>.
	 *
	 * @param task
	 *            the task/agent to provide FT for
	 * @param name
	 *            the complete DARX path name for this task; it includes the
	 *            location and the replicant number
	 * @param strategy
	 *            the current replication group information
	 * @see DarxTask
	 * @see ReplicationStrategy
	 */
	TaskShell(final DarxTask task, final ReplicantInfo info)
			throws RemoteException {
		this.task = task;
		this.info = info;
		this.policy = new ReplicationPolicy(info);
		this.lastMsgs = new Hashtable<String, Serializable>();
		this.replyCache = new Hashtable<String, Serializable>();
	}

	// -------------------//
	// ---- SELECTORS ----//
	// -------------------//

	/**
	 * @return the information concerning the present replicant
	 */
	@Override
	public synchronized ReplicantInfo getInfo() throws RemoteException {
		return this.info;
	}

	/**
	 * @return the encapsulated task
	 */
	@Override
	public DarxTask getTask() throws RemoteException {
		return this.task;
	}

	/**
	 * @return the replication policy of the local task
	 * @see ReplicationPolicy
	 */
	@Override
	public ReplicationPolicy getReplicationPolicy() throws RemoteException {
		return this.policy;
	}

	/**
	 * @return the replication manager of the local task
	 * @see ReplicationManager
	 */
	public ReplicationManager getReplicationManager() throws RemoteException {
		return this.rep_manager;
	}

	// -------------------//
	// ---- MODIFIERS ----//
	// -------------------//

	/**
	 * Inserts a given task inside the current TaskShell. NB: Its current use is
	 * exclusively that of the passive replication strategy backup.
	 *
	 * @param task
	 *            the new task to encapsulate
	 * @see DarxTask
	 * @see PassiveReplicationStrategy#update()
	 */
	@Override
	public void setTask(final DarxTask task) throws RemoteException {
		this.task = task;
	}

	/**
	 * Sets the information concerning the current TaskShell.
	 *
	 * @param info
	 *            the info to set
	 * @see ReplicantInfo
	 */
	@Override
	public void setInfo(final ReplicantInfo info) throws RemoteException {
		this.info = info;
	}

	/**
	 * Sets the information concerning the replication policy applied to the
	 * current TaskShell.
	 *
	 * @param rep_policy
	 *            the info to set
	 * @see ReplicationPolicy
	 */
	@Override
	public void setPolicy(final ReplicationPolicy rep_policy)
			throws RemoteException {
		this.policy = rep_policy;
	}

	/**
	 * Sets this replicant as the leader of its group.<BR>
	 *
	 * @see ReplicationStrategy
	 */
	@Override
	public void setAsGroupLeader() throws RemoteException {
		this.task.leader = true;
		// Instanciate ReplicationManager & resume replication strategies
	}

	/**
	 * Demotes this replicant from the status of leader to that of a simple
	 * replica within its group.<BR>
	 *
	 * @see ReplicationStrategy
	 */
	@Override
	public void demoteFromGroupLeader() throws RemoteException {
		this.task.leader = false;
		// Suspend replication strategies & destroy ReplicationManager
	}

	/**
	 * Modifies the replication policy by applying a different strategy to a
	 * given replica.<BR>
	 *
	 * @param url
	 *            the url of the location which hosts the given replica
	 * @param port_nb
	 *            the port nb of the same location
	 * @param rs
	 *            the strategy to apply to the given replica
	 * @throws RemoteException
	 * @throws UnknownReplicantException
	 *             if there is no replicant from the group at the given location
	 * @see ReplicationStrategy
	 */
	@Override
	public synchronized void switchReplicationStrategy(final String url,
			final int port_nb, final ReplicationStrategy rs)
			throws RemoteException, UnknownReplicantException {
		final ReplicantInfo ri = this.policy.containsReplicant(url, port_nb);
		this.suspend();
		this.policy.suspend();
		this.policy.switchAppliedStrategy(rs, ri);
		this.policy.publishReplicationPolicy();
		this.policy.resume();
		this.resume();
	}

	// -----------------------------------//
	// ---- COMMUNICATIONS MANAGEMENT ----//
	// -----------------------------------//

	/**
	 * Checks the validity of a received message.<BR>
	 * This method discards messages for which the serial number is obsolete
	 * relatively to the sender, that is a message with a superior serial has
	 * already been received from the same sender. If a message is accepted, its
	 * serial number is recorded for later checks.
	 */
	boolean acceptMsg(final DarxMessage msg) {
		final String sender_name = msg.getSenderName();
		if (this.lastMsgs.containsKey(sender_name)
				&& ((Integer) this.lastMsgs.get(sender_name)).intValue() >= msg
						.getSerial()) {
			// Discard message
			Logger.exception(this, "Ce message aurait du être refusé:\n"
					+ msg.getContents());
			return true;// false;
		} else {
			// Accept message
			this.lastMsgs.put(sender_name, new Integer(msg.getSerial()));
			return true;
		}
	}

	@Override
	public void processDeliverAsyncMessage(final DarxMessage msg)
			throws RemoteException {
		if (this.acceptMsg(msg))
			new MessageDeliverThread(msg, this.task).start();
	}

	@Override
	public Serializable processDeliverSyncMessage(final DarxMessage msg)
			throws RemoteException {
		Serializable reply;
		if (this.acceptMsg(msg)) {
			reply = this.task.receiveSyncMessage(msg.getContents());
			this.replyCache.put(msg.getSenderName() + msg.getSerial(), reply);
		} else
			reply = this.replyCache.get(msg.getSenderName() + msg.getSerial());
		// This request has already been processed : return cached reply
		return reply;
	}

	// *****************************************************

	@Override
	public synchronized void deliverAsyncMessage(final DarxMessage msg)
			throws RemoteException {
		this.policy.deliverAsyncMessage(this, msg);
	}

	@Override
	public synchronized Serializable deliverSyncMessage(final DarxMessage msg)
			throws RemoteException {
		return this.policy.deliverSyncMessage(this, msg);
	}

	// --------------------------------------//
	// ---- REPLICATION GROUP MANAGEMENT ----//
	// --------------------------------------//

	/**
	 * Retrieves the reference to a remote DARX server.
	 *
	 * @param url
	 *            the location of the remote DARX server
	 * @param port_nb
	 *            the port corresponding to the remote DARX server
	 */
	private DarxServer getRemoteServerHandle(final String url, final int port_nb) {
		DarxServer server = null;
		final String server_name = url + ":" + port_nb + "/DarxServer";
		System.out.println("Retrieving remote reference to server at: " + url
				+ ":" + port_nb);
		try {
			final Registry server_reg = LocateRegistry
					.getRegistry(url, port_nb);
			server = (DarxServer) server_reg.lookup(server_name);
		}
		/*
		 * catch(MalformedURLException e) { throw new
		 * DarxMalformedURLException(server_name); }
		 */
		catch (final NotBoundException nbe) {
			System.out.println("Internal error : no Darx server bound at "
					+ url + ":" + port_nb);
		} catch (final RemoteException re) {
			System.out
					.println("Internal error : cannot contact Darx server at "
							+ url + ":" + port_nb);
			re.printStackTrace();
		}
		return server;
	}

	/**
	 * Creates a new replicant inside the replication group at a given location. <BR>
	 * <BR>
	 * This action includes informing the replication group of the replicant's
	 * creation.<BR>
	 * This can involve suspending the whole replication group, thus justifying
	 * a synchronized policy.
	 *
	 * @param url
	 *            the location where the new replicant is to be created
	 * @param port_nb
	 *            the port corresponding to the server
	 * @param rs
	 *            the replication strategy applied to the new replicant
	 * @throws RemoteException
	 * @throws IllegalReplicationException
	 *             if a replica of the same group already exists at the location
	 * @see DarxServer
	 * @see ReplicationStrategy
	 */
	@Override
	public synchronized void replicateTo(final String url, final int port_nb,
			final ReplicationStrategy rs) throws RemoteException,
			IllegalReplicationException {
		// Check that there is no replica already present at the given location
		// Generate the information for the new replica if the location is valid
		ReplicantInfo new_rep_info = null;
		try {
			this.policy.containsReplicant(url, port_nb);
		} catch (final UnknownReplicantException e) {
			new_rep_info = this.policy.generateNewInfo(url, port_nb);
		}
		if (new_rep_info == null)
			throw new IllegalReplicationException(url, port_nb);
		// Obtain the remote DarxServer stub corresponding to 'url' & 'port_nb'
		final DarxServer server = this.getRemoteServerHandle(url, port_nb);
		new_rep_info.textifyDarxName();
		// Suspend the current task's replication group
		this.suspend();
		this.policy.suspend();
		// Create the replicant on the remote server
		// Obtain the remote reference to the new task shell
		final TaskShellHandle new_rep_handle = server.createReplicant(
				new_rep_info, this.task);
		// Update the replication policy information
		this.policy.addReplicant(rs, new_rep_info, new_rep_handle);
		// Publish the new policy information throughout the group
		this.policy.publishReplicationPolicy();
		// Resume execution within the replication group
		this.policy.resume();
		this.resume();
	}

	/**
	 * Deletes the replicant residing at the given location, provided it belongs
	 * to the replication group.<BR>
	 * This action includes informing the replication group of the deletion. <BR>
	 * This can involve suspending the whole replication group, thus justifying
	 * a synchronized policy.<BR>
	 * NB: It is assumed that only one replicant of a given task can exist on a
	 * given host.
	 *
	 * @param url
	 *            the location where the new replicant is to be created
	 * @param port_nb
	 *            the port corresponding to the server
	 * @throws RemoteException
	 * @throws UnknownReplicantException
	 *             if there is no replicant from the group at the given location
	 * @see DarxServer
	 * @see ReplicationStrategy
	 */
	@Override
	public synchronized void killReplicantAt(final String url, final int port_nb)
			throws RemoteException, UnknownReplicantException {
		// Retrieve the information concerning the doomed replicant
		final ReplicantInfo doomed_rep = this.policy.containsReplicant(url,
				port_nb);
		// Suspend the replication group
		this.suspend();
		this.policy.suspend();
		// Remove the replicant information from the policy
		this.policy.removeReplicant(doomed_rep);
		// Obtain the remote DarxServer stub corresponding to 'url' & 'port_nb'
		final DarxServer server = this.getRemoteServerHandle(url, port_nb);
		// Kill the replica on the remote server
		server.killReplicant(doomed_rep.getTaskName());
		// Publish the new policy information throughout the group
		this.policy.publishReplicationPolicy();
		// Resume execution within the replication group
		this.policy.resume();
		this.resume();
	}

	/**
	 * Ends the generic task execution. This method spreads the termination
	 * process throughout the replication group.
	 **/
	@Override
	public void killTask() throws RemoteException {
		System.out.println("\nTaskShell: killing task "
				+ this.info.getTaskName());
		this.policy.terminate();
		this.terminate();
		System.out.println(this.info.getTaskName()
				+ " successfully terminated\n");
	}

	// -----------------------------------//
	// ---- TASK EXECUTION MANAGEMENT ----//
	// -----------------------------------//

	@Override
	public synchronized void suspend() throws RemoteException {
		this.task.suspend();
	}

	@Override
	public synchronized void resume() throws RemoteException {
		this.task.resume();
	}

	@Override
	public void terminate() throws RemoteException {
		// Remove the reference to this replica on the local RMIRegistry
		final Registry local_reg = LocateRegistry.getRegistry(this.info
				.getURL(), this.info.getPortNb());
		try {
			local_reg.unbind(this.info.textifyDarxName());
		} catch (final NotBoundException e) {
			System.out.println("Internal error : replicant does not exist "
					+ this.info.textifyDarxPath());
			e.printStackTrace();
		}
		// Terminate the task execution
		this.terminateTask();
	}

	/**
	 * Terminates the task execution.<BR>
	 * This method does not remove the references to the encapsulation shell.
	 */
	@Override
	public void terminateTask() throws RemoteException {
		this.task.terminate();
		this.task = null;
	}
}
