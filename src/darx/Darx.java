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
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;

/**
 * This is the server implementation which, as main objective, provides the
 * means for running DARX tasks on the local host.
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class Darx extends UnicastRemoteObject implements DarxServer {

	/**
	 *
	 */
	private static final long serialVersionUID = 2018459850444722446L;

	/**
	 * The stub providing access to the name server functionalities.
	 */
	static protected NameServer nserver = null;

	/**
	 * The URL of the name server.
	 */
	private static String ns_url;// = NameServer.DEFAULT_URL;

	/**
	 * The port number of the name server.
	 */
	private static int ns_port_nb = NameServer.DEFAULT_PORT_NB;

	/**
	 * The RMIregistry serving this specific location (<host>:<port_nb>).
	 */
	static protected Registry local_registry;

	/**
	 * The local server's URL
	 */
	private static String myURL = "";

	/**
	 * The local server's name
	 */
	static protected String myName;

	/**
	 * The local server's port number
	 */
	private static int myPortNb = 6789;

	/**
	 * The generic service name
	 */
	static public final String SERVICE_NAME = "DarxServer";

	// ------------------------------------//
	// ---- SERVER CREATION & ROUTINES ----//
	// ------------------------------------//

	/**
	 * Constructs a new DARX server instance.
	 */
	public Darx() throws RemoteException {
	}

	/**
	 * @param myURL
	 *            the myURL to set
	 */
	public static void setMyURL(final String myURL) {
		Darx.myURL = myURL;
	}

	/**
	 * @return the myURL
	 */
	public static String getMyURL() {
		return Darx.myURL;
	}

	/**
	 * @param myPortNb
	 *            the myPortNb to set
	 */
	public static void setMyPortNb(final int myPortNb) {
		Darx.myPortNb = myPortNb;
	}

	/**
	 * @return the myPortNb
	 */
	public static int getMyPortNb() {
		return Darx.myPortNb;
	}

	/**
	 * @param ns_url
	 *            the ns_url to set
	 */
	public static void setNs_url(final String ns_url) {
		Darx.ns_url = ns_url;
	}

	/**
	 * @return the ns_url
	 */
	public static String getNs_url() {
		return Darx.ns_url;
	}

	/**
	 * @param ns_port_nb
	 *            the ns_port_nb to set
	 */
	public static void setNs_port_nb(final int ns_port_nb) {
		Darx.ns_port_nb = ns_port_nb;
	}

	/**
	 * @return the ns_port_nb
	 */
	public static int getNs_port_nb() {
		return Darx.ns_port_nb;
	}

	/*
	 * Creation of the server and its running environment.
	 */
	static public void main(final String[] params) {
		Darx.analyseCommandLine(params);
		Darx.locateNamingService();
		try {
			final Darx darx = new Darx();
			Darx.darxRegistration(darx);
		} catch (final Exception e) {
			System.out.println("Error during initialisation : " + e);
			System.exit(0);
		}
	}

	public static void locateNamingService() {
		try {
			// Retrieve reference to the naming service
			final Registry nserver_reg = LocateRegistry.getRegistry(Darx
					.getNs_url(), Darx.getNs_port_nb());
			Darx.nserver = (NameServer) nserver_reg.lookup(Darx.getNs_url()
					+ ":" + Darx.getNs_port_nb() + "/NameServer");
			System.out.println("NameServer registry found at "
					+ Darx.getNs_url() + ":" + Darx.getNs_port_nb());
			// Retrieve URL
			Darx.setMyURL(InetAddress.getLocalHost().getHostName());
		} catch (final Exception e) {
			System.out.println("DARX server> Name Server URL : "
					+ Darx.getNs_url() + " " + Darx.getNs_port_nb());
			System.out.println("Error : cannot connect to nameserver : " + e);
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void darxRegistration(final Darx darx)
			throws RemoteException, AccessException {
		// Create new RMI registry on the location
		Darx.local_registry = LocateRegistry.createRegistry(Darx.getMyPortNb());
		// Register server
		Darx.myName = Darx.getMyURL() + ":" + Darx.getMyPortNb() + "/"
				+ Darx.SERVICE_NAME;
		Darx.local_registry.rebind(Darx.myName, darx);
		System.out.println("Darx server ready at " + Darx.getMyURL() + ":"
				+ Darx.getMyPortNb());
	}

	/**
	 * Analyses the command line in order to fill in specific runtime info: the
	 * URL and port number of the name server if the default value is not used,
	 * as well as the port number of the DARX server if the default value (6789)
	 * is not used.
	 *
	 * @param parameters
	 *            the String array containing the parameters, can be void if
	 *            default values are used and debug mode is off
	 */
	public static void analyseCommandLine(final String[] parameters) {
		final int l = parameters.length;
		int i = 0;
		while (i < l)
			if (parameters[i].compareTo("-ns") == 0) {
				i++;
				Darx.setNs_url(parameters[i]);
				NameServerImpl.URL = Darx.getNs_url();
				i++;
				Darx.setNs_port_nb(new Integer(parameters[i]).intValue());
				NameServerImpl.PORT_NB = Darx.getNs_port_nb();
				i++;
			} else if (parameters[i].compareTo("-p") == 0) {
				i++;
				Darx.setMyPortNb(new Integer(parameters[i]).intValue());
				i++;
			} else if (parameters[i].compareTo("-u") == 0) {
				i++;
				Darx.myName = parameters[i];
				i++;
			} else {
				System.out
				.println("Incorrect use of DARX server startup command:"
						+ "\n'./startdarx -ns <ns_url ns_port_number>] "
						+ "[-p <port_number>]'");
				final Collection<String> yo = Arrays.asList(parameters);
				System.out
				.println("received command :"+yo.toString());
				System.exit(0);
			}
	}

	// --------------------------------------//
	// ---- TASK RMI REGISTRY MANAGEMENT ----//
	// --------------------------------------//

	static private void rebindTask(final ReplicantInfo rep_info,
			final TaskShell shell) throws RemoteException {
		final Registry r = LocateRegistry.getRegistry(rep_info.getPortNb());
		r.rebind(rep_info.textifyDarxName(), shell);
	}

	static private void unbindTask(final ReplicantInfo rep_info)
			throws RemoteException {
		final Registry r = LocateRegistry.getRegistry(rep_info.getPortNb());
		try {
			System.out.println("Unbinding " + rep_info.textifyDarxName());
			r.unbind(rep_info.textifyDarxName());
		} catch (final NotBoundException e) {
			System.out.println("Task " + rep_info.textifyDarxName()
					+ " isn't bound by the naming service");
		}
	}

	// -----------------------------------//
	// ---- TASK EXECUTION MANAGEMENT ----//
	// -----------------------------------//

	/**
	 * Starts the execution of a new replication group leader on the current
	 * server.
	 *
	 * @param task
	 *            the <code>DarxTask</code> to be started on this server.
	 * @return the handle for the started task; to be used for user purposes,
	 *         such as (un)replication or policy modification.
	 */
	@Override
	public RemoteTask startTask(final DarxTask task) throws RemoteException {
		DarxHandle handle = null;
		final String generic_task_name = task.getTaskName();
		// System.out.println("Starting task " + generic_task_name);
		// Generate the info for the new replicant
		final ReplicantInfo new_rep_info = new ReplicantInfo(Darx.getMyURL(),
				Darx.getMyPortNb(), generic_task_name, 0);
		// Create the shell for the new replicant
		// & set it as the leader of its group
		final TaskShell shell = new TaskShell(task, new_rep_info);
		shell.setAsGroupLeader();
		try {
			// Register new task locally, then on name server
			Darx.rebindTask(new_rep_info, shell);
			Darx.nserver.register(new_rep_info);
			// Start task execution
			task.start();
			// Check task registration
			final String handle_name = new_rep_info.textifyDarxName();
			handle = (DarxHandle) Darx.local_registry.lookup(handle_name);
			handle = shell;
		} catch (final NotBoundException e) {
			System.out.println("Error : " + new_rep_info.textifyDarxName()
					+ " is not bound ?!?!");
			e.printStackTrace();
		}
		final RemoteTask remote = new RemoteTask(new_rep_info, handle);
		task.setHandle(remote);
		return remote;
	}

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
	@Override
	public TaskShellHandle createReplicant(final ReplicantInfo info,
			final DarxTask task) throws RemoteException {
		final String rep_name = info.textifyDarxName();
		System.out.println("Creating replicant '" + rep_name + "'");
		final TaskShell shell = new TaskShell(task, info);
		// Register the new replica on the naming service
		Darx.nserver.register(info);
		System.out.println(info.textifyDarxPath() + " created");
		// Register the new replica on the local RMI registry
		Darx.rebindTask(info, shell);
		System.out.println(rep_name + " registered locally\n");
		// Retrieve the remote reference to the new replica
		TaskShellHandle rep_handle = null;
		try {
			rep_handle = (TaskShellHandle) Darx.local_registry.lookup(rep_name);
		} catch (final NotBoundException e) {
			System.out.println("Can't find remote reference to " + rep_name
					+ " on the local RMI registry");
			e.printStackTrace();
		}
		return rep_handle;
	}

	/**
	 * Destroys the local replicant corresponding to the specified task. This
	 * includes unreferencing the doomed replicant on the local RMI server and
	 * on the name server.
	 *
	 * @param task_name
	 *            the name of the task to which the replicant belongs
	 */
	@Override
	public void killReplicant(final String task_name) throws RemoteException {
		System.out.println("Treating deletion request for task: " + task_name);
		ReplicantInfo rep_info = null;
		try {
			// Remove the doomed replicant info from the NameServer
			rep_info = Darx.nserver.unregister(task_name, Darx.getMyURL(), Darx
					.getMyPortNb());
			// Retrieve the local reference to the local replicant
			final String hn = rep_info.textifyDarxName();
			final TaskShellHandle shell = (TaskShellHandle) Darx.local_registry
					.lookup(hn);
			// Remove the reference from the local RMIRegistry
			Darx.unbindTask(rep_info);
			// Terminate the replicant execution
			shell.terminateTask();
		} catch (final NotBoundException e2) {
			System.out.println("No known replicant for task: " + task_name
					+ " on this server");
		}
		if (rep_info != null)
			System.out.println(rep_info.textifyDarxName()
					+ " successfully deleted\n");
	}

	// -----------------------//
	// ---- TASK SERVICES ----//
	// -----------------------//

	/**
	 * Generates a remote reference to the current leader of the task which
	 * corresponds to the given generic name. If the current leader cannot be
	 * contacted, it is assumed that the host has failed, and therefore a new
	 * leader election is launched through the <code>NameServer</code> services.
	 **/
	@Override
	public RemoteTask findTask(final String task_name) throws RemoteException {
		try {
			ReplicantInfo task_info = Darx.nserver.getLeaderOfTask(task_name);

			boolean next = false;
			TaskShellHandle ts_handle = null;
			// get the DARX server supporting the current task leader
			try {
				ts_handle = task_info.getTaskShellHandle();
			} catch (final Exception e) {
				next = true;
			}
			// This loop is entered if a non-DARX exception was caught
			while (next)
				try {
					task_info = Darx.nserver.selectAnotherLeader(task_info);
					ts_handle = task_info.getTaskShellHandle();
					ts_handle.setAsGroupLeader();
					next = false;
				} catch (final DarxException e) {
					throw e;
				} catch (final Exception e) {
					next = true;
				}
			return new RemoteTask(task_info, ts_handle);
		} catch (final DarxException e) {
			throw e;
		} catch (final RemoteException e) {
			System.out.println("Internal error : " + e);
		}
		return null;
	}
}
