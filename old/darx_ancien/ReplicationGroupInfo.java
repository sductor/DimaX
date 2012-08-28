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
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This object contains the information concerning a specific replication group
 * handled by DARX. It provides the leader of the replication group, as well as
 * the list of all the members.
 *
 *
 * @author Olivier Marin
 *
 * @version %I%, %G%
 **/
public class ReplicationGroupInfo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 305940710755226848L;

	/**
	 * the name of the application task represented by the replication group.
	 */
	private String task_name;

	/**
	 * the identification of the replication group leader. A value of -1 means
	 * that the group is empty.
	 */
	private int leader;

	/**
	 * the list of the replication group members. key: <int> replicant number
	 * accessed info: <ReplicantInfo> replicant global naming info
	 */
	private final Hashtable<Integer, ReplicantInfo> members;

	/**
	 * Constructs an empty information container.
	 */
	public ReplicationGroupInfo() {
		this.task_name = "";
		this.leader = -1;
		this.members = new Hashtable<Integer, ReplicantInfo>();
	}

	/**
	 * Constructs an information container.
	 */
	public ReplicationGroupInfo(final String task_id, final int leader_id,
			final Hashtable<Integer, ReplicantInfo> member_list) {
		this.task_name = task_id;
		this.leader = leader_id;
		this.members = member_list;
	}

	/**
	 * @return the name of the task represented by the group
	 */
	public String getTaskName() {
		return this.task_name;
	}

	/**
	 * Checks whether the given replicant info belongs to the group.
	 *
	 * @return true if it does belong to the group, false otherwise
	 */
	public boolean contains(final ReplicantInfo rep_info) {
		return this.members.contains(rep_info);
	}

	/**
	 * @return the info concerning the current leader of the group
	 */
	public ReplicantInfo getLeaderInfo() throws NoMoreReplicantsException {
		if (this.leader == -1)
			throw new NoMoreReplicantsException(this.task_name);
		else
			return this.members.get(new Integer(this.leader));
	}

	/**
	 * Returns information about a replica at a specific location.
	 *
	 * @return the info concerning a given replica within the group
	 */
	public ReplicantInfo getLocatedMemberInfo(final String url,
			final int port_nb) throws UnknownReplicantException {
		// Retrieve the list of info containers
		final Enumeration<ReplicantInfo> e = this.members.elements();
		// Search through the list
		boolean found = false;
		ReplicantInfo info = null;
		while (!found && e.hasMoreElements()) {
			info = e.nextElement();
			if (url.compareTo(info.getURL()) == 0
					&& info.getPortNb() == port_nb)
				found = true;
		}
		// If no corresponding replica was found
		if (found == false) {
			info = new ReplicantInfo(url, port_nb, this.task_name, -1);
			throw new UnknownReplicantException(info);
		} else
			return info;
	}

	public Enumeration<ReplicantInfo> getMembers() {
		return this.members.elements();
	}

	public int getSize() {
		return this.members.size();
	}

	// MODIFIERS

	public void setTaskName(final String new_task_name) {
		this.task_name = new_task_name;
	}

	public void setLeader(final int new_leader_id) {
		this.leader = new_leader_id;
	}

	public void setLeader(final ReplicantInfo new_leader)
			throws UnknownReplicantException {
		if (this.contains(new_leader))
			this.leader = new_leader.getReplicantID();
		else
			throw new UnknownReplicantException(new_leader);
	}

	// GROUP HANDLING

	public void addMember(final ReplicantInfo new_member) {
		final Integer id = new Integer(new_member.getReplicantID());
		if (this.members.containsKey(id))
			System.out.println(new_member.textifyDarxName()
					+ " already registered in rep group");
		else
			this.members.put(id, new_member);
		// System.out.println(new_member.textifyDarxName()
		// +" registered in rep group");
	}

	public void addLeader(final ReplicantInfo new_leader) {
		this.addMember(new_leader);
		this.leader = new_leader.getReplicantID();
	}

	/**
	 * Removes a member from the replication group information. Information on
	 * the leader cannot be removed unless it is the last replica in the group.
	 *
	 * @param doomed_member
	 *            the info regarding the removed replica
	 */
	public void removeMember(final ReplicantInfo doomed_member)
			throws IllegalLeaderRemovalException, NoMoreReplicantsException {
		final Integer doomed_id = new Integer(doomed_member.getReplicantID());
		if (this.leader == doomed_id.intValue() && this.members.size() != 1)
			throw new IllegalLeaderRemovalException(doomed_member);
		else if (this.leader == -1)
			throw new NoMoreReplicantsException(this.task_name);
		else {
			this.members.remove(doomed_id);
			if (this.members.size() == 0)
				this.leader = -1;
		}
	}

	/**
	 * Selects a new leader within the current replication group. BEWARE:
	 * calling this method implies that the current leader is considered faulty;
	 * the previous leader will automatically be removed from the replication
	 * group.
	 */
	public ReplicantInfo selectNewLeader() throws NoMoreReplicantsException {
		// If the group is empty or bound to be so
		if (this.leader == -1 || this.members.size() == 1) {
			// If the faulty replica is the last of its group
			if (this.leader != -1)
				this.members.remove(new Integer(this.leader));
			throw new NoMoreReplicantsException(this.task_name);
		}
		// Remove faulty leader
		this.members.remove(new Integer(this.leader));
		System.out.println("RGI: After leader removal, group size = "
				+ this.members.size());
		final Integer new_leader_id = this.members.keys().nextElement();
		System.out.println("RGI: old leader id = " + this.leader);
		this.leader = new_leader_id.intValue();
		System.out.println("RGI: new leader id = " + this.leader);
		// !!! Must warn new leader about its new role
		return this.members.get(new_leader_id);
	}

	/**
	 * Displays the DARX path of every replicant in the group.
	 */
	public void displayReplicants() {
		ReplicantInfo rep_info;
		for (final Enumeration<ReplicantInfo> e = this.members.elements(); e
				.hasMoreElements();) {
			rep_info = e.nextElement();
			System.out.println("\t" + rep_info.textifyDarxPath());
		}
	}

}
