package dima.introspectionbasedagents.services.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.ProactiveComponentInterface;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.CompetentComponent;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.support.GimaObject;
import dimaxx.deployment.DimaXDeploymentScript;
import dimaxx.deployment.DimaXLocalLaunchScript;
import dimaxx.hostcontrol.LocalHost;
import dimaxx.server.HostIdentifier;

public class APIAgent extends BasicCompetentAgent {


	/**
	 *
	 */
	private static final long serialVersionUID = 8785216532127504439L;
	private APILauncherModule api = null;

	/*
	 *
	 */

	public APIAgent(final AgentIdentifier newId)
			throws CompetenceException {
		super(newId);
	}
	public APIAgent(final String newId) throws CompetenceException {
		super(newId);
	}

	/*
	 *
	 */

	public APILauncherModule getApi() {
		return this.api;
	}

	public void setApi(final APILauncherModule api) {
		this.api = api;
	}

	public Map<AgentIdentifier, HostIdentifier> getLocations(){
		return this.api.locations;
	}

	/*
	 *
	 */

	public void initAPI(final boolean threaded) throws CompetenceException {
		this.api = new APILauncherModule(threaded);
		this.api.setMyAgent(this);
	}

	public  void initAPI(final int nameServer_port, final int server_port) throws CompetenceException {
		this.api = new APILauncherModule(nameServer_port, server_port);
		this.api.setMyAgent(this);
	}

	public  void initAPI(final String machinesFile)
			throws JDOMException,IOException, CompetenceException {
		this.api = new APILauncherModule(machinesFile);
		this.api.setMyAgent(this);
	}
	public  void initAPI(final File machinesFile)
			throws JDOMException,IOException, CompetenceException {
		this.api = new APILauncherModule(machinesFile);
		this.api.setMyAgent(this);
	}

	/*
	 *
	 */

	public void launchMySelf(){
		this.api.init();
	}

	public void launch(final Collection<LaunchableComponent> ags, final Map<AgentIdentifier, HostIdentifier> locations) {
		for (final LaunchableComponent c : ags) {
			c.launchWith(this.api, locations.get(c.getIdentifier()));
		}
	}

	public  void launch(final Collection<? extends LaunchableComponent> ags) {
		for (final LaunchableComponent c : ags) {
			c.launchWith(this.api);
		}
	}

	public  void launch(final LaunchableComponent ag) {
		ag.launchWith(this.api);
	}

	public  void launch(final LaunchableComponent ag, final HostIdentifier h) {
		ag.launchWith(this.api,h);
	}

	//

	public static void launch(final APILauncherModule api, final Map<? extends LaunchableComponent, HostIdentifier> locations) {
		for (final LaunchableComponent c : locations.keySet()) {
			c.launchWith(api, locations.get(c));
		}
	}

	public static void launch(final APILauncherModule api, final Collection<? extends LaunchableComponent> ags) {
		for (final LaunchableComponent c : ags) {
			c.launchWith(api);
		}
	}
	/*
	 *
	 */

	public void startApplication() {
		this.api.startApplication();
	}

	public void startActivity(final LaunchableComponent ag) {
		this.api.startActivity(ag);
	}


	public void startActivities(final Collection<? extends LaunchableComponent> ags){
		this.api.start(ags);
	}

	//

	public static void startActivities(final APILauncherModule api, final Collection<? extends LaunchableComponent> ags){
		api.start(ags);
	}

	public static void startActivity(final APILauncherModule api, final LaunchableComponent ag){
		api.startActivity(ag);
	}

	public static void startActivities(final APILauncherModule api) {
		api.startApplication();
	}

	//
	// Subclasses
	//

	public enum LaunchType { NotThreaded, FIPA, DarX }

	public class APILauncherModule extends BasicAgentModule<BasicCompetentAgent> {
		private static final long serialVersionUID = 7241441256737644000L;


		public static final String _logKeyForAPIManagement = "log key for api start";


		private final Map<AgentIdentifier, LaunchableComponent> registeredAgent =
				new HashMap<AgentIdentifier, LaunchableComponent>();
		Map<AgentIdentifier, HostIdentifier> locations =
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
		private APILauncherModule(final boolean threaded) throws CompetenceException {
			if (threaded) {
				this.initWithFipa();
			} else {
				this.initNotThreaded();
			}
		}

		/**
		 * lancement avec darx en local
		 * @param nameServer_port
		 * @param server_port
		 * @throws CompetenceException
		 */
		private APILauncherModule(final int nameServer_port, final int server_port) throws CompetenceException {
			this.initLocalDarx(nameServer_port, server_port);
		}

