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
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.ProactiveComponents.ProactiveComponent;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.support.GimaObject;
import dimaxx.deployment.DimaXDeploymentScript;
import dimaxx.deployment.DimaXLocalLaunchScript;
import dimaxx.hostcontrol.RemoteHostExecutor;
import dimaxx.server.HostIdentifier;

public class APILauncherModule extends BasicAgentModule<CommunicatingCompetentComponent> {
	private static final long serialVersionUID = 7241441256737644000L;

	public enum LaunchType { NotThreaded, FIPA, DarXLocal, DarXDeployed } 

	public static final String _logKeyForAPIManagement = "log key for api start";


	private Map<AgentIdentifier, BasicCompetentAgent> registeredAgent = 
			new HashMap<AgentIdentifier, BasicCompetentAgent>();

	//Launch
	private LaunchType myLaunchType = null;

	//Darx Local
	private Integer nameServer_port = null;
	private Integer server_port = null;

	//Darx Deployed
	private DimaXDeploymentScript machines = null;
	private Map<AgentIdentifier, HostIdentifier> locations = null;
	private Collection<HostIdentifier> hostsCollection = null;
	private Iterator<HostIdentifier> hostsIt = null;

	//No thread
	private LocalFipaScheduler scheduler = null;

	//
	// Constructor
	//

	public APILauncherModule(final CommunicatingCompetentComponent ag) throws CompetenceException {
		super(ag);
		ag.addLogKey(_logKeyForAPIManagement, true, false);
	}

	//
	// Init
	//

	public void initFipa(){
		myLaunchType = LaunchType.FIPA;
		AgentManagementSystem.initAMS();
	}

	public void initLocalDarx(final int nameServer_port, final int server_port)  {
		myLaunchType = LaunchType.DarXLocal;
		this.nameServer_port = nameServer_port;
		this.server_port = server_port;
	}


	//routine
	private void deployedDarxInstanciation(final File machinesFile)
			throws JDOMException, IOException {
		myLaunchType = LaunchType.DarXDeployed;
		machines = new DimaXDeploymentScript(machinesFile);
		if (machines.getAllHosts().isEmpty())
			getMyAgent().signalException("no machines!!!");
		else {
			machines.launchNameServer();
			machines.launchAllDarXServer();
		}
	}	
	//

	public void initDeployedDarx(
			final File machinesFile, 
			Collection<HostIdentifier> hosts) 
					throws JDOMException, IOException{
		deployedDarxInstanciation(machinesFile);
		hostsCollection=hosts;
		hostsIt = hostsCollection.iterator();
	}

	public void initDeployedDarx(
			final File machinesFile) 
					throws JDOMException, IOException{
		deployedDarxInstanciation(machinesFile);
		hostsCollection=machines.getAllHostsIdentifier();
		hostsIt = hostsCollection.iterator();
	}

	public void initDeployedDarx(
			final File machinesFile, 
			final HashMap<AgentIdentifier, HostIdentifier> locations) 
					throws JDOMException, IOException{
		deployedDarxInstanciation(machinesFile);
		this.locations = locations;
	}

	public void initNotThreaded(int n){
		myLaunchType = LaunchType.NotThreaded;
		scheduler = new LocalFipaScheduler(n);
	}

	public void initNotThreaded(){
		myLaunchType = LaunchType.NotThreaded;
		scheduler = new LocalFipaScheduler();
	}

	//
	// Launch method
	//

	protected boolean launch(BasicCompetentAgent c){
		registeredAgent.put(c.getIdentifier(), c);
		c.addObserver(this.getIdentifier(), LogService.logNotificationKey);

		switch (myLaunchType) {
		case NotThreaded:
			scheduler.add(c);
			break;
		case FIPA:
			c.activateWithFipa();
			break;
		case DarXLocal:
			c.activateWithDarx(server_port);
			break;
		case DarXDeployed:
			if (locations!=null){
				c.activateWithDarx(locations.get(c).getUrl(), locations.get(c).getPort());
			} else {
				if (!hostsIt.hasNext())
					hostsIt =hostsCollection.iterator();
				final HostIdentifier machine = hostsIt.next();
				c.activateWithDarx(machine.getUrl(),machine.getPort());
			}
			break;
		default:
			break;
		}
		return true;
	}

