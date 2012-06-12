package dimaxx.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.APIAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.server.HostIdentifier;
/**
 * Laborantin manage the execution of an experience moddelled with its simulation parameters
 * it collects the results and write them
 *
 * @author Sylvain Ductor
 *
 */

public class Laborantin extends BasicCompetentAgent {
	private static final long serialVersionUID = -6358568153248160761L;




	//
	// Fields
	//


	APILauncherModule api;



	protected HashMap<AgentIdentifier, BasicCompetentAgent> agents =
			new HashMap<AgentIdentifier, BasicCompetentAgent>();
	private final Map<BasicCompetentAgent, HostIdentifier> locations;

	//	int numberOfAgentPerMAchine;
	private final ExperimentationParameters p;

	//
	// Competence
	//

	@Competence
	public ObservationService myInformationService = new SimpleObservationService();

	@Competence
	public ObservingGlobalService observingService;

	//
	// Constructor
	//

	public Laborantin(final ExperimentationParameters p, final ObservingGlobalService observingService, final APILauncherModule api)
			throws CompetenceException, IfailedException, NotEnoughMachinesException{
		super("Laborantin_of_"+p.getSimulationName());
		this.p = p;
		this.observingService=observingService;
		this.api=api;
		//		this.numberOfAgentPerMAchine=numberOfAgentPerMAchine;
		//		setLogKey(PatternObserverService._logKeyForObservation, true, false);

		observingService.setMyAgent(this);
		observingService.initiate();
		p.setMyAgent(this);

		this.logMonologue("launching :\n--> "+new Date().toString()+" simulation named : ******************     "+
				this.getSimulationParameters().getSimulationName()+"\n"+this.p,LogService.onBoth);//agents.values());

		p.initiateParameters();
		final Collection<? extends BasicCompetentAgent> ag = p.instanciateAgents();

		for (final BasicCompetentAgent a : ag){
			//					assert a.getCompetences().contains(ObservingSelfService.class);
			this.agents.put(a.getIdentifier(), a);
		}


		this.locations = this.generateLocations(
				this.api,
				this.agents.values());
		//		System.out.println(agents);
		//		System.out.println(api.getAvalaibleHosts());
		assert this.locations!=null;
	}

	public APILauncherModule getApi() {
		return api;
	}

	//
	// Behaviors
	//

	//
	@ProactivityInitialisation
	public void startSimu() {
		this.logMonologue("Those are my agents!!!!! :\n"+this.agents,LogService.onFile);

		this.observingService.setObservation();
		this.addObserver(this.p.experimentatorId, SimulationEndedMessage.class);

		APIAgent.launch(this.api,this.locations);
		this.wwait(1000);
		System.err.println("!!!!!!!!!!!!!!!!!!!!!STARTING!!!!!!!!!!!!!!!!!!!!!!!");
		APIAgent.startActivities(this.api, this.agents.values());
	}

	//
	// Implemented
	//

	public Map<BasicCompetentAgent, HostIdentifier> generateLocations(
			final APILauncherModule api,
			final Collection<BasicCompetentAgent> collection) throws NotEnoughMachinesException{
		final Map<BasicCompetentAgent, HostIdentifier> result = new Hashtable<BasicCompetentAgent, HostIdentifier>();
		final Map<HostIdentifier, Integer> hostsLoad = new Hashtable<HostIdentifier, Integer>();

		for (final HostIdentifier h : api.getAvalaibleHosts()) {
			if (api.getAgentsRunningOn(h).size()<this.p.getMaxNumberOfAgent(h)) {
				hostsLoad.put(h, api.getAgentsRunningOn(h).size());
			}
		}

		final Collection<HostIdentifier> hosts = new ArrayList<HostIdentifier>();
		hosts.addAll(hostsLoad.keySet());
		Iterator<HostIdentifier> itHosts = hosts.iterator();

		for (final BasicCompetentAgent id : collection) {
			if (hosts.isEmpty()) {
				throw new NotEnoughMachinesException();
			} else {
				if (!itHosts.hasNext()) {
					itHosts = hosts.iterator();
				}

				HostIdentifier host = itHosts.next();

				while (hostsLoad.get(host)>this.p.getMaxNumberOfAgent(host)){
					itHosts.remove();
					if (itHosts.hasNext()) {
						host = itHosts.next();
					} else {
						throw new NotEnoughMachinesException();
					}
				}

				assert host!=null:
					"wtfffffffffffffffffffffffffffffffff";
				result.put(id, host);
				hostsLoad.put(host, new Integer(hostsLoad.get(host)+1));
			}
		}
		return result;
	}


	@StepComposant()
	@Transient
	public boolean endSimulation(){
		if (this.observingService.simulationHasEnded()){
			this.logMonologue("I've finished!!",LogService.onBoth);
			this.logWarning("I've finished!!",LogService.onBoth);
			this.observingService.writeResult();
			this.wwait(10000);
			//				for (final ResourceIdentifier h : this.hostsStates4simulationResult.keySet())
			//					HostDisponibilityTrunk.remove(h);
			this.notify(new SimulationEndedMessage(observingService));
			this.sendNotificationNow();
			//				this.logMonologue("notifications Sended", onBoth);

			this.logMonologue("my job is done! cleaning my lab bench...",LogService.onBoth);
			this.logWarning("my job is done! cleaning my lab bench...",LogService.onBoth);
			this.agents.clear();
			this.agents=null;
			this.setAlive(false);
			return true;
		}
		return false;
	}



	//
	// Accessors
	//

	public Collection<BasicCompetentAgent> getAgents(){
		return this.agents.values();
	}
	public BasicCompetentAgent getAgent(final AgentIdentifier id){
		return this.agents.get(id);
	}

	public Collection<AgentIdentifier> getIdentifiers(){
		return this.agents.keySet();
	}
	public int getNbAgents() {
		return this.agents.size();
	}

	public ExperimentationParameters getSimulationParameters() {
		return this.p;
	}


	public ObservingGlobalService getObservingService() {
		return this.observingService;
	}



	//
	// Subclass
	//




	public class NotEnoughMachinesException extends Exception{
		private static final long serialVersionUID = -7238636027171768604L;}







}