		/**
		 * lancement avec darx en utilisant toutes les machines disponibles
		 * @param machinesFile
		 * @throws CompetenceException
		 * @throws JDOMException
		 * @throws IOException
		 */
		private APILauncherModule(
				final String machinesFile)
						throws CompetenceException, JDOMException,IOException {
			final DimaXDeploymentScript machines = new DimaXDeploymentScript(machinesFile);
			this.initDeployedDarx(machines);
		}
		private APILauncherModule(
				final File machinesFile)
						throws CompetenceException, JDOMException,IOException {
			final DimaXDeploymentScript machines = new DimaXDeploymentScript(machinesFile);
			this.initDeployedDarx(machines);
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
			return this.avalaibleHosts;
		}

		public Collection<AgentIdentifier> getAgentsRunningOn(final HostIdentifier h){
			final Collection<AgentIdentifier> result =
					new ArrayList<AgentIdentifier>();
			for (final AgentIdentifier a : this.locations.keySet()) {
				if (this.locations.get(a).equals(h)) {
					result.add(a);
				}
			}
			return result;
		}

		public Collection<AgentIdentifier> getAllAgents(){
			return this.locations.keySet();
		}
		//
		// Launch method
		//

		void init() {
			this.getMyAgent().launchWith(this);
			this.startActivity(this.getMyAgent());
			this.registeredAgent.remove(this.getMyAgent());

			if (this.myLaunchType.equals(LaunchType.NotThreaded)){
				this.scheduler.runApplication();
				this.scheduler=null;
			}
		}

