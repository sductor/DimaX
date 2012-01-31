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
 * This is the thread launched every time a synchronous message delivery is
 * attempted. Its goal is to provide a single reply for a request forwarded to
 * all the active replicas within the replication group.
 */
class SyncMessageDeliver extends Thread {
	private final TaskShellHandle shell;
	private final DarxMessage msg;
	private Serializable reply;
	private RemoteException e;

	public SyncMessageDeliver(final TaskShellHandle shell, final DarxMessage msg) {
		this.shell = shell;
		this.msg = msg;
		this.e = null;
	}

	@Override
	public void run() {
		try {
			this.reply = this.shell.processDeliverSyncMessage(this.msg);
		} catch (final RemoteException e) {
			this.reply = null;
			this.e = e;
		}
	}

	public Serializable getReply() {
		return this.reply;
	}

	public RemoteException getException() {
		return this.e;
	}
}

/**
 * This is the replication group manager specialisation which implements the
 * active replication strategy.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class ActiveReplicationStrategy extends ReplicationStrategy implements
Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7326455434936577104L;

	public ActiveReplicationStrategy() {
		super();
		this.type = ReplicationStrategy.ACTIVE_STRATEGY;
	}

	public ActiveReplicationStrategy(final ActiveReplicationStrategy newstr) {
		super();
		this.info = newstr.getReplicantInfo();
		this.replicants = newstr.getReplicants();
		this.rep_number = newstr.getReplicationNumber();
	}

	@Override
	public int compareTo(final ReplicationStrategy other_strat) {
		final int other_strat_type = other_strat.getReplicationType();
		return this.type - other_strat_type;
	}

	@Override
	public void suspend() {
		for (final Enumeration en = this.replicants.elements(); en
				.hasMoreElements();)
			try {
				((TaskShellHandle) en.nextElement()).suspend();
			} catch (final RemoteException e) {
				this.handleUnreachableReplicant(this.info, e);
			}
	}

	@Override
	public void resume() {
		for (final Enumeration en = this.replicants.elements(); en
				.hasMoreElements();)
			try {
				((TaskShellHandle) en.nextElement()).resume();
			} catch (final RemoteException e) {
				this.handleUnreachableReplicant(this.info, e);
			}
	}

	@Override
	public void deliverAsyncMessage(final TaskShell shell, final DarxMessage msg) {
		try {
			shell.processDeliverAsyncMessage(msg);
		} catch (final Exception e) {
			System.out.println("Couldn't deliver asynchronous message #"
					+ msg.getSerial() + " from " + msg.getSenderName());
		}
		/*
		 * if(shell.acceptMsg(msg)) { Thread deliver=new
		 * MessageDeliverThread(msg, shell.task); deliver.start(); }
		 */
		for (final Enumeration en = this.replicants.elements(); en
				.hasMoreElements();)
			try {
				((TaskShellHandle) en.nextElement())
				.processDeliverAsyncMessage(msg);
			} catch (final RemoteException e) {
				this.handleUnreachableReplicant(this.info, e);
			}
	}

	@Override
	public Serializable deliverSyncMessage(final TaskShell shell,
			final DarxMessage msg) {
		final SyncMessageDeliver[] proc = new SyncMessageDeliver[this.replicants
		                                                         .size() + 1];
		proc[0] = new SyncMessageDeliver(shell, msg);
		proc[0].start();
		int k = 0;
		TaskShellHandle handle;
		for (final Enumeration en = this.replicants.elements(); en
				.hasMoreElements();) {
			handle = (TaskShellHandle) en.nextElement();
			proc[k + 1] = new SyncMessageDeliver(handle, msg);
			proc[k + 1].start();
			k++;
		}
		for (k = 0; k < proc.length; k++)
			try {
				proc[k].join();
				if (proc[k].getException() != null)
					this.handleUnreachableReplicant(this.info, proc[k]
							.getException());
			} catch (final InterruptedException e) {
			}
		return proc[0].getReply();
	}

}
