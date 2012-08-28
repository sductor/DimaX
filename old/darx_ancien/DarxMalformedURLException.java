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
 * This exception is thrown when a MalformedURLException has occured in the DARX
 * context. Either no legal protocol could be found in a specification string or
 * the string could not be parsed. <BR>
 * <BR>
 *
 * @see java.net.MalformedURLException
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
public class DarxMalformedURLException extends DarxException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8599189561773136495L;
	/**
	 * The faulty URL which caused this exception to be thrown.
	 **/
	String url;

	/**
	 * Constructs a MalformedURLException with the faulty URL.
	 **/
	public DarxMalformedURLException(final String url) {
		this.url = url;
	}

	/**
	 * @return the faulty URL
	 **/
	public String getURL() {
		return this.url;
	}
}
