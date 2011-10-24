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

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Hashtable;

/* import dimaxx.server.Logger; */

/**
 * This is the name server implementation which provides the means for locating
 * replication group leaders.
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 **/
public class NameServerImpl extends UnicastRemoteObject implements NameServer {

	/**
	 *
	 */
	private static final long serialVersionUID = -2348860089022055451L;

	/**
	 * The URL of the location of the centralized name server.
	 */
	static protected String URL = NameServer.DEFAULT_URL;

	/**
	 * The port number of the location of the centralized name server.
	 */
	static protected int PORT_NB = NameServer.DEFAULT_PORT_NB;

	/**
	 * The hashtable for storing the replication groups.<BR>
	 * The key is the generic task name.<BR>
	 * The accessed information is the corresponding ReplicationGroupInfo.
	 */
	private final Hashtable<String, ReplicationGroupInfo> tasks;

	/**
	 * The RMIRegistry to retrieve the NameServer handle.
	 */
	static private Registry local_reg;

	/**
	 * The debug functional mode of the server (off by default)
	 */
	static private boolean debug = false;

	/**
	 * Constructor...
	 */
	NameServerImpl() throws RemoteException {
		this.tasks = new Hashtable<String, ReplicationGroupInfo>();
	}

	/**
	 * Modifier for the URL of the name server.
	 */
	static void setURL(final String url) {
		NameServerImpl.URL = url;
	}

	/**
	 * Modifier for the PORT_NUMBER of the name server.
	 */
	static void setPortNumber(final String port_number) {
		// System.out.println("port number "+port_number);
		final Integer pn = new Integer(port_number);
		NameServerImpl.PORT_NB = pn.intValue();
	}

	/**
	 * Returns the information necessary for obtaining the handle of the current
	 * leader of the given replication group.
	 *
	 * @param task_name
	 *            the generic task name
	 * @return the info concerning the current replication group leader
	 */
	public synchronized ReplicantInfo getLeaderOfTask(final String task_name)
			throws RemoteException, NoMoreReplicantsException,
			InexistentNameException {
		// Check whether the task is known when getting its info
		final ReplicationGroupInfo rep_gp_info = this.tasks.get(task_name);
		if (rep_gp_info == null)
			throw new InexistentNameException(task_name);
		// Return the requested info
		return rep_gp_info.getLeaderInfo();
	}

	/**
	 * Sets a replicant as the new leader within its replication group info. NB:
	 * this method implicitly understands that the current leader is faulty; its
	 * information will consequently be removed from the group info.
	 *
	 * @param old_leader
	 *            the info defining the leader to be replaced
	 * @return the info concerning the new replication group leader
	 */
	public synchronized ReplicantInfo selectAnotherLeader(
			final ReplicantInfo old_leader) throws RemoteException,
			NoMoreReplicantsException, InexistentNameException {
		System.out.println("Selecting new leader for task "
				+ old_leader.getTaskName());
		// Check whether the task is known when getting its info
		final ReplicationGroupInfo rep_gp_info = this.tasks.get(old_leader
				.getTaskName());
		if (rep_gp_info == null)
			throw new InexistentNameException(old_leader.getTaskName());
		// Check that the given info corresponds to the current leader
		// If it is the case, remove previous leader & return the info
		// concerning
		// the new one; otherwise the change has already occured and the current
		// leader info should be returned
		ReplicantInfo current_leader = rep_gp_info.getLeaderInfo();
		if (current_leader.textifyDarxName().compareTo(
				old_leader.textifyDarxName()) == 0)
			current_leader = rep_gp_info.selectNewLeader();
		System.out.println("Old leader was " + old_leader.textifyDarxPath());
		System.out.println("New leader is " + current_leader.textifyDarxPath());
		// Remove previous leader & return the info concerning the new one
		// !!! Must warn new leader about its new role
		return current_leader;
	}

