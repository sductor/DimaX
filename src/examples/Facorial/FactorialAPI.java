package examples.Facorial;

import dima.basicagentcomponents.AgentName;
import dima.kernel.FIPAPlatform.AgentManagementSystem;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class FactorialAPI {

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */
	public FactorialAPI() {
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

		AgentManagementSystem.initAMS();

		/*
		 * the two following lines are not useful if you are using the FIPA AMS
		 * but they can be used to make the program more efficient
		 */

		//F.addAquaintance(M);
		//M.addAquaintance(F);
		// F.activate(); M.activate();
		F.activateWithFipa();
		M.activateWithFipa();

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