	protected boolean destroy(BasicCompetentAgent c){
		registeredAgent.remove(c.getIdentifier());

		switch (myLaunchType) {
		case NotThreaded:
			scheduler.remove(c);
			break;
		case FIPA:
			c.desactivateWithFipa();
			break;
		case DarXLocal:
			//			c.activateWithDarx(server_port);
			//			break;
		case DarXDeployed:
			throw new RuntimeException("todo retirer lagent de location/it?");
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

	public void launch(Collection<BasicCompetentAgent> ags){
		for (final BasicCompetentAgent c : ags)
			launch(c);
	}



	//
	// Primitive
	//

	public void startAll(){
		start(registeredAgent.values());
	}

	public void start(BasicCompetentAgent ag){
		Collection<BasicCompetentAgent> ags= new ArrayList<BasicCompetentAgent>();
		ags.add(ag);
		start(ags);
	}

	private void start(Collection<BasicCompetentAgent> ags){
		StartSimulationMessage m = new StartSimulationMessage();
		for (BasicCompetentAgent ag : ags){
			if (myLaunchType.equals(LaunchType.NotThreaded)){
				ag.start(m);
			} else {
				getMyAgent().sendMessage(ag.getIdentifier(), m);
			}

			getMyAgent().logMonologue("Start order sended to "+ag.getIdentifier(),_logKeyForAPIManagement);
		}
		if (myLaunchType.equals(LaunchType.NotThreaded)){
			scheduler.runApplication();
			scheduler=null;
		}
	}

	//
	// SubClasses
	//

	//
	// Local Scheduler
	//

	//GROS BUG : SI ON AJOUTE UN AGENT AU MILIEU IL NE SERA PAS INITIALISE!!!!!! 
	class LocalFipaScheduler extends GimaObject{

		private static final long serialVersionUID = -6806175893332597817L;
		public int step = 0;
		final int nbMaxStep;
		List<BasicCompetentAgent> agents = new ArrayList<BasicCompetentAgent>();


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
		private void remove(BasicCompetentAgent c) {
			AgentManagementSystem.getDIMAams().removeAquaintance(c);
			agents.remove(c);

		}

		private boolean add(final BasicCompetentAgent c){
			AgentManagementSystem.getDIMAams().addAquaintance(c);
			return agents.add(c);
		}

		/*
		 * 
		 */

		private void initialize(){
			for (final ProactiveComponent c : registeredAgent.values())
				c.proactivityInitialize();
		}


		private void executeStep(){
			Collections.shuffle(agents);
			for (final ProactiveComponent c : agents){
				//			LoggerManager.write("\n\n-------------------->");//SIMULATION : executing "+c.toString()+"***********");
				c.preActivity();
				LogService.flush();
				c.step();
				LogService.flush();
				c.postActivity();
				LogService.flush();
			}

			final Iterator<BasicCompetentAgent> it = agents.iterator();
			while(it.hasNext()){
				final ProactiveComponent c = it.next();
				if (!c.isAlive()){
					c.proactivityTerminate();
					it.remove();
				}
			}
		}

		public void terminate(){
			for (final ProactiveComponent c : registeredAgent.values())
				c.proactivityTerminate();
		}

		public void runApplication(){
			int step = 0;

			LogService.flush();
			this.initialize();

			while (!registeredAgent.isEmpty() || (nbMaxStep!=-1 && step < nbMaxStep)){
				//			LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");
				LogService.flush();
				this.executeStep();
				step++;
			}

			LogService.flush();
			terminate();

			LogService.flush();
			LogService.write("\n\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
			agents=null;
		}

	}

	//
	//
	//


	class StartSimulationMessage extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date startDate = new Date();

		public Date getStartDate() {
			return startDate;
		}
	}
}








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