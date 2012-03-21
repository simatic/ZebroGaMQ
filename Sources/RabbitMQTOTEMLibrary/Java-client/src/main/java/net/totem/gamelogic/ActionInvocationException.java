/**
 TCM: TOTEM Communication Middleware
 Copyright: Copyright (C) 2009-2012
 Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 USA

 Developer(s): Denis Conan, Gabriel Adgeg
 */

package net.totem.gamelogic;

public class ActionInvocationException extends Exception {

	/**
	 * Serial version Unique Identifier for this exception.
	 */
	private static final long serialVersionUID = 7979541852867650988L;

	/**
	 * @see Exception#Exception()
	 */
	public ActionInvocationException() {
		super();
	}

	/**
	 * @see Exception#Exception(java.lang.String)
	 * @param message
	 *            the output to print
	 */
	public ActionInvocationException(final String message) {
		super(message);
	}

	/**
	 * Creates an exception caused by another exception.
	 * 
	 * @param cause
	 *            a throwable that cause this exception.
	 * @see Exception#Exception(java.lang.Throwable)
	 */
	public ActionInvocationException(final Throwable cause) {
		super();
	}

	/**
	 * Creates an exception caused by another exception.
	 * 
	 * @param message
	 *            the detailed output.
	 * @param cause
	 *            a throwable that cause this exception.
	 * @see Exception#Exception(java.lang.String,java.lang.Throwable)
	 */
	public ActionInvocationException(final String message,
			final Throwable cause) {
		super(message);
	}
}
