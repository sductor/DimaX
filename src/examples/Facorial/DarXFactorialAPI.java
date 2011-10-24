package examples.Facorial;

import dima.basicagentcomponents.AgentName;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class DarXFactorialAPI {

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */
	public DarXFactorialAPI() {
	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */

	public static void main(final String args[]) {

		final int n = 10;
		System.out.println(" Factroial:  " + n);
		final AgentFact F = new AgentFact(new AgentName("Fact"), n);
		final AgentMult M = new AgentMult(new AgentName("M"));

		if (args.length > 0) {

			// The arguments spefify the URL and the port number for one agent
			F.activateWithDarx(args[0], new Integer(args[1]).intValue());
			M.activateWithDarx(args[2], new Integer(args[3]).intValue());
		} else {
			//		 The arguments spefify the default URL and port number for one
			// agent

			F.activateWithDarx("indira", 7002);
			M.activateWithDarx("indira", 7002);
		}
	}
}
