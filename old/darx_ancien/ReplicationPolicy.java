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
import java.util.Vector;

/**
 * This is the component, used alongside each <code>TaskShell</code>, which
 * defines a task's replication group.<BR>
 * This class provides methods to manage the information a specific task
 * possesses about its replication group in terms of replicant (un)registration,
 * that is it maintains up to date the number of replicants for this particular
 * task, the location of each replicant, the strategies applied within the
 * group, and to which replicants they are applied.<BR>
 * <BR>
 * NB: EVERY replicant, independently of its being leader of the replication
 * group or not, carries and maintains this information up to date so as to be
 * available to take the leadership in case of the current leader's failure. <BR>
 *
 * A replication policy must contain a list of strategies, a list of replicas,
 * and a mapping between both lists.
 *
 *
 * @author Olivier Marin
 *
 * @version %I%, %G%
 *
 * @see ReplicationStrategy
 * @see ReplicationManager
 */
public class ReplicationPolicy implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2188765528451408970L;

	/**
	 * The name of the task represented by the replication group.
	 */
	private String task_name = "";

	/**
	 * The info concerning the leader of the replication group.
	 */
	private ReplicantInfo leader_info = null;

	/**
	 * The unique number used in the darx-pathname of the latest created
	 * replicant.
	 */
	private int replication_number = 0;

	/**
	 * The information concerning the members of the replication group; it is a
	 * list of replicant infos ordered by decreasing degree of consistency.<BR>
	 * Therefore it contains at least one element: the leader, of index value 0.
	 *
	 * @see ReplicantInfo
	 */
	private Vector<ReplicantInfo> members = null;

	/**
	 * The unordered table of strategies applied in this policy. Reminder: every
	 * strategy possesses the list of group members <BR>
	 * (<CODE>Hashtable: ReplicantInfo <=> TaskShellHandle</CODE>)<BR>
	 * to which it is applied.
	 *
	 * @see ReplicationStrategy
	 */
	private Vector<ReplicationStrategy> strategies = null;

	/**
	 * Constructs a new, empty replication policy
	 *
	 * @param leader_info
	 *            the info for the leader of the replication group
	 */
	ReplicationPolicy(final ReplicantInfo leader_info) {
		this.strategies = new Vector<ReplicationStrategy>();
		this.members = new Vector<ReplicantInfo>();
		this.task_name = leader_info.getTaskName();
		this.leader_info = leader_info;
		this.members.addElement(leader_info);
	}

	/**
	 * Adds a strategy to the policy. <BR>
	 * If an equivalent strategy is already present, then the policy remains
	 * unchanged.
	 *
	 * @param rep_strat
	 *            the strategy to add
	 * @return the reference to the strategy applied in the policy
	 */
	protected ReplicationStrategy addStrategy(
			final ReplicationStrategy rep_strat) {
		ReplicationStrategy rs = this.containsStrategy(rep_strat);
		if (rs == null) {
			this.strategies.add(rep_strat);
			rep_strat.setLeaderInfo(this.leader_info);
			rs = rep_strat;
		}
		return rs;
	}

	/**
	 * Checks whether a strategy is applied within the policy.
	 *
	 * @return the reference to the given strategy, null otherwise.
	 */
	private ReplicationStrategy containsStrategy(final ReplicationStrategy rs) {
		ReplicationStrategy reps = null;
		final int index = this.strategies.indexOf(rs);
		if (index != -1)
			reps = this.strategies.elementAt(index);
		return reps;
	}

	/**
	 * Removes a strategy from the policy. Additionaly, this method terminates
	 * the removed strategy. NB: it is assumed that the whole policy is
	 * suspended. NB2: it is also assumed that the strategy is not currently
	 * applied to any replica.
	 *
	 * @param rep_strat
	 *            the strategy to remove
	 */
	protected void removeStrategy(final ReplicationStrategy rep_strat) {
		final ReplicationStrategy rs = this.containsStrategy(rep_strat);
		if (rs != null) {
			// System.out.println("Removing strategy " +
			// rs.getReplicationType());
			// members.removeAll(rep_strat.getReplicants().keySet());
			this.strategies.remove(rs);
			rs.stop();
			// try {
			// rs.terminate();
			// } catch (RemoteException e) {
			// System.out.println("Trouble while removing strategy "
			// + rep_strat.getReplicationType()
			// + " from policy");
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * Finds the strategy which is applied to a given replica. It is mainly used
	 * to modify the strategy accordingly to a change of status for the replica
	 * (strategy switching, replica removal.)
	 *
	 * @param rep_info
	 *            the replica to which the strategy is applied
	 * @return the strategy applied to the replica
	 */
	protected ReplicationStrategy findAppliedStrategy(
			final ReplicantInfo rep_info) throws UnknownReplicantException {
		ReplicationStrategy result = null;
		int i = 0;
		boolean found = false;
		while (!found && i < this.strategies.size()) {
			result = this.strategies.elementAt(i);
			found = result.contains(rep_info);
			i++;
		}
		if (!found)
			throw new UnknownReplicantException(rep_info);
		return result;
	}

	/**
	 * Sets the strategy to be applied to a replica at a given location. The
	 * location is assumed to host a replica, otherwise an exception is thrown.
	 * If the strategy isn't already applied in the group, then it is added to
	 * the policy.
	 *
	 * @param rep_strat
	 *            the strategy to apply
	 * @param rep_info
	 *            the replica to which the strategy is applied
	 */
	protected void switchAppliedStrategy(final ReplicationStrategy rep_strat,
			final ReplicantInfo rep_info) throws UnknownReplicantException {
		// Remove reference to the replica
		final TaskShellHandle handle = this.removeReplicant(rep_info);
		// Add reference to the replica in its new strategy
		this.addReplicant(rep_strat, rep_info, handle);
	}

	/**
	 * Adds a replica to the policy. If a strategy equivalent to the one given
	 * in parameter is not applied yet, then it is added also. NB: it is assumed
	 * that the whole policy is suspended.
	 *
	 * @param rep_strat
	 *            the strategy to apply
	 * @param rep_info
	 *            the info concerning the replica to be added
	 * @param rep_handle
	 *            the remote reference to the added replica
	 */
	protected void addReplicant(final ReplicationStrategy rep_strat,
			final ReplicantInfo rep_info, final TaskShellHandle rep_handle) {
		this.members.addElement(rep_info);
		// !!!! Sort 'members' by decreasing DOC
		final ReplicationStrategy rs = this.addStrategy(rep_strat);
		rs.registerReplicant(rep_info, rep_handle);
	}

	/**
	 * Generates an information object for a new group member.
	 *
	 * @param url
	 *            the url of the location for the new member
	 * @param port_nb
	 *            the port nb of the location for the new member
	 * @return the information object for the new member
	 */
	protected ReplicantInfo generateNewInfo(final String url, final int port_nb) {
		this.replication_number++;
		return new ReplicantInfo(url, port_nb, this.task_name,
				this.replication_number);
	}

	/**
	 * Checks whether there exists a replica at a given location.
	 *
	 * @param url
	 *            the URL of the checked location
	 * @param port_nb
	 *            the port number of the checked location
	 * @return the reference to the replicant information.
	 * @throws UnknownReplicantException
	 *             if there is no replica at the given location
	 */
	protected ReplicantInfo containsReplicant(final String url,
			final int port_nb) throws UnknownReplicantException {
		ReplicantInfo ri = null;
		boolean found = false;
		int i = 0;
		while (!found && i < this.members.size()) {
			ri = this.members.elementAt(i);
			found = ri.isAt(url, port_nb);
			i++;
		}
		if (!found) {
			ri = new ReplicantInfo(url, port_nb, this.task_name, -1);
			throw new UnknownReplicantException(ri);
		}
		return ri;
	}

	/**
	 * Removes a replica from the policy. If the corresponding strategy has no
	 * replica left to be applied to, then it is removed also. NB: it is assumed
	 * that the whole policy is suspended.
	 *
	 * @param rep_info
	 *            the replica to remove
	 * @return the remote reference to the removed replica (used by
	 *         <CODE>setStrategy</CODE>)
	 */
	protected TaskShellHandle removeReplicant(final ReplicantInfo rep_info)
			throws UnknownReplicantException {
		ReplicationStrategy rep_strat = null;
		TaskShellHandle handle = null;
		this.members.remove(rep_info);
		rep_strat = this.findAppliedStrategy(rep_info);
		handle = rep_strat.unregisterReplicant(rep_info);
		if (rep_strat.getReplicants().size() == 0)
			this.removeStrategy(rep_strat);
		return handle;
	}

	/**
	 * Spreads the suspension of the encapsulated task execution throughout the
	 * replication group. This method is used by the leader of the replication
	 * group only.
	 */
	protected void suspend() {
		System.out.println("Suspending policy");
		for (int i = 0; i < this.strategies.size(); i++)
			(this.strategies.elementAt(i)).suspend();
	}

	/**
	 * Spreads the resumption of the encapsulated task execution throughout the
	 * replication group. Additionally this method publishes the replication
	 * policy, that is the policy of every replica is updated. This method is
	 * used by the leader of the replication group only.
	 */
	protected void resume() {
		System.out.println("Resuming policy");
		this.publishReplicationPolicy();
		for (int i = 0; i < this.strategies.size(); i++)
			(this.strategies.elementAt(i)).resume();
	}

	/**
	 * Terminates the activity of the entire replication group. This method is
	 * used by the leader of the replication group only.
	 */
	protected void terminate() {
		for (int i = 0; i < this.strategies.size(); i++)
			try {
				(this.strategies.elementAt(i)).terminate();
			} catch (final RemoteException e) {
				System.out.println("Trouble while terminating strategy");
				e.printStackTrace();
			}
	}

	/**
	 * Spreads the information concerning the addition of a new member
	 * throughout the replication group. This operation includes updating the
	 * view that the new member possesses on the replication group.
	 *
	 * @param rep_info
	 *            the info concerning the new member
	 */
	public void publishReplicationPolicy() {
		// Get the reference to every replica in the group
		final Vector<TaskShellHandle> handles = new Vector<TaskShellHandle>();
		int i;
		ReplicationStrategy rs = null;
		TaskShellHandle tsh = null;
		for (i = 0; i < this.strategies.size(); i++) {
			rs = this.strategies.elementAt(i);
			handles.addAll(rs.getReplicants().values());
		}
		// Send the new policy to every replica in the group
		try {
			for (i = 0; i < handles.size(); i++) {
				tsh = handles.elementAt(i);
				tsh.setPolicy(this);
			}
		} catch (final Exception re) {
			System.out.println("Remote exception while publishing the "
					+ "replication policy for " + this.task_name);
			re.printStackTrace();
		}
	}

	public void deliverAsyncMessage(final TaskShell shell, final DarxMessage msg)
			throws RemoteException {
		shell.processDeliverAsyncMessage(msg);
		ReplicationStrategy rs;
		for (int i = 0; i < this.strategies.size(); i++) {
			rs = this.strategies.elementAt(i);
			rs.deliverAsyncMessage(shell, msg);
		}
	}

	public Serializable deliverSyncMessage(final TaskShell shell,
			final DarxMessage msg) throws RemoteException {
		shell.processDeliverSyncMessage(msg);
		// !!!!!Must forward to active replicas and wait for all replies
		// Get the lists of all active replicas from all strategies
		// Append the lists and add leader
		// Make synchronized delivery such as the one in the active strategy
		// !!!!This does it for the time being
		ReplicationStrategy rs;
		Serializable reply = null;
		for (int i = 0; i < this.strategies.size(); i++) {
			rs = this.strategies.elementAt(i);
			reply = rs.deliverSyncMessage(shell, msg);
		}
		return reply;
	}

	void display() {
		int i;
		System.out.println("\nPolicy composition:\nMembers");
		for (i = 0; i < this.members.size(); i++)
			System.out.println((this.members.elementAt(i)).textifyDarxPath());
		for (i = 0; i < this.strategies.size(); i++)
			(this.strategies.elementAt(i)).display();
		System.out.println("\n\n");
	}

}
