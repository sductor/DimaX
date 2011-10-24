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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This is the component, used alongside each <code>TaskShell</code>, which
 * defines a task's replication group.<BR>
 * This class provides methods to manage the information a specific task
 * possesses about its replication group in terms of replicant (un)registration,
 * that is it maintains up to date the number of replicants for this particular
 * task and the location of each replicant.<BR>
 * <BR>
 * NB: EVERY replicant, independently of its being leader of the replication
 * group or not, carries and maintains this information up to date so as to be
 * available to take the leadership in case of the current leader's failure.<BR>
 * <BR>
 * NB2: This class is abstract; its specialisations define the current
 * replication policy within the task's group, and therefore the message passing
 * and runtime management schemes.
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 *
 * @see ActiveReplicationStrategy
 * @see PassiveReplicationStrategy
 */
public abstract class ReplicationStrategy implements Serializable, Comparable {

	/**
	 *
	 */
	private static final long serialVersionUID = -9092610925375365001L;
	/*
	 * The constants used to define the strategy type. The values below
	 * correspond to my (Olivier Marin) idea of how well the given strategy
	 * guarantees consistency. Those values should be modified, or even entirely
	 * redefined by the user to suit her/his needs.
	 */
	/**
	 * The constant used to define the strategy type as empty. This is used as a
	 * comparative element, e.g.for the hashcode calculation.
	 */
	public final static int EMPTY_STRATEGY = 0;
	/**
	 * The constant used to define the strategy type as active.
	 */
	public final static int ACTIVE_STRATEGY = 9;
	/**
	 * The constant used to define the strategy type as passive.
	 */
	public final static int PASSIVE_STRATEGY = 6;
	/**
	 * The constant used to define the strategy type as quorum-based.
	 */
	public final static int QUORUM_STRATEGY = 3;

	/*
	 * The constants used to define the role of the strategy owner. Presently,
	 * the role can either be leader or backup.
	 */
	/**
	 * The constant used to define the role of the strategy owner as leader.
	 */
	public final static int LEADER = 0;
	/**
	 * The constant used to define the role of the strategy owner as backup.
	 */
	public final static int BACKUP = 1;

	/**
	 * The strategy type, that is whether it is active or passive.
	 */
	protected int type;

	/**
	 * The role of the strategy owner (leader, active or passive)
	 */
	protected int role;

	/**
	 * The information concerning the replication group leader
	 */
	protected ReplicantInfo info;

	/**
	 * The list of remote replicants participating to the current replication
	 * group, the owner of this instance not included. key: replicant info
	 * value: remote replicant handle
	 */
	protected Hashtable<ReplicantInfo, TaskShellHandle> replicants;

	/**
	 * The unique number used in the darx-pathname of the latest created
	 * replicant.
	 */
	protected int rep_number;

	// ---------------------//
	// ---- CONSTRUCTOR ----//
	// ---------------------//

	/**
	 * Constructs an empty information container to be filled in with data about
	 * the task's replication group. This is the unique class constructor. As
	 * this class is abstract, it is only provided here to be extended in
	 * specialisation classes.
	 */
	protected ReplicationStrategy() {
		this.info = null;
		this.role = ReplicationStrategy.BACKUP;
		this.replicants = new Hashtable<ReplicantInfo, TaskShellHandle>();
		this.rep_number = 0;
	}

	// -----------------------//
	// ---- SERIALIZATION ----//
	// -----------------------//

	/**
	 * Controles the deserialization of this object, during the replication
	 */
	private void readObject(final java.io.ObjectInputStream s)
			throws java.io.IOException, java.lang.ClassNotFoundException {
		s.defaultReadObject();
		// shell = null;
	}

	// -------------------//
	// ---- SELECTORS ----//
	// -------------------//

	/**
	 * @return the replication type (active or passive)
	 */
	public int getReplicationType() {
		return this.type;
	}

	/**
	 * @return the role of the replication strategy owner
	 */
	public int getReplicantRole() {
		return this.role;
	}

	/**
	 * @return the info about the strategy owner
	 */
	public ReplicantInfo getReplicantInfo() {
		return this.info;
	}

	/**
	 * @return the ID number of the next replica to be created
	 */
	public int getReplicationNumber() {
		return this.rep_number;
	}

	/**
	 * @return the list of replicants participating to the replication group
	 */
	public Hashtable<ReplicantInfo, TaskShellHandle> getReplicants() {
		return this.replicants;
	}

	/**
	 * @param rep_info
	 *            the replica looked for
	 * @return whether this strategy instance is applied to the given replica
	 */
	public boolean contains(final ReplicantInfo rep_info) {
		return this.replicants.containsKey(rep_info);
	}

