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

import java.rmi.RemoteException;

/**
 * This is the generic exception for DARX events.<BR>
 * Exceptions which are specific to DARX must extend this class.
 *
 * @see InexistentNameException
 * @see DarxMalformedURLException
 * @see NoMoreReplicantsException
 * @see UnknownReplicantException
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class DarxException extends RemoteException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5243665349330519332L;
}
