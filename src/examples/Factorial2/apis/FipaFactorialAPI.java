package examples.Factorial2.apis;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import examples.Factorial2.masapplication.AgentFact;
import examples.Factorial2.masapplication.AgentMult;

public class FipaFactorialAPI {

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

		AgentManagementSystem.initAMS();

		M.activateWithFipa();
		M1.activateWithFipa();
		M2.activateWithFipa();

		F.activateWithFipa();
		LogCompetence.write("\n\n\n"+" Factroial:  " + n+"\n\n\n");
	}
}
