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
 * This is the encapsulator provided for communications between replication
 * groups.<BR>
 * The supported agents are considered to communicate between themselves through
 * messages. In order to propagate this information to the replicas, every
 * transmitted message is encapsulated in this object. Thus it is possible to
 * handle the problems associated to replication: mistaken duplication,
 * disordering, ...
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class DarxMessage implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2457980808367255373L;

	/**
	 * The body of the message. NB: The objects associated to messages in the
	 * supported MASs have to implement <code>Serializable</code>
	 */
	private final Serializable msg;

	/**
	 * The identity of the sender. It is obtained through the
	 * <code>DarxCommInterface</code> of the sender.
	 */
	private final String senderName;

	/**
	 * The ordering number of the message.
	 */
	private final int serialNumber;

	/**
	 * Constructs a new instance of <code>DarxMessage</code>.
	 *
	 * @param msg
	 *            the body of the encapsulated message
	 * @param sn
	 *            the identity of the sender
	 * @param num
	 *            the ordering number of the encapsulated message
	 */
	public DarxMessage(final Serializable msg, final String sn, final int num) {
		this.msg = msg;
		this.senderName = sn;
		this.serialNumber = num;
	}

	/**
	 * @return the body of the encapsulated message.
	 */
	public Serializable getContents() {
		return this.msg;
	}

	/**
	 * @return the identity of the sender.
	 */
	String getSenderName() {
		return this.senderName;
	}

	/**
	 * @return the ordering number of the encapsulated message.
	 */
	public int getSerial() {
		return this.serialNumber;
	}
}
