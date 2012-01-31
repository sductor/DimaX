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
import java.util.Enumeration;

/**
 * This is the replication group manager specialisation which implements the
 * passive replication strategy.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class PassiveReplicationStrategy extends ReplicationStrategy implements
Runnable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 45776507982592722L;

	/**
	 * The update delay, that is the time elapsed between backups.
	 */
	private int updateDelay;

	/**
	 * The thread used to update the replicas concurrently to the leader's
	 * execution.
	 */
	private transient Thread updater;

	/**
	 * The DARX internal handle for the replica which owns this instance.
	 */
	private transient TaskShellHandle owner_handle = null;

	/**
	 * The state of the thread (running, suspended, or dead).
	 */
	private int updater_state;

	/**
	 * The state of the thread as: running.
	 */
	private final int RUNNING = 0;

	/**
	 * The state of the thread as: suspended.
	 */
	private final int SUSPENDED = 1;

	/**
	 * The state of the thread as: dead.
	 */
	private final int DEAD = 2;

	// CONSTRUCTION

	/**
	 * Constructs a new instance of the passive replication strategy.
	 *
	 * @param ud
	 *            the delay between every backup.
	 */
	public PassiveReplicationStrategy(final int ud) {
		super();
		this.type = ReplicationStrategy.PASSIVE_STRATEGY;
		this.updateDelay = ud;
	}

	@Override
	public int compareTo(final ReplicationStrategy other_strat) {
		final int other_strat_type = other_strat.getReplicationType();
		int result = this.type - other_strat_type;
		if (result == 0)
			result = ((PassiveReplicationStrategy) other_strat)
			.getUpdateDelay()
			- this.updateDelay;
		return result;
	}

	// SELECTION & MODIFICATION

	/**
	 * Returns the update delay.
	 *
	 * @return the update delay.
	 */
	public int getUpdateDelay() {
		return this.updateDelay;
	}

	/**
	 * Sets the update delay.
	 *
	 * @param ud
	 *            the new update delay.
	 */
	public void setUpdateDelay(final int ud) {
		this.updateDelay = ud;
	}

	// BACKUP HANDLING

	/**
	 * Updates the replication group. Sends a fresh copy of the leading
	 * <code>DarxTask</code> to replace the one held in the
	 * <code>TaskShell</code> of the backups.
	 */
	protected void update() {
		System.out.println("Updating replicas...");
		if (this.owner_handle == null)
			this.owner_handle = this.getRemoteShellHandle(this.info);
		DarxTask task = null;
		TaskShellHandle handle = null;
		try {
			// Get the local task to be exported to the backups
			// I expect this call to provide a deep copy of the original
			// DarxTask
			// through serialization; please contact me if I'm wrong
			// NEED TO SUSPEND DARX_TASK_ENGINE HERE
			task = this.owner_handle.getTask();
			// NEED TO RESUME DARX_TASK_ENGINE HERE
			// To satisfy both needs, necessity calls for a
			// getConsistentTaskImage
			// that will remotely suspend, deep-copy and then resume
		} catch (final RemoteException re) {
			System.out.println("Error while updating replicas, couldn't access"
					+ " the replicated task's state: " + re);
		}
		for (final Enumeration e = this.replicants.elements(); e
				.hasMoreElements();)
			try {
				handle = (TaskShellHandle) e.nextElement();
				handle.setTask(task);
				handle.demoteFromGroupLeader();
			} catch (final RemoteException re) {
				this.handleUnreachableReplicant(this.info, re);
			}
		System.out.println(".............. update done");
	}

	// EXECUTION HANDLING

	/**
	 * Starts the backup process. This method will only be called if the owner
	 * of the strategy is a leader.
	 *
	 * @param ud
	 *            the update delay.
	 * @see #resume()
	 */
	public void activateUpdating(final int ud) {
		this.updateDelay = ud;
		if (this.updater == null) {
			this.updater = new Thread(this);
			this.updater.start();
		}
		this.updater_state = this.RUNNING;
	}

	@Override
	public void suspend() {
		System.out.println("<PassiveReplicationStrategy> Suspending strategy");
		// if (role == ReplicationStrategy.LEADER && updater != null)
		this.update();
		this.updater_state = this.SUSPENDED;
	}

	@Override
	public void resume() {
		// if (role == ReplicationStrategy.LEADER) {
		System.out.println("<PassiveReplicationStrategy> Resuming strategy");
		this.activateUpdating(this.updateDelay);
		// }
	}

	/**
	 * Executes the backup mechanism.
	 */
	@Override
	public void run() {
		while (this.updater_state != this.DEAD) {
			try {
				Thread.sleep(this.updateDelay);
			} catch (final InterruptedException e) {
			}
			if (this.updater_state == this.RUNNING)
				// && (role == ReplicationStrategy.LEADER))
				this.update();
		}
	}

	@Override
	public void stop() {
		this.updater_state = this.DEAD;
		this.updater = null;
	}

	@Override
	public void terminate() throws RemoteException {
		this.stop();
		super.terminate();
	}

	// COMMUNICATION HANDLING

	@Override
	public void deliverAsyncMessage(final TaskShell shell, final DarxMessage msg) {
		try {
			shell.processDeliverAsyncMessage(msg);
		} catch (final RemoteException e) {
		}
	}

	@Override
	public Serializable deliverSyncMessage(final TaskShell shell,
			final DarxMessage msg) {
		Serializable reply = null;
		try {
			reply = shell.processDeliverSyncMessage(msg);
		} catch (final RemoteException e) {
		}
		return reply;
	}

}
