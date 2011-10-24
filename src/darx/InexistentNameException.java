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

/**
 * This exception is thrown when a searched task name has no match inside the
 * set of the application's tasks.<BR>
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class InexistentNameException extends DarxException {

	/**
	 *
	 */
	private static final long serialVersionUID = -6258774176903238007L;
	/**
	 * the generic name of the agent that can't be found in the application.
	 */
	private final String task_name;

	/**
	 * Constructs a new instance of the class.
	 *
	 * @param task_name
	 *            The generic name of the agent that can't be found in the
	 *            application.
	 */
	public InexistentNameException(final String task_name) {
		this.task_name = task_name;
	}

	/**
	 * @return The generic name of the agent that can't be found in the
	 *         application.
	 */
	public String getName() {
		return this.task_name;
	}

	/**
	 * @return The explanation for the triggering of this exception.
	 */
	@Override
	public String toString() {
		return "The name " + this.task_name
				+ " is not registered on this server";
	}
}
