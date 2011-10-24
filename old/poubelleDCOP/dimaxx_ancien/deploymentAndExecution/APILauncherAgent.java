package dimaxx.deploymentAndExecution;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.competences.DuplicateCompetenceException;
import dima.introspectionbasedagents.competences.UnInstanciableCompetenceException;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dimaxx.integrationkernel.DimaXDeploymentScript;
import dimaxx.integrationkernel.DimaXLocalLaunchScript;
import dimaxx.server.HostIdentifier;

public abstract class APILauncherAgent extends BasicCompetentAgent{


	/**
	 *
	 */
	private static final long serialVersionUID = 7241441256737644000L;

	public abstract Collection<? extends BasicCommunicatingAgent> getAgents();

	public APILauncherAgent(final AgentIdentifier newId) throws UnInstanciableCompetenceException,
	DuplicateCompetenceException {
		super(newId);
	}

	public APILauncherAgent(final String newId)
	throws UnInstanciableCompetenceException,
	DuplicateCompetenceException {
		super(newId);
	}

	//
	// Launch
	//

	public synchronized void launchWithFipa() {
		if (AgentManagementSystem.getDIMAams()==null) AgentManagementSystem.initAMS();
		for (final BasicCommunicatingAgent a : this.getAgents())
			a.activateWithFipa();
	}
	public void launchWithDarx(final int nameServer_port, final int server_port)  {
		new DimaXLocalLaunchScript().launchDARX(nameServer_port, server_port);
		for (final BasicCommunicatingAgent a : this.getAgents())
			a.activateWithDarx(server_port);
	}

	public void launchWithDarx(final File f) throws JDOMException, IOException {
		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);//contient le chemin vers le fichier xml
		if (script.getAllHosts().isEmpty())
			this.logException("no machines!!!");
		else {

			script.launchNameServer();
			script.launchAll();

			Iterator<RemoteHostExecutor> machines = script.getAllHosts().iterator();
			for (final BasicCommunicatingAgent ag : this.getAgents()){
				if (!machines.hasNext())
					machines =script.getAllHosts().iterator();

				final RemoteHostExecutor machine = machines.next();
				ag.activateWithDarx(machine.getUrl(),machine.getPort());
			}
		}
	}

	public void launchWithDarx(final File f, final HashMap<AgentIdentifier, HostIdentifier> locations) throws JDOMException, IOException {
		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);
		if (!script.getAllHostsIdentifier().containsAll(locations.values()))
			this.logException("some machines are unknown!");
		else {

			script.launchNameServer();
			script.launchAll();

			for (final BasicCommunicatingAgent ag : this.getAgents())
				ag.activateWithDarx(locations.get(ag).getUrl(),locations.get(ag).getPort());
		}
	}

	public void launchWithoutThreads(final int n) {
		final LocalFipaScheduler s = new LocalFipaScheduler(this);
		s.runApplication(n);
	}

	public void launchWithoutThreads() {
		final LocalFipaScheduler s = new LocalFipaScheduler(this);
		s.runApplication();
	}
}
