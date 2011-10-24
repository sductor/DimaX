package dima.introspectionbasedagents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.ProactiveComponents.ProactiveComponent;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dimaxx.deployment.DimaXDeploymentScript;
import dimaxx.deployment.DimaXLocalLaunchScript;
import dimaxx.hostcontrol.RemoteHostExecutor;
import dimaxx.server.HostIdentifier;

public abstract class APILauncherAgent extends BasicCompetentAgent{
	private static final long serialVersionUID = 7241441256737644000L;

	//
	// Constructor
	//

	public APILauncherAgent(final AgentIdentifier newId) throws CompetenceException {
		super(newId);
	}

	public APILauncherAgent(final String newId)
			throws CompetenceException {
		super(newId);
	}

	//
	// Abstract
	//

	public abstract Collection<? extends BasicCompetentAgent> getAgents();

	//
	// Launch methods
	//

	protected synchronized void launchWithFipa() {
		if (AgentManagementSystem.getDIMAams()==null) AgentManagementSystem.initAMS();
		for (final BasicCommunicatingAgent a : this.getAgents())
			a.activateWithFipa();
				startAppli();
	}


	/*
	 * 
	 */

	protected void launchWithDarxLocally(final int nameServer_port, final int server_port)  {
		new DimaXLocalLaunchScript().launchDARX(nameServer_port, server_port);
		for (final BasicCommunicatingAgent a : this.getAgents())
			a.activateWithDarx(server_port);
				startAppli();
	}

	/*
	 * 
	 */

	protected void startDeployedDarx(final File f) throws JDOMException, IOException{

		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);//contient le chemin vers le fichier xml
		if (script.getAllHosts().isEmpty())
			this.logException("no machines!!!");
		else {

			script.launchNameServer();
			script.launchAllDarXServer();
		}	
	}

	protected void launchWithDarx(final File f, Collection<HostIdentifier> machines) throws JDOMException, IOException {
		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);//contient le chemin vers le fichier xml
		if (script.getAllHosts().isEmpty())
			this.logException("no machines!!!");
		else {
			Iterator<HostIdentifier> machinesIt = machines.iterator();
			for (final BasicCommunicatingAgent ag : this.getAgents()){
				if (!machinesIt.hasNext())
					machinesIt =machines.iterator();

				final HostIdentifier machine = machinesIt.next();
				ag.activateWithDarx(machine.getUrl(),machine.getPort());
			}
			startAppli();
		}
	}

	protected void launchWithDarx(final File f, final HashMap<AgentIdentifier, HostIdentifier> locations) throws JDOMException, IOException {
		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);
		if (!script.getAllHostsIdentifier().containsAll(locations.values()))
			this.logException("some machines are unknown!");
		else {

			script.launchNameServer();
			script.launchAllDarXServer();

			for (final BasicCommunicatingAgent ag : this.getAgents())
				ag.activateWithDarx(locations.get(ag).getUrl(),locations.get(ag).getPort());
					startAppli();
		}
	}

	protected void launchWithDarxOnAllMachines(final File f) throws JDOMException, IOException {
		Collection<RemoteHostExecutor> allHosts = new DimaXDeploymentScript(f).getAllHosts();
		Collection<HostIdentifier> machines = new ArrayList<HostIdentifier>();
		for (RemoteHostExecutor h : allHosts)
			machines.add(h.generateHostIdentifier());
				launchWithDarx(f, machines);
	}

	/*
	 * 
	 */

	protected void launchWithoutThreads(final int n) {
		final LocalFipaScheduler s = new LocalFipaScheduler(this);
		startAppli();
		s.runApplication(n);
	}

	protected void launchWithoutThreads() {
		final LocalFipaScheduler s = new LocalFipaScheduler(this);
		startAppli();
		s.runApplication();
	}

	//
	// Primitive
	//

	private void startAppli(){
		this.wwait(1000);
		StartSimulationMessage m = new StartSimulationMessage();
		for (BasicCompetentAgent ag : getAgents()){
			if (LogService.toFiles)
				ag.addObserver(this.getIdentifier(), LogService.logKey);
			ag.start();
			//			logMonologue("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! sended to "+ag.getIdentifier());
		}
	}


	//
	// SubClass
	//


	public class StartSimulationMessage extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date startDate = new Date();

		public Date getStartDate() {
			return startDate;
		}

	}
}






class LocalFipaScheduler extends ArrayList<BasicCommunicatingAgent>{


	/**
	 *
	 */
	private static final long serialVersionUID = -6806175893332597817L;
	public static int step = 0;

	public LocalFipaScheduler(final APILauncherAgent c) {
		super(c.getAgents().size());
		AgentManagementSystem.initAMS();
		for (final BasicCommunicatingAgent a : c.getAgents()){
			this.add(a);
			AgentManagementSystem.getDIMAams().addAquaintance(a);
		}
	}

	public void initialize(){
		for (final ProactiveComponent c : this)
			c.proactivityInitialize();
	}

	@Override
	public boolean add(final BasicCommunicatingAgent c){
		AgentManagementSystem.getDIMAams().addAquaintance(c);
		return super.add(c);
	}

	public void executeStep(){
		Collections.shuffle(this);
		for (final ProactiveComponent c : this){
			//			LoggerManager.write("\n\n-------------------->");//SIMULATION : executing "+c.toString()+"***********");
			c.preActivity();
			LogService.flush();
			c.step();
			LogService.flush();
			c.postActivity();
			LogService.flush();
		}

		final Iterator<BasicCommunicatingAgent> it = this.iterator();
		while(it.hasNext()){
			final ProactiveComponent c = it.next();
			if (!c.isAlive()){
				c.proactivityTerminate();
				it.remove();
			}
		}
	}

	public void runApplication(){
		this.initialize();
		int step = 0;
		while (!this.isEmpty()){
			//			LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");
			LogService.flush();
			this.executeStep();
			LogService.flush();
			step++;
		}
		LogService.write("\n\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
		System.exit(1);
	}

	public void runApplication(final int nbMaxStep){
		this.initialize();
		LogService.flush();
		while (!( this.isEmpty() || step > nbMaxStep) ){
			//			LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");
			LogService.flush();
			this.executeStep();
			LogService.flush();
			step++;
		}
		LogService.write("\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
		System.exit(1);
	}
}