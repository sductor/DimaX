package examples.Facorial;

import darx.Darx;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class ScriptDarxServer {

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */
	public ScriptDarxServer() {
	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */

	public static void main(final String args[]) {
		final String[] arg = new String[1];
		/*
		 * the following lines allow to intialize only one DarX Server at the
		 * same time (one JVM)
		 */
		/* the URL corresponds to the local host */

		//Server
		if (args.length > 0)
			Darx.main(args);

		else {
			arg[0] = "7002";
			Darx.main(arg);
		}
	}

}
/**
 * Insert the method's description here. Creation date: (08/11/2002 07:09:41)
 *
 * @return java.lang.Integer
 * @param a
 *            java.lang.Integer
 * @param b
 *            java.lang.Integer
 */

