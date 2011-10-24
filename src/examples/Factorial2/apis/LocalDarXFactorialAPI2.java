package examples.Factorial2.apis;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dimaxx.deployment.DimaXLocalLaunchScript;
import examples.Factorial2.masapplication.AgentFact;
import examples.Factorial2.masapplication.AgentMult;

/**
 * This API launch the Factorial application on different jvms using darx
 */
public class LocalDarXFactorialAPI2 {

	static AgentIdentifier factName = new AgentName("Fact");

	static AgentIdentifier getMultName(final int number){
		return new AgentName("M"+number);
	}

	public static void main(final String args[]) {

		final int n = 10;
		final AgentMult M = new AgentMult(getMultName(1), factName);
		final AgentMult M1 = new AgentMult(getMultName(2), factName);
		final AgentMult M2 = new AgentMult(getMultName(3), factName);

		final AgentFact F = new AgentFact(factName, n, M.getId(), M1.getId(), M2.getId());

		new DimaXLocalLaunchScript().launchDARX(7777, 7001, 7002,7003,7004);

		M.activateWithDarx(7002);
		M1.activateWithDarx(7003);
		M2.activateWithDarx(7004);

		F.activateWithDarx(7001);
		LogCompetence.write("\n\n\n"+" Factorial:  " + n+"\n\n\n");
	}
}
