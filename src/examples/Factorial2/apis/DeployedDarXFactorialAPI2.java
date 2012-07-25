
package examples.Factorial2.apis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.deployment.DimaXDeploymentScript;
import dima.introspectionbasedagents.services.deployment.hosts.RemoteHostInfo;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import examples.Factorial2.masapplication.AgentFact;
import examples.Factorial2.masapplication.AgentMult;

/**
 * This API launch the Factorial application on different jvms using darx
 */
public class DeployedDarXFactorialAPI2 {

	static AgentIdentifier factName = new AgentName("Fact");

	static int nameNumber = 0;
	static AgentIdentifier getMultName(){
		DeployedDarXFactorialAPI2.nameNumber++;
		return new AgentName("M"+DeployedDarXFactorialAPI2.nameNumber);
	}

	public static void main(final String args[]) throws JDOMException, IOException {
		final Collection<AgentIdentifier> launchedMults = new ArrayList<AgentIdentifier>();
		final int n = 10;
		final DimaXDeploymentScript machines = new DimaXDeploymentScript(args[0]);//args[0] contient le chemin vers le fichier xml

		machines.launchNameServer();
		machines.launchAllDarXServer();
		try{Thread.sleep(5000);}catch(final Exception e){};

		//Lancement d'un agent mult sur chaque machine
		for (final RemoteHostInfo h : machines.getHosts()){
			final AgentMult m = new AgentMult(DeployedDarXFactorialAPI2.getMultName(), DeployedDarXFactorialAPI2.factName);
			System.out.println("starting mult on "+h);
			m.activateWithDarx(h.getUrl(), h.getPort());
			System.out.println("starting mult name is "+m.getIdentifier());
			launchedMults.add(m.getId());
		}


		final AgentFact F = new AgentFact(DeployedDarXFactorialAPI2.factName, n, launchedMults);
		final RemoteHostInfo h = machines.getHosts().iterator().next();
		F.activateWithDarx(h.getUrl(), h.getPort());

		LogService.write("\n\n\n"+" Factorial:  " + n+"\n\n\n");
	}
}