		boolean launch(final BasicCompetentAgent c, final HostIdentifier machine){
			if (!this.getAvalaibleHosts().contains(machine)) {
				throw new RuntimeException("i can not use this machine "+machine+" available machines are "+this.getAvalaibleHosts());
			}

			c.addObserver(this.getMyAgentIdentifier(), LogService.logNotificationKey);
			c.addObserver(this.getMyAgentIdentifier(), EndLiveMessage.class);


			switch (this.myLaunchType) {
			case NotThreaded:
				this.scheduler.add(c);
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

			this.registeredAgent.put(c.getIdentifier(), c);
			this.locations.put(c.getIdentifier(), machine);

			return true;
		}

		boolean launch(final BasicCompetentAgent c){
			if (this.pos==this.avalaibleHosts.size()) {
				this.pos=0;
			}

			return this.launch(c, this.avalaibleHosts.get(this.pos));
		}

		Collection<AgentIdentifier> killed = new ArrayList<AgentIdentifier>();
		public boolean kill(final AgentIdentifier c){
			final boolean removed1 = this.registeredAgent.remove(c)!=null;
			final boolean removed2 =  this.locations.remove(c)!=null;
			this.killed.add(c);

			assert removed1 && removed2:c+" \n REGISTERD \n "+this.registeredAgent+" \n LOCATIONS \n "+this.locations;

			APIAgent.this.sendMessage(c, new SigKillOrder());
			return true;
		}

		public boolean kill(final Collection<AgentIdentifier> cs){
			for (final AgentIdentifier c : cs) {
				this.kill(c);
			}
			return true;
		}

		public boolean destroy(final BasicCompetentAgent c){
			final boolean removed1 = this.registeredAgent.remove(c.getIdentifier())!=null;
			final boolean removed2 =  this.locations.remove(c.getIdentifier())!=null;

			//			logMonologue("Agent destroyed : "+c);

			assert this.killed.contains(c) || removed1 && removed2:c+" \n REGISTERD \n "+this.registeredAgent+" \n LOCATIONS \n "+this.locations;

			switch (this.myLaunchType) {
			case NotThreaded:
				this.scheduler.remove(c);
				break;
			case FIPA:
				try {
					Thread.sleep(500);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				AgentManagementSystem.getDIMAams().removeAquaintance(c);
				break;
			case DarX:
				c.darxEngine.terminateTask();
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
			this.start(this.registeredAgent.values());
		}

		void startActivity(final LaunchableComponent ag){
			final Collection<LaunchableComponent> ags= new ArrayList<LaunchableComponent>();
			ags.add(ag);
			this.start(ags);
		}

		@MessageHandler
		void end(final NotificationMessage<EndLiveMessage> m){
			this.getMyAgent().logMonologue(m.getSender()+" has ended activity ... nothing to do...",LogService.onBoth);
		}

		//
		// Primitive
		//

		private void initWithFipa(){
			this.myLaunchType = LaunchType.FIPA;
			AgentManagementSystem.initAMS();
			this.avalaibleHosts.add(HostIdentifier.getLocalHost());
		}

		private void initLocalDarx(final int nameServer_port, final int server_port)  {
			this.myLaunchType = LaunchType.DarX;
			final DimaXLocalLaunchScript darxLaunch = new DimaXLocalLaunchScript();
			darxLaunch.launchDARX(nameServer_port, server_port);
			this.avalaibleHosts.add(new HostIdentifier(LocalHost.getUrl(), server_port));
		}


		//routine
		private void initDeployedDarx(
				final DimaXDeploymentScript machines)
						throws JDOMException, IOException {
			this.myLaunchType = LaunchType.DarX;
			if (machines.getAllHosts().isEmpty()) {
				this.getMyAgent().signalException("no machines!!!");
			} else {
				machines.launchNameServer();
				machines.launchAllDarXServer();
			}
			this.avalaibleHosts.addAll(machines.getDarxServersIdentifier());
		}

		private void initNotThreaded(){
			this.myLaunchType = LaunchType.NotThreaded;
			this.scheduler = new LocalFipaScheduler();
			this.avalaibleHosts.add(HostIdentifier.getLocalHost());
		}

		void start(final Collection<? extends LaunchableComponent> ags){
			final StartActivityMessage m = new StartActivityMessage();
			for (final LaunchableComponent ag : ags)
			{
				if (this.myLaunchType.equals(LaunchType.NotThreaded)) {
					ag.start(m);
				} else {
					ag.start(m);
					this.getMyAgent().sendMessage(ag.getIdentifier(), m);
				}
				//						getMyAgent().logMonologue("Start order sended to "+ag.getIdentifier(),LogService.onBoth);//_logKeyForAPIManagement);
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
			boolean randomized = false;
			List<ProactiveComponentInterface> toInitialize = new ArrayList<ProactiveComponentInterface>();
			List<ProactiveComponentInterface> toExecute = new ArrayList<ProactiveComponentInterface>();
			List<ProactiveComponentInterface> toTerminate = new ArrayList<ProactiveComponentInterface>();


			/*
			 *
			 */
			private LocalFipaScheduler() {
				super();
				this.nbMaxStep = -1;
			}

			private LocalFipaScheduler(final int nbMaxStep) {
				super();
				this.nbMaxStep = nbMaxStep;
			}

			public void setRandomized(final boolean randomized) {
				this.randomized = randomized;
			}

			/*
			 *
			 */
			Collection<BasicCompetentAgent> toAdd = new ArrayList<BasicCompetentAgent>();
			private boolean add(final BasicCompetentAgent c){
				AgentManagementSystem.getDIMAams().addAquaintance(c);
				return this.toAdd.add(c);
			}

			Collection<BasicCompetentAgent> toRemove = new ArrayList<BasicCompetentAgent>();
			private void remove(final BasicCompetentAgent c){
				AgentManagementSystem.getDIMAams().removeAquaintance(c);
				this.toRemove.add(c);
			}
			/*
			 *
			 */

			public void runApplication(){
				int step = 0;
				//AJOUT DES NOUVEAUX AGENTS
				this.toInitialize.addAll(this.toAdd);
				this.toAdd.clear();

				while (!( this.toExecute.isEmpty() && this.toInitialize.isEmpty() || this.nbMaxStep!=-1 && step > this.nbMaxStep )){
					//LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");


					//AJOUT DES NOUVEAUX AGENTS
					this.toInitialize.addAll(this.toAdd);
					this.toAdd.clear();
					//RETRAIT DES AGENTS SUPPRIMME
					this.toInitialize.removeAll(this.toRemove);
					this.toExecute.removeAll(this.toRemove);
					this.toTerminate.removeAll(this.toRemove);


					//AGENT PRO ACTIVITY INITAILISATION
					LogService.flush();
					if (this.randomized) {
						Collections.shuffle(this.toInitialize);
					}
					for (final ProactiveComponentInterface c : this.toInitialize){
						c.proactivityInitialize();
						this.toExecute.add(c);
					}
					this.toInitialize.clear();

					//AGENT STEP ACTIVITIES
					LogService.flush();
					if (this.randomized) {
						Collections.shuffle(this.toExecute);
					}
					for (final ProactiveComponentInterface c : this.toExecute) {
						if (c.isAlive()){
							if (c.isActive()){
								c.preActivity();
								LogService.flush();
								c.step();
								LogService.flush();
								c.postActivity();
							} else {
								c.tryToResumeActivity();
							}
						} else {
							this.toTerminate.add(c);
						}
					}

					//AGENT PRO ACTIVITY TERMINATION
					LogService.flush();
					if (this.randomized) {
						Collections.shuffle(this.toTerminate);
					}
					for (final ProactiveComponentInterface c : this.toTerminate){
						c.proactivityTerminate();
						this.toExecute.remove(c);
						AgentManagementSystem.getDIMAams().removeAquaintance((BasicCommunicatingAgent) c);
					}
					this.toTerminate.clear();

					//LIFE IS GOOD : NEXT STEP
					step++;
				}

				LogService.flush();
				LogService.write("\n\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
			}
		}

	}


	//
	//
	//


	public class StartActivityMessage extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date startDate = new Date();

		public Date getStartDate() {
			return this.startDate;
		}
	}

	public class EndLiveMessage extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date endDate = new Date();

		public Date getEndDate() {
			return this.endDate;
		}
	}
	public class SigKillOrder extends Message{
		private static final long serialVersionUID = 5340852990030437060L;

		private final Date endDate = new Date();

		public Date getEndDate() {
			return this.endDate;
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