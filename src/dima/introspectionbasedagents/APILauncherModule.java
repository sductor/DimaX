package dima.introspectionbasedagents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.ProactiveComponents.ProactiveComponent;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.support.GimaObject;
import dimaxx.deployment.DimaXDeploymentScript;
import dimaxx.deployment.DimaXLocalLaunchScript;
import dimaxx.hostcontrol.LocalHost;
import dimaxx.hostcontrol.RemoteHostExecutor;
import dimaxx.server.HostIdentifier;

public class APILauncherModule extends BasicAgentModule<BasicCompetentAgent> {
	private static final long serialVersionUID = 7241441256737644000L;

	public enum LaunchType { NotThreaded, FIPA, DarX } 

	public static final String _logKeyForAPIManagement = "log key for api start";


	private Map<AgentIdentifier, BasicCompetentAgent> registeredAgent = 
			new HashMap<AgentIdentifier, BasicCompetentAgent>();
	private Map<AgentIdentifier, HostIdentifier> locations = 
			new HashMap<AgentIdentifier, HostIdentifier>();

	//Launch
	private LaunchType myLaunchType = null;

	//Darx
	List<HostIdentifier> avalaibleHosts = 
			new ArrayList<HostIdentifier>();
	int pos=0;

	//No thread
	private LocalFipaScheduler scheduler = null;

	//
	// Constructor
	//

	/**
	 * Lancement avec thread (fipa) ou non (scheduler)
	 * @param threaded
	 * @throws CompetenceException
	 */
	APILauncherModule(boolean threaded) throws CompetenceException {
		if (threaded)
			initWithFipa();
		else 
			initNotThreaded();	
	}

	/**
	 * lancement avec darx en local
	 * @param nameServer_port
	 * @param server_port
	 * @throws CompetenceException
	 */
	APILauncherModule(int nameServer_port, int server_port) throws CompetenceException {
		initLocalDarx(nameServer_port, server_port);	
	}

	/**
	 * lancement avec darx en utilisant toutes les machines disponibles
	 * @param machinesFile
	 * @throws CompetenceException
	 * @throws JDOMException
	 * @throws IOException
	 */
	APILauncherModule(
			String machinesFile) 
					throws CompetenceException, JDOMException,IOException {
		DimaXDeploymentScript machines = new DimaXDeploymentScript(machinesFile);
		initDeployedDarx(machines);
	}
	APILauncherModule(
			File machinesFile) 
					throws CompetenceException, JDOMException,IOException {
		DimaXDeploymentScript machines = new DimaXDeploymentScript(machinesFile);
		initDeployedDarx(machines);
	}


	//	@Override
	//	public void setMyAgent(BasicCompetentAgent ag){
	//		super.setMyAgent(ag);
	//		//		getMyAgent().addLogKey(_logKeyForAPIManagement, false, false);		
	//	}

	//
	// Accessors
	//


	public Collection<HostIdentifier> getAvalaibleHosts() {
		return avalaibleHosts;
	}	

	public Collection<AgentIdentifier> getAgentsRunningOn(HostIdentifier h){
		Collection<AgentIdentifier> result =
				new ArrayList<AgentIdentifier>();
		for (AgentIdentifier a : locations.keySet()){
			if (locations.get(a).equals(h))
				result.add(a);
		}
		return result;
	}

	//
	// Launch method
	//

	void init() {
		getMyAgent().launchWith(this);
		startActivity(getMyAgent());
		registeredAgent.remove(getMyAgent());

		if (myLaunchType.equals(LaunchType.NotThreaded)){
			scheduler.runApplication();
			scheduler=null;
		}
	}

	boolean launch(BasicCompetentAgent c, HostIdentifier machine){
		if (!getAvalaibleHosts().contains(machine))
			throw new RuntimeException("i can not use this machine "+machine+" available machines are "+getAvalaibleHosts());

		registeredAgent.put(c.getIdentifier(), c);
		c.addObserver(this.getIdentifier(), LogService.logNotificationKey);
		c.addObserver(this.getIdentifier(), EndActivityMessage.class);

		locations.put(c.getIdentifier(), machine);

		switch (myLaunchType) {
		case NotThreaded:
			scheduler.add(c);
			break;
		case FIPA:
			c.activateWithFipa();
			break;
		case DarX:
			c.activateWithDarx(machine.getUrl(), machine.getPort());
			break;
		default:
			break;
		}
		return true;
	}

	boolean launch(BasicCompetentAgent c){
		if (pos==avalaibleHosts.size())
			pos=0;

		return launch(c, avalaibleHosts.get(pos));
	}

	boolean destroy(BasicCompetentAgent c){
		registeredAgent.remove(c.getIdentifier());
		locations.remove(c);

		switch (myLaunchType) {
		case NotThreaded:
			scheduler.remove(c);
			break;
		case FIPA:
			AgentManagementSystem.getDIMAams().removeAquaintance(c);
			break;
		case DarX:
			break;
			//			if (locations!=null){
			//				c.activateWithDarx(locations.get(c).getUrl(), locations.get(c).getPort());
			//			} else {
			//				if (!hostsIt.hasNext())
			//					hostsIt =hostsCollection.iterator();
			//				final HostIdentifier machine = hostsIt.next();
			//				c.activateWithDarx(machine.getUrl(),machine.getPort());
			//			}
			//			break;
		default:
			break;
		}
		return true;
	}


