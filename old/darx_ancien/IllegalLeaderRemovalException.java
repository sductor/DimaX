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
 * This exception is thrown when an attempt to remove the given task would
 * result in an inconsistent replication group.<BR>
 * <BR>
 * It is originally conceived to be used only <B>within</B> replication groups.
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class IllegalLeaderRemovalException extends DarxException {

	/**
	 *
	 */
	private static final long serialVersionUID = 5346745321117698003L;
	/**
	 * the replicant whose removal failed.
	 **/
	private final ReplicantInfo replicant_info;

	/**
	 * Constructs a new instance of the class.
	 *
	 * @param ri
	 *            the information concerning the replicant related to the failed
	 *            removal attempt.
	 **/
	public IllegalLeaderRemovalException(final ReplicantInfo ri) {
		this.replicant_info = ri;
	}

	/**
	 * @return the URL of the host where the replicant is being looked for.
	 **/
	public ReplicantInfo getReplicantInfo() {
		return this.replicant_info;
	}

	/**
	 * @return the information conveyed by this exception, in String format.
	 **/
	@Override
	public String toString() {
		return "The replicant " + this.replicant_info.textifyDarxName()
				+ " cannot be removed as it is the leader of "
				+ "an operational replication group";
	}
}