	/**
	 * Selects a replicant as the new leader within its replication group info.
	 * NB: no failure is assumed when using this method; the change in the
	 * leadership here is an explicit decision from the developer.
	 *
	 * @param rep_info
	 *            the info about the replicant to be set as the new leader
	 * @throws InexistentNameException
	 *             No replication group corresponds to the task the replicant
	 *             info claims to represent.
	 * @throws RemoteException
	 *             RMI general problem
	 * @throws UnknownReplicantException
	 *             The given replicant info hasn't been registered before.
	 */
	public void setLeaderOfTask(final ReplicantInfo rep_info)
			throws InexistentNameException, RemoteException,
			UnknownReplicantException {
		// Check whether the task is known when getting its info
		final ReplicationGroupInfo rep_gp_info = this.tasks.get(rep_info
				.getTaskName());
		if (rep_gp_info == null)
			throw new InexistentNameException(rep_info.getTaskName());
		// Change the leader ONLY if the new leader info is different from
		// the current one
		final ReplicantInfo current_leader_info = rep_gp_info.getLeaderInfo();
		if (current_leader_info.textifyDarxName() != rep_info.textifyDarxName())
			rep_gp_info.setLeader(rep_info);
		if (NameServerImpl.debug)
			System.out.println(rep_info.textifyDarxPath()
					+ "is the new leader of its group");
	}

	/**
	 * Selects a replicant as the new leader within its replication group info.
	 * NB: no failure is assumed when using this method; the change in the
	 * leadership here is an explicit decision from the developer.
	 *
	 * @param url
	 *            the URL of the replicant to be set as new leader
	 * @param port_nb
	 *            the port nb of the replicant to be set as new leader
	 * @param task_name
	 *            the generic name of the concerned task
	 * @return the info concerning the group whose hierarchy is altered.
	 * @throws InexistentNameException
	 *             No replication group corresponds to the task the replicant
	 *             info claims to represent.
	 * @throws RemoteException
	 *             RMI general problem
	 * @throws UnknownReplicantException
	 *             The given replicant info hasn't been registered before.
	 */
	public ReplicantInfo setLeaderOfTask(final String url, final int port_nb,
			final String task_name) throws InexistentNameException,
			RemoteException, UnknownReplicantException {
		// Check whether the task is known when getting its info
		final ReplicationGroupInfo rep_gp_info = this.tasks.get(task_name);
		if (rep_gp_info == null)
			throw new InexistentNameException(task_name);
		// Retrieve the information for the new leader
		final ReplicantInfo rep_info = rep_gp_info.getLocatedMemberInfo(url,
				port_nb);
		// Modify the group hierarchy with new leader
		rep_gp_info.setLeader(rep_info.getReplicantID());
		if (NameServerImpl.debug)
			System.out.println(rep_info.textifyDarxPath()
					+ "is the new leader of its group");
		return rep_info;
	}

	/**
	 * Registers the info concerning a new replicant for a given task. If no
	 * replicant was previously registered, that is if there is no entry
	 * corresponding to the given task_name, the replication group info is
	 * created.
	 *
	 * @param new_replicant
	 *            the replicant info to register
	 */
	public synchronized void register(final ReplicantInfo new_replicant)
			throws RemoteException {
		// System.out.println("Registering " + new_replicant.textifyDarxPath());
		Logger.fromDARX("NameServer : Registering "
				+ new_replicant.textifyDarxPath());
		final String task_name = new_replicant.getTaskName();
		// Check whether the task is known when getting its info
		ReplicationGroupInfo rep_gp_info = this.tasks.get(task_name);
		// If it's the first registration for this task,
		// create the group info,
		// set the given replicant as group leader,
		// and add the new group to the tasks
		if (rep_gp_info == null) {
			rep_gp_info = new ReplicationGroupInfo();
			rep_gp_info.setTaskName(task_name);
			rep_gp_info.addLeader(new_replicant);
			this.tasks.put(task_name, rep_gp_info);
		} else
			rep_gp_info.addMember(new_replicant);
		// System.out.println("Done...\n");
		if (NameServerImpl.debug)
			this.displayGroups();
	}