	void startApplication(){
		start(registeredAgent.values());
	}

	void startActivity(BasicCompetentAgent ag){
		Collection<BasicCompetentAgent> ags= new ArrayList<BasicCompetentAgent>();
		ags.add(ag);
		start(ags);
	}

	@MessageHandler
	void end(NotificationMessage<EndActivityMessage> m){
		getMyAgent().logMonologue(m.getSender()+" has ended activity ... nothing to do...");
	}

	//
	// Primitive
	//

	private void initWithFipa(){
		myLaunchType = LaunchType.FIPA;
		AgentManagementSystem.initAMS();
		avalaibleHosts.add(HostIdentifier.getLocalHost());
	}

	private void initLocalDarx(final int nameServer_port, final int server_port)  {
		myLaunchType = LaunchType.DarX;
		DimaXLocalLaunchScript darxLaunch = new DimaXLocalLaunchScript();
		darxLaunch.launchDARX(nameServer_port, server_port);
		avalaibleHosts.add(new HostIdentifier(LocalHost.getUrl(), server_port));
	}


	//routine
	private void initDeployedDarx(
			final DimaXDeploymentScript machines)
					throws JDOMException, IOException {
		myLaunchType = LaunchType.DarX;
		if (machines.getAllHosts().isEmpty())
			getMyAgent().signalException("no machines!!!");
		else {
			machines.launchNameServer();
			machines.launchAllDarXServer();
		}		
		avalaibleHosts.addAll(machines.getDarxServersIdentifier());
	}	

	private void initNotThreaded(){
		myLaunchType = LaunchType.NotThreaded;
		scheduler = new LocalFipaScheduler();
		avalaibleHosts.add(HostIdentifier.getLocalHost());
	}

	void start(Collection<BasicCompetentAgent> ags){
		StartActivityMessage m = new StartActivityMessage();
		for (BasicCompetentAgent ag : ags){
			if (myLaunchType.equals(LaunchType.NotThreaded)){
				ag.start(m);
			} else {
				ag.start(m);
				getMyAgent().sendMessage(ag.getIdentifier(), m);
			}
			//			getMyAgent().logMonologue("Start order sended to "+ag.getIdentifier(),_logKeyForAPIManagement);
		}
	}


	//
	// SubClasses
	//

	//
	// Local Scheduler
	//

	class LocalFipaScheduler extends GimaObject{

		private static final long serialVersionUID = -6806175893332597817L;
		public int step = 0;
		final int nbMaxStep;
		List<BasicCompetentAgent> toInitialize = new ArrayList<BasicCompetentAgent>();
		List<BasicCompetentAgent> toExecute = new ArrayList<BasicCompetentAgent>();
		List<BasicCompetentAgent> toTerminate = new ArrayList<BasicCompetentAgent>();


		/*
		 * 
		 */
		private LocalFipaScheduler() {
			super();
			this.nbMaxStep = -1;
		}

		private LocalFipaScheduler(int nbMaxStep) {
			super();
			this.nbMaxStep = nbMaxStep;
		}

		/*
		 * 
		 */
		Collection<BasicCompetentAgent> toAdd = new ArrayList<BasicCompetentAgent>();
		private boolean add(final BasicCompetentAgent c){
			AgentManagementSystem.getDIMAams().addAquaintance(c);
			return toAdd.add(c);
		}

		Collection<BasicCompetentAgent> toRemove = new ArrayList<BasicCompetentAgent>();
		private void remove(final BasicCompetentAgent c){
			AgentManagementSystem.getDIMAams().removeAquaintance(c);
			toRemove.add(c);
		}
		/*
		 * 
		 */

		public void runApplication(){
			int step = 0;
			//AJOUT DES NOUVEAUX AGENTS
			toInitialize.addAll(toAdd);
			toAdd.clear();

			while (!( (toExecute.isEmpty() && toInitialize.isEmpty()) || (nbMaxStep!=-1 && step > nbMaxStep) )){
				//LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");


				//AJOUT DES NOUVEAUX AGENTS
				toInitialize.addAll(toAdd);
				toAdd.clear();
				//RETRAIT DES AGENTS SUPPRIMME
				toInitialize.removeAll(toRemove);
				toExecute.removeAll(toRemove);
				toTerminate.removeAll(toRemove);


				//AGENT PRO ACTIVITY INITAILISATION
				LogService.flush();
				for (final BasicCompetentAgent c : toInitialize){
					c.proactivityInitialize();
					toExecute.add(c);
				}
				toInitialize.clear();

				//AGENT STEP ACTIVITIES
				LogService.flush();
				for (final BasicCompetentAgent c : toExecute){
					if (c.isAlive()){
						if (c.isActive()){
							c.preActivity();
							LogService.flush();
							c.step();
							LogService.flush();
							c.postActivity();
						} else 
							c.tryToResumeActivity();
					} else {
						toTerminate.add(c);
					}
				}

				//AGENT PRO ACTIVITY TERMINATION
				LogService.flush();
				for (final BasicCompetentAgent c : toTerminate){
					c.proactivityTerminate();
					toExecute.remove(c);
					AgentManagementSystem.getDIMAams().removeAquaintance(c);
				}
				toTerminate.clear();

				//LIFE IS GOOD : NEXT STEP
				step++;
			}

			LogService.flush();
			LogService.write("\n\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
		}
	}


