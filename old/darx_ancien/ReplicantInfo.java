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
import java.util.StringTokenizer;

/**
 * This object contains the information regarding a specific replicant handled
 * by DARX. It is used as a global name for each DarxTask.
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 **/
public class ReplicantInfo implements Serializable, Comparable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3817180472058429385L;

	/**
	 * the URL of the current host for this replica
	 **/
	private String url;

	/**
	 * the port number of the current host for this replica
	 **/
	private int port_nb;

	/**
	 * the generic task name to which this replica is related
	 **/
	private String task_name;

	/**
	 * the identifier for this replica
	 **/
	private int id;

	/**
	 * constructs the information container of a task's leader, leaving the
	 * fields empty.
	 **/
	ReplicantInfo() {
		this.url = "";
		this.port_nb = -1;
		this.task_name = "";
		this.id = -1;
	}

	/**
	 * constructs the global identifier of a replica
	 **/
	ReplicantInfo(final String url, final int port_nb, final String task_name,
			final int id) {
		this.url = url;
		this.port_nb = port_nb;
		this.task_name = task_name;
		this.id = id;
	}

	// SELECTORS

	/** @return the URL of the current host for this replica */
	public String getURL() {
		return this.url;
	}

	/** @return the port number of the current host for this replica */
	public int getPortNb() {
		return this.port_nb;
	}

	/** @return the generic task name to which this replica is related */
	public String getTaskName() {
		return this.task_name;
	}

	/** @return the identifier for this replica */
	public int getReplicantID() {
		return this.id;
	}

	// MODIFIERS

	public void setURL(final String new_url) {
		this.url = new_url;
	}

	public void setPortNb(final int new_port_nb) {
		this.port_nb = new_port_nb;
	}

	public void setTaskName(final String new_task_name) {
		this.task_name = new_task_name;
	}

	public void setReplicantID(final int new_id) {
		this.id = new_id;
	}

	// FORMAT MANIPULATIONS

	/**
	 * parses the given DARX path name String (format: <URL>:<port nb>/<generic
	 * task name>$<replication nb>) in order to fill the fields of the current
	 * ReplicantInfo.
	 */
	void parse(final String info) {
		String temp;
		final StringTokenizer st1 = new StringTokenizer(info, ":");
		this.url = st1.nextToken();
		temp = st1.nextToken();
		final StringTokenizer st2 = new StringTokenizer(temp, "/");
		this.port_nb = new Integer(st2.nextToken()).intValue();
		temp = st2.nextToken();
		final StringTokenizer st3 = new StringTokenizer(temp, "$");
		this.task_name = st3.nextToken();
		this.id = new Integer(st3.nextToken()).intValue();
	}

	/**
	 * creates the unique DARX replicant name corresponding to the current
	 * ReplicantInfo.
	 */
	String textifyDarxName() {
		String temp = "";
		temp += this.task_name + "$" + this.id;
		return temp;
	}

	/**
	 * creates the unique DARX path corresponding to the current ReplicantInfo.
	 */
	String textifyDarxPath() {
		String temp = "";
		temp += this.url + ":" + this.port_nb + "/" + this.task_name + "$"
				+ this.id;
		return temp;
	}

	// ------------------------//
	// ------ COMPARISON ------//
	// ------------------------//

	/**
	 * Compares this replicant info to an object. This method must be defined in
	 * order to implement interface Comparable.
	 *
	 * @param o
	 *            the object (info) to be compared
	 * @return 0 if both infos correspond to the same replicant.
	 */
	public int compareTo(final Object o) {
		final ReplicantInfo ri = (ReplicantInfo) o;
		return this.textifyDarxPath().compareTo(ri.textifyDarxPath());
	}

	/**
	 * Compares the specified Object with this info for equality.
	 *
	 * @param o
	 *            the object to be compared (hopefully a
	 *            <code>ReplicationInfo</code>)
	 * @return true if the specified Object is equal to this info.
	 */
	@Override
	public boolean equals(final Object o) {
		return this.compareTo(o) == 0;
	}

	/**
	 * Who knows when THIS might come in handy...
	 */
	@Override
	public int hashCode() {
		return this.textifyDarxPath().hashCode();
	}

	/**
	 * Returns whether the given location is that of the current ReplicantInfo.
	 */
	public boolean isAt(final String l_url, final int l_port_nb) {
		return this.url.compareTo(l_url) == 0 && this.port_nb == l_port_nb;
	}

	/**
	 * Retrieves the remote reference to the given replica, ie. the
	 * <code>TaskShellHandle</code> corresponding to the replicant information.
	 */
	TaskShellHandle getTaskShellHandle() throws RemoteException {
		TaskShellHandle tsh = null;
		try {
			final Registry host_reg = LocateRegistry.getRegistry(this.url,
					this.port_nb);
			tsh = (TaskShellHandle) host_reg.lookup(this.textifyDarxName());
		} catch (final DarxException e) {
			System.out.println("Shell handle for replicant "
					+ this.textifyDarxPath() + " cannot be found");
			e.printStackTrace();
		} catch (final NotBoundException e) {
			System.out.println(Darx.getMyURL()+":"+Darx.getMyPortNb()+"RMI registration pb while fetching handle for "
					+ this.textifyDarxPath());
			e.printStackTrace();
		}
		return tsh;
	}

}
