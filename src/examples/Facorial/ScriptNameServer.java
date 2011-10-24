package examples.Facorial;

import darx.NameServerImpl;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class ScriptNameServer {

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */
	public ScriptNameServer() {
	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */

	public static void main(final String args[]) {

		/*
		 * the two following lines allow to intialize the DarX NamingService and
		 * Server
		 */

		//Naming Service
		final String[] s = new String[1];
		s[0] = "7777";
		NameServerImpl.main(s);

	}
	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:09:41)
	 *
	 * @return java.lang.Integer
	 * @param a
	 *            java.lang.Integer
	 * @param b
	 *            java.lang.Integer
	 */

}