	/**
	 * Removes the info concerning a replicant for a given task.<BR>
	 * The killed replicant MUST NOT be a leader; killing a task is implemented
	 * differently (see method pointer)
	 *
	 * @param task_name
	 *            the name of the task to which the replicant belongs
	 * @param url
	 *            the url of the server hosting the doomed replica
	 * @param port_nb
	 *            the port nb of the server hosting the doomed replica
	 */
	public synchronized ReplicantInfo unregister(final String task_name,
			final String url, final int port_nb) throws RemoteException,
			UnknownReplicantException, InexistentNameException {
		// Check whether the task is known when getting its info
		final ReplicationGroupInfo rep_gp_info = this.tasks.get(task_name);
		if (rep_gp_info == null)
			throw new InexistentNameException(task_name);
		// Retrieve the searched replicant info
		final ReplicantInfo doomed_replicant = rep_gp_info
				.getLocatedMemberInfo(url, port_nb);
		// Remove the replicant info from the group info
		rep_gp_info.removeMember(doomed_replicant);
		System.out
				.println(doomed_replicant.textifyDarxPath() + " unregistered");
		if (NameServerImpl.debug)
			this.displayGroups();
		return doomed_replicant;
	}

	/**
	 * Removes the info concerning a whole replication group for a given task.
	 *
	 * @param task_name
	 *            the name of the task being stopped
	 * @return the replication group information for the task being stopped
	 */
	public synchronized ReplicationGroupInfo unregisterGroup(
			final String task_name) throws RemoteException,
			InexistentNameException {
		// Check whether the task is known when getting its info
		// Remove the searched replication group info if found
		final ReplicationGroupInfo rep_gp_info = this.tasks.remove(task_name);
		if (rep_gp_info == null)
			throw new InexistentNameException(task_name);
		if (NameServerImpl.debug)
			this.displayGroups();
		// Return the replication group info
		return rep_gp_info;
	}

	/**
	 * Displays the info about all replication groups handled by the server.
	 */
	private void displayGroups() {
		ReplicationGroupInfo rep_gp_info;
		System.out.println("\nRegistered groups: ");
		for (final Enumeration e = this.tasks.elements(); e.hasMoreElements();) {
			rep_gp_info = (ReplicationGroupInfo) e.nextElement();
			System.out.println("**** " + rep_gp_info.getTaskName());
			rep_gp_info.displayReplicants();
		}
		System.out.println("\n");
	}

	public static void main(final String[] p) {
		System.out.println("Starting Name Server...");
		NameServerImpl.analyseCommandLine(p);
		try {
			NameServerImpl.URL = InetAddress.getLocalHost().getHostName();
		} catch (final java.net.UnknownHostException e) {
			System.out.println("Host name cannot be obtained: " + e);
			e.printStackTrace();
			System.exit(0);
		}
		final String location = NameServerImpl.URL + ":"
				+ NameServerImpl.PORT_NB;
		try {
			// Create local RMIregistry on pre-defined port
			NameServerImpl.local_reg = LocateRegistry
					.createRegistry(NameServerImpl.PORT_NB);
			// Bind NameServer in local registry
			// Registry local_reg0 = //A RETIRER
			// LocateRegistry.getRegistry(location, PORT_NB);
			final String nserver_ref = location + "/NameServer";
			NameServerImpl.local_reg.rebind(nserver_ref, new NameServerImpl());
		} catch (final Exception e) {
			System.out.println("Name server cannot be initialized : " + e);
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Darx name server ready at: " + location);
		if (NameServerImpl.debug == true)
			System.out.println("Debug mode ON");
	}

	/**
	 * Analyses the command line in order to fill in specific runtime info: the
	 * port number of the name server if the default value is not used, and
	 * whether the name server is in debug (verbose) mode or not.
	 *
	 * @param params
	 *            the String array containing the parameters, can be void if
	 *            default values are used and debug mode is off
	 */
	static void analyseCommandLine(final String[] params) {
		final int l = params.length;
		int i = 0;
		while (i < l)
			if (params[i].compareTo("-debug") == 0) {
				NameServerImpl.debug = true;
				i++;
			} else if (params[i].compareTo("-p") == 0) {
				i++;
				NameServerImpl.setPortNumber(params[i]);
				i++;
			} else {
				System.out
						.println("Incorrect use of name server startup command:"
								+ "\n'./startnserv [-p <ns_port_number>] [-debug]'");
				System.exit(0);
			}
	}

}