	/**
	 * Finds the info concerning a replicant at a given location
	 *
	 * @param url
	 *            the url of server hosting the replicant to look for
	 * @param port_nb
	 *            the port nb of server hosting the replicant to look for
	 * @return the stub of the replicant at the given location
	 */
	public ReplicantInfo findReplicantAt(final String url, final int port_nb)
			throws UnknownReplicantException {
		ReplicantInfo rep_info = null;
		boolean found = false;
		final Enumeration e = this.replicants.keys();
		while (e.hasMoreElements() && !found) {
			rep_info = (ReplicantInfo) e.nextElement();
			if (url.compareTo(rep_info.getURL()) == 0
					&& rep_info.getPortNb() == port_nb)
				found = true;
		}
		if (!found) {
			rep_info = new ReplicantInfo(url, port_nb, this.info.getTaskName(),
					-1);
			System.out.println("ReplicationStrategy: couldn't find "
					+ rep_info.textifyDarxPath());
			throw new UnknownReplicantException(rep_info);
		} else
			return rep_info;
	}

	/**
	 * Gets the handle for a remote replicant within the group.
	 *
	 * @return the handle for the given replicant
	 */
	public TaskShellHandle getReplicantHandle(final ReplicantInfo rep_info)
			throws UnknownReplicantException {
		final TaskShellHandle handle = this.replicants.get(rep_info);
		if (handle == null)
			throw new UnknownReplicantException(rep_info);
		else
			return handle;
	}

	// -------------------//
	// ---- MODIFIERS ----//
	// -------------------//

	/**
	 * Sets the type of the strategy (active or passive).
	 */
	public void setReplicantType(final int type) {
		this.type = type;
	}

	/**
	 * Sets the role of the strategy owner (leader or backup).
	 */
	public void setReplicantRole(final int new_role) {
		this.role = new_role;
	}

	/**
	 * Sets the info about the replication group leader
	 */
	public void setLeaderInfo(final ReplicantInfo new_info) {
		this.info = new_info;
	}

	/**
	 * Sets the list of tasks belonging to this replication group. The task
	 * which corresponds to this manager is not included in this list.
	 */
	public void setReplicants(
			final Hashtable<ReplicantInfo, TaskShellHandle> new_replicants_list) {
		this.replicants = new_replicants_list;
	}

	/**
	 * Sets the serial number of the last created replicant.
	 */
	public void setReplicationNumber(final int new_rep_number) {
		this.rep_number = new_rep_number;
	}

	// ------------------------//
	// ------ COMPARISON ------//
	// ------------------------//

	/**
	 * Compares this replication strategy to another.
	 *
	 * @param other_strategy
	 *            the strategy to be compared
	 * @return 0 if both strategies are equivalent, a positive integer if this
	 *         strategy is more pessimistic than the compared strategy, a
	 *         negative integer otherwise.
	 */
	public abstract int compareTo(ReplicationStrategy other_strategy);

	/**
	 * Compares this replication strategy to an object. This method must be
	 * defined in order to implement interface Comparable.
	 *
	 * @param o
	 *            the object (strategy) to be compared
	 * @return 0 if both strategies are equivalent, a positive integer if this
	 *         strategy is more pessimistic than the compared strategy, a
	 *         negative integer otherwise.
	 */
	@Override
	public int compareTo(final Object o) {
		return this.compareTo((ReplicationStrategy) o);
	}

	/**
	 * Compares the specified Object with this strategy for equality.
	 *
	 * @param o
	 *            the object to be compared (hopefully a
	 *            <code>ReplicationStrategy</code>)
	 * @return true if the specified Object is equal to this strategy.
	 */
	@Override
	public boolean equals(final Object o) {
		return this.compareTo((ReplicationStrategy) o) == 0;
	}

	/**
	 * Who knows when THIS might come in handy...
	 */
	@Override
	public int hashCode() {
		return this.type;
	}

	// --------------------------------------------------//
	// ---- REPLICATION GROUP INFORMATION MANAGEMENT ----//
	// --------------------------------------------------//

	/**
	 * Adds a member to the replication group.
	 *
	 * @param rep
	 *            the info concerning the new member
	 * @param sh
	 *            the shell handle for the new member
	 */
	public void registerReplicant(final ReplicantInfo rep,
			final TaskShellHandle sh) {
		this.replicants.put(rep, sh);
		this.rep_number++;
	}

