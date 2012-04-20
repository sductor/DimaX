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

/**
 * This is the container for the information observed locally for each DARX
 * server.
 * There should be one and only one instance of this object per DARX server.
 *
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */

package darx;

import java.rmi.RemoteException;
import java.util.Hashtable;

public class ObservationManager {

	/**
	 * the information concerning the criticity of each agent and its
	 * application. key: <String> the 'programmer-given' identifier of the agent
	 * accessed info: <ReplicationManager> the above defined information
	 */
	private final Hashtable agent_criticities;

	/**
	 * Constructs a new ObservationManager.
	 */
	public ObservationManager() {
		this.agent_criticities = null;
	}

	/**
	 * Adds a new agent to the list of those observed locally.
	 *
	 * @param task
	 *            the generic task, leader of its replication group, which is
	 *            being evaluated wrt the rest of the app
	 * @param criticity
	 *            the criticity of the agent
	 */
	public synchronized void registerAgent(final TaskShell shell,
			final double criticity) {
		try {
			shell.getTask().getTaskName();
		} catch (final RemoteException e) {
		}
		// ReplicationManager rm = new ReplicationManager(shell, criticity);
		// agent_criticities.put(agent_name, rm);
	}

	/**
	 * Removes an agent from the list of those observed locally.
	 *
	 * @param agent_name
	 *            the generic name of the task
	 */
	public synchronized void unregisterAgent(final String agent_name)
			throws InexistentNameException {
		final Object rs = this.agent_criticities.remove(agent_name);
		if (rs == null) {
			throw new InexistentNameException(agent_name);
		}
	}

	/**
	 * Updates the criticity fo a given agent.
	 *
	 * @param agent_name
	 *            the generic name of the task
	 * @param criticity
	 *            the newly computed criticity for this task
	 */
	public synchronized void setAgentCriticity(final String agent_name,
			final double criticity) throws RemoteException,
			InexistentNameException {
		// verifier que l'agent concerne est bien enregistre localement
		// ReplicationManager rm =
		// (ReplicationManager) agent_criticities.get(agent_name);
		// if (rm == null)
		// throw new InexistentNameException(agent_name);
		// //mettre a jour la criticite
		// rm.setCriticity(criticity);
	}

}
