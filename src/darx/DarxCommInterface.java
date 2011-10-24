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

/**
 * This is the communication interface, specific to an agent, used to send
 * messages to other agents.<BR>
 * <BR>
 * In practical terms, the leading <code>DarxTask</code> of a replication group
 * creates an instance of this class in order to communicate with the other
 * leaders via their corresponding <code>RemoteTask</code>.<BR>
 * <BR>
 * The <code>DarxCommInterface</code> is responsible for wrapping the contents
 * of the messages in instances of <code>DarxMessage</code>, as well as keeping
 * count of the sequence number of the sent messages and increasing this number
 * for each emission.<BR>
 * <BR>
 *
 * <I>NB: It might be a good idea to force this class to be a <B>transient</B>
 * attribute of the <code>DarxTask</code>. Only the leader of a replication
 * group would then be able to send messages.</I>
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class DarxCommInterface implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3566202643044159673L;

	/**
	 * The sequence number of the next message.
	 */
	private int msgNbr;

	/**
	 * The name of the owner of this instance; generally the name of the
	 * corresponding task.
	 */
	private final String name;

	/**
	 * Constructs a new instance for this class.
	 *
	 * @param name
	 *            the name of the owner of this instance; generally the name of
	 *            the corresponding task.
	 */
	public DarxCommInterface(final String name) {
		this.name = name;
		this.msgNbr = 0;
	}

	/**
	 * Sends an asynchronous message to an agent.
	 *
	 * @param rem
	 *            the remote reference to the corresponding replication group.
	 * @param body
	 *            the contents of the emission.
	 * @see RemoteTask#sendAsyncMessage(DarxMessage msg)
	 */
	public void sendAsyncMessage(final RemoteTask rem, final Serializable body) {
		rem.sendAsyncMessage(new DarxMessage(body, this.name, this.msgNbr++));
	}

	/**
	 * Sends a synchronous message to an agent.
	 *
	 * @param rem
	 *            the remote reference to the corresponding replication group.
	 * @param body
	 *            the contents of the emission.
	 * @see RemoteTask#sendSyncMessage(DarxMessage msg)
	 */
	public Object sendSyncMessage(final RemoteTask rem, final Serializable body) {
		return rem.sendSyncMessage(new DarxMessage(body, this.name,
				this.msgNbr++));
	}
}