	/**
	 * Removes a member from the replication group.
	 *
	 * @param doomed_rep_info
	 *            the info concerning the removed member
	 * @return the handle to which the info had been mapped, null otherwise.
	 */
	public TaskShellHandle unregisterReplicant(
			final ReplicantInfo doomed_rep_info) {
		return this.replicants.remove(doomed_rep_info);
	}

	/**
	 * Spreads the suspension of the encapsulated task execution throughout the
	 * replication group. This method is used by the leader of the replication
	 * group only.
	 */
	public abstract void suspend();

	/**
	 * Spreads the resumption of the encapsulated task execution throughout the
	 * replication group. This method is used by the leader of the replication
	 * group only.
	 */
	public abstract void resume();

	/**
	 * Stops the activity regarding consistency maintenance within the
	 * replication group (e.g. updater thread in the passive strategy.) This
	 * method is used by the leader of the replication group only.
	 */
	public void stop() {
	}

	/**
	 * Spreads the termination of the task execution throughout the replication
	 * group. This method is used by the leader of the replication group only.
	 */
	public void terminate() throws RemoteException {
		// Remove information about this task on the NameServer
		NameServer nserver = null;
		final Registry name_server_reg = LocateRegistry.getRegistry(
				NameServerImpl.URL, NameServerImpl.PORT_NB);
		try {
			final String ns_addr = NameServerImpl.URL + ":"
					+ NameServerImpl.PORT_NB + "/" + NameServer.SERVICE_NAME;
			nserver = (NameServer) name_server_reg.lookup(ns_addr);
		} catch (final java.rmi.NotBoundException e) {
			System.out.println("Couldn't retrieve NameServer reference "
					+ "during task termination");
		}
		try {
			nserver.unregisterGroup(this.info.getTaskName());
		} catch (final InexistentNameException e2) {
			e2.printStackTrace();
		}
		// Send termination order to all replicants
		for (final Enumeration reps = this.replicants.elements(); reps
				.hasMoreElements();)
			((TaskShellHandle) reps.nextElement()).terminate();
	}

	/**
	 * Spreads the delivery of an asynchronous message throughout the
	 * replication group. This method is used by the leader of the replication
	 * group only.
	 *
	 * @param shell
	 *            the shell corresponding to the present strategy (used to
	 *            process the message locally)
	 * @param msg
	 *            the message to deliver
	 */
	public abstract void deliverAsyncMessage(TaskShell shell, DarxMessage msg);

	/**
	 * Spreads the delivery of a synchronous message throughout the replication
	 * group. This method is used by the leader of the replication group only.
	 *
	 * @param shell
	 *            the shell corresponding to the present strategy (used to
	 *            process the message locally)
	 * @param msg
	 *            the message to deliver
	 * @return the reply to be sent back
	 */
	public abstract Serializable deliverSyncMessage(TaskShell shell,
			DarxMessage msg);

	/**
	 * Handles situations where a replicant fails to be contacted. In this
	 * version of DARX, the replicant is considered dead and is thus removed
	 * from the replication group.
	 *
	 * @param dead
	 *            the information concerning the replicant which fails to
	 *            respond
	 * @param re
	 *            the exception resulting from the failure
	 */
	public void handleUnreachableReplicant(final ReplicantInfo dead,
			final RemoteException re) {
		System.out.println("Current Time = " + System.currentTimeMillis());
		System.out.println("Max size : " + this.replicants.size()
				+ "Replicant : " + dead);
		System.out.println("Failure detected : replicant not responding");
		this.replicants.remove(dead);
	}

	/**
	 * Retrieves the stub of a given replicant.
	 *
	 * @param rep_info
	 *            the information concerning the replicant whose stub is being
	 *            retrieved
	 * @return the stub of the remote shell for the given replicant
	 */
	protected TaskShellHandle getRemoteShellHandle(final ReplicantInfo rep_info) {
		TaskShellHandle shell = null;
		try {
			final Registry server_reg = LocateRegistry.getRegistry(rep_info
					.getURL(), rep_info.getPortNb());
			shell = (TaskShellHandle) server_reg.lookup(rep_info
					.textifyDarxName());
		} catch (final RemoteException re) {
			System.out.println("Remote exception while looking for replicant "
					+ rep_info.textifyDarxName());
			re.printStackTrace();
		} catch (final java.rmi.NotBoundException ue) {
			System.out.println(rep_info.textifyDarxName() + "not bound");
		}
		return shell;
	}

	void display() {
		System.out.println("....Strat type: " + this.getReplicationType());
		ReplicantInfo ri = null;
		final Enumeration en = this.replicants.keys();
		while (en.hasMoreElements()) {
			ri = (ReplicantInfo) en.nextElement();
			System.out.println("........" + ri.textifyDarxPath());
		}
	}

}