	//
	//
	//


	class StartActivityMessage extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date startDate = new Date();

		public Date getStartDate() {
			return startDate;
		}
	}

	class EndActivityMessage extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date endDate = new Date();

		public Date getEndDate() {
			return endDate;
		}
	}
}


//
//private void initDeployedDarx(
//		final File machinesFile, 
//		final HashMap<AgentIdentifier, HostIdentifier> locations)
//		throws JDOMException, IOException {
//	myLaunchType = LaunchType.DarXDeployed;
//	DimaXDeploymentScript machines = new DimaXDeploymentScript(machinesFile);
//	if (machines.getAllHosts().isEmpty())
//		getMyAgent().signalException("no machines!!!");
//	else {
//		machines.launchNameServer();
//		machines.launchAllDarXServer();
//	}
//	
//	//
//	
//	this.locations = locations;
//}	

//
//public APILauncherModule(File machinesFile,
//		HashMap<AgentIdentifier, HostIdentifier> locations) 
//				throws CompetenceException, JDOMException,IOException{
//	initDeployedDarx(machinesFile, locations);
//}



//	protected void launchWithDarx(final File f, Collection<HostIdentifier> machines) throws JDOMException, IOException {
//		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);//contient le chemin vers le fichier xml
//		if (script.getAllHosts().isEmpty())
//			this.signalException("no machines!!!");
//		else {
//			Iterator<HostIdentifier> machinesIt = machines.iterator();
//			for (final BasicCommunicatingAgent ag : this.getAgents()){
//				if (!machinesIt.hasNext())
//					machinesIt =machines.iterator();
//
//				final HostIdentifier machine = machinesIt.next();
//				ag.activateWithDarx(machine.getUrl(),machine.getPort());
//			}
//			startAppli();
//		}
//	}
//
//	protected void launchWithDarx(final File f, final HashMap<AgentIdentifier, HostIdentifier> locations) throws JDOMException, IOException {
//		final DimaXDeploymentScript script = new DimaXDeploymentScript(f);
//		if (!script.getAllHostsIdentifier().containsAll(locations.values()))
//			this.signalException("some machines are unknown!");
//		else {
//
//			script.launchNameServer();
//			script.launchAllDarXServer();
//
//			for (final BasicCommunicatingAgent ag : this.getAgents())
//				ag.activateWithDarx(locations.get(ag).getUrl(),locations.get(ag).getPort());
//					startAppli();
//		}
//	}
//
//	protected void launchWithDarxOnAllMachines(final File f) throws JDOMException, IOException {
//		Collection<RemoteHostExecutor> allHosts = new DimaXDeploymentScript(f).getAllHosts();
//		Collection<HostIdentifier> machines = new ArrayList<HostIdentifier>();
//		for (RemoteHostExecutor h : allHosts)
//			machines.add(h.generateHostIdentifier());
//				launchWithDarx(f, machines);
//	}

/*
 * 
// */
//
//protected void launchWithoutThreads(final int n) {
//	final LocalFipaScheduler s = new LocalFipaScheduler(this);
//	startAppli();
//	s.runApplication(n);
//}
//
//protected void launchWithoutThreads() {
//	final LocalFipaScheduler s = new LocalFipaScheduler(this);
//	startAppli();
//	s.runApplication();
//}




//protected synchronized void launchWithFipa() {
//	if (AgentManagementSystem.getDIMAams()==null) AgentManagementSystem.initAMS();
//	for (final BasicCommunicatingAgent a : this.getAgents())
//		a.activateWithFipa();
//			startAppli();
//}
//
//
///*
// * 
// */
//
//protected void launchWithDarxLocally(final int nameServer_port, final int server_port)  {
//	new DimaXLocalLaunchScript().launchDARX(nameServer_port, server_port);
//	for (final BasicCommunicatingAgent a : this.getAgents())
//		a.activateWithDarx(server_port);
//			startAppli();
//}
//
///*
// * 
// */
//
//protected void startDeployedDarx(final File f) throws JDOMException, IOException{
//	final DimaXDeploymentScript script = new DimaXDeploymentScript(f);//contient le chemin vers le fichier xml
//	if (script.getAllHosts().isEmpty())
//		this.signalException("no machines!!!");
//	else {
//
//		script.launchNameServer();
//		script.launchAllDarXServer();
//	}	
//}
//