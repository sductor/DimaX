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

/* import dimaxx.server.Logger; */

/**
 * This is the wrapper that represents an agent's replica at the DARX level. It
 * provides control over the replica's execution, thus DARX can:
 * <ul>
 * <li>start/suspend/resume a given replica,
 * <li>decide which message is significant, and when such a message must be
 * processed.
 * </ul>
 * <BR>
 *
 * IMPORTANT: Agents must extend this class in order to beneficiate from DARX's
 * functionalities .
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 *
 * @see TaskShell
 */
public class DarxTask implements Serializable, Cloneable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7507295570037030396L;

	/**
	 * the name of the generic task pursued by this replica.
	 **/
	private final String name;

	/**
	 * the status of this replica in the group.
	 **/
	protected boolean leader;

	/**
	 * The reference to the DARX server which hosts this replica. <BR>
	 * This attribute is set whilst activating the replica.
	 *
	 * @see DarxTask#activateTask()
	 **/
	protected DarxServer server = null;

	/**
	 * the handle to the latest agent corresponding to this DarxTask. It
	 * references a TaskShell currently running on a DARX server; namely the
	 * last one that was activated by this instance. The reason for the
	 * existence of this attribute is to prevent the destruction of the
	 * TaskShell by the GC before it is correctly registered.
	 *
	 * @see TaskShell
	 * @see Darx#startTask(DarxTask)
	 **/
	public RemoteTask handle = null;

	protected RemoteTask getHandle() {
		return this.handle;
	}

	/**
	 * Constructs a new agent replica from scratch.
	 *
	 * @param name
	 *            the name of the generic agent.
	 **/
	protected DarxTask(final String name) {
		this.name = name;
		this.leader = false;
	}

	/**
	 * Constructs a new agent replica from an existing replica.
	 **/
	protected DarxTask cloneTask() {
		DarxTask new_task = null;
		try {
			new_task = (DarxTask) super.clone();
		} catch (final CloneNotSupportedException e) {
			System.out.println("Couldn't clone task " + this.name);
		}
		new_task.leader = false;
		return new_task;
	}

	/**
	 * Returns the name of the generic task pursued by this replica.
	 *
	 * @return the name of the generic task pursued by this replica
	 **/
	public String getTaskName() {
		return this.name;
	}

	/**
	 * Sets the handle attribute (RemoteTask) of this task.
	 */
	public void setHandle(final RemoteTask remote) {
		this.handle = remote;
	}

	/**
	 * Starts the reception of an asynchronous message.
	 **/
	public synchronized void receiveAsyncMessage(final Object msg) {
		System.out.println("Received async message : " + msg.toString());
	}

	/**
	 * Starts the reception of a synchronous message.
	 *
	 * @return the serialized message containing the reply
	 **/
	public synchronized Serializable receiveSyncMessage(final Object msg) {
		System.out.println("Received sync message : " + msg.toString());
		return (Serializable) msg;
	}

	/**
	 * Requests a new task activation on a specific DARX server.
	 *
	 * @param url
	 *            the url of the chosen location (port nb not included)
	 * @param port_nb
	 *            the port number of the chosen server
	 * @return the remote reference to the activated, leading replica
	 */
	public RemoteTask activateTask(final String url, final int port_nb)
			throws RemoteException {
		final String server_path = url + ":" + Integer.toString(port_nb)
				+ "/DarxServer";
		try {
			// System.out.println("Getting registry at:"+url+":"+port_nb);
			final Registry server_reg = LocateRegistry
					.getRegistry(url, port_nb);
			// System.out.println("Getting server:"+ server_path);
			this.server = (DarxServer) server_reg.lookup(server_path);
			this.handle = this.server.startTask(this);
		} catch (final NotBoundException e) {
			System.out.println("Error : " + server_path + " is not bound ?!?!");
		}
		return this.handle;
	}

	/**
	 * Generates a remote reference to the current leader of the task which
	 * corresponds to the given generic name. If the current leader cannot be
	 * contacted, it is assumed that the host has failed, and therefore a new
	 * leader election is launched through the <code>NameServer</code> services.
	 *
	 * @see Darx#findTask(String task_name)
	 */
	public RemoteTask findTask(final String task_name) throws DarxException {
		RemoteTask rt = null;
		try {
			rt = this.server.findTask(task_name);
		} catch (final DarxException de) {
			de.printStackTrace();
		} catch (final RemoteException re) {
			System.out
					.println("DarxTask> Remote Exception while trying to find task "
							+ task_name);
			re.printStackTrace();
		}
		return rt;
	}

	/**
	 * starts the replica's execution
	 **/
	public void start() {
		// System.out.println("Starting "+name);
		Logger.fromDARX("The replica " + this.name + " is started!");
	}

	/**
	 * suspends the replica's execution
	 **/
	public void suspend() {
		System.out.println("Suspending " + this.name);
	}

	/**
	 * resumes the replica's execution
	 **/
	public void resume() {
		System.out.println("Resuming " + this.name);
	}

	/**
	 * Ends the execution of this specific replica.
	 **/
	public void terminate() {
		System.out.println("Terminating " + this.name);
		this.handle = null;
	}

	/**
	 * Ends the generic task execution. This method spreads the termination
	 * process throughout the replication group.<BR>
	 * It is to be used exclusively by the DARX programmer to end the life cycle
	 * of the represented agent if he does not possess the RemoteTask associated
	 * to the replication group of the current task instance.
	 **/
	public void terminateTask() {
		System.out.println("Terminating " + this.name);
		try {
			this.handle.killTask();
		} catch (final Exception e) {
			System.out.println("Exception raised: couldn't terminate: "
					+ this.name);
		}
		this.handle = null;
	}

}
