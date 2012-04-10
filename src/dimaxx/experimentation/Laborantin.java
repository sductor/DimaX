package dimaxx.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import negotiation.faulttolerance.candidaturewithstatus.Host;
import negotiation.faulttolerance.candidaturewithstatus.ObservingStatusService;
import negotiation.faulttolerance.candidaturewithstatus.Replica;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeHost;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeReplica;
import negotiation.faulttolerance.experimentation.ReplicationResultAgent;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.shells.APIAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ObservingSelfService.ActivityLog;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.mappedcollections.HashedHashSet;
/**
 * Laborantin manage the execution of an experience moddelled with its simulation parameters
 * it collects the results and write them
 *
 * @author Sylvain Ductor
 *
 */

public abstract class Laborantin extends BasicCompetentAgent {
	private static final long serialVersionUID = -6358568153248160761L;


	//
	// Fields
	//


	APILauncherModule api;
	protected HashMap<AgentIdentifier, BasicCompetentAgent> agents =
			new HashMap<AgentIdentifier, BasicCompetentAgent>();
	private Map<BasicCompetentAgent, HostIdentifier> locations;

	int numberOfAgentPerMAchine;

	//
	// Competence
	//

	@Competence
	public ObservationService myInformationService = new SimpleObservationService();

	@Competence
	public ObservingGlobalService observingService;

	@Competence
	private final ExperimentationParameters p;
	
	//
	// Constructor
	//

	public Laborantin(final ExperimentationParameters p, final APILauncherModule api, final int numberOfAgentPerMAchine)
			throws CompetenceException, IfailedException, NotEnoughMachinesException{
		super("Laborantin_of_"+p.getSimulationName());
		this.p = p;
		observingService=p.getGlobalObservingService();
		observingService.setMyAgent(this);
		this.api=api;
		this.numberOfAgentPerMAchine=numberOfAgentPerMAchine;
	}


	//
	@ProactivityInitialisation
	public void startSimu() throws CompetenceException, NotEnoughMachinesException, IfailedException{

		assert p.isInitiated();
		//		setLogKey(PatternObserverService._logKeyForObservation, true, false);
		this.logMonologue("Launching : \n"+this.p,LogService.onBoth);
		System.out.println("launching :\n--> "+new Date().toString()+" simulation named : ******************     "+
				this.getSimulationParameters().getSimulationName()+"\n"+this.p);//agents.values());

		int count = 5;
		boolean iFailed=false;
		do {
			iFailed=false;
			try {
				Collection<? extends BasicCompetentAgent> ag = p.instanciate();
				for (BasicCompetentAgent a : ag){
					assert a.getCompetences().contains(ObservingGlobalService.class);
					agents.put(a.getIdentifier(), a);
				}
			} catch (final IfailedException e) {
				iFailed=true;
				this.logWarning("I'v faileeeeeddddddddddddd RETRYINNNGGGGG", LogService.onBoth);
				count--;
				if (count==0) {
					throw e;
				}
			}
		}while(iFailed && count > 0);

		this.logMonologue("Those are my agents!!!!! :\n"+this.agents,LogService.onFile);
		//		this.agents.put(getIdentifier(), this);
		observingService.setObservation();
		this.addObserver(new AgentName(p.experimentatorId), SimulationEndedMessage.class);
		//		if (true)
		//		//			throw new RuntimeException();
		//				launch();
		//		throw new RuntimeException();
		this.locations = this.generateLocations(
				this.api,
				this.agents.values(),
				this.numberOfAgentPerMAchine);
		
		
		//		System.out.println(agents);
		//		System.out.println(api.getAvalaibleHosts());
		assert locations!=null;
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
			final Collection<BasicCompetentAgent> collection,
			final int nbMaxAgent) throws NotEnoughMachinesException{
		final Map<BasicCompetentAgent, HostIdentifier> result = new Hashtable<BasicCompetentAgent, HostIdentifier>();
		final Map<HostIdentifier, Integer> hostsLoad = new Hashtable<HostIdentifier, Integer>();

		for (final HostIdentifier h : api.getAvalaibleHosts()) {
			if (api.getAgentsRunningOn(h).size()<nbMaxAgent) {
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

				while (hostsLoad.get(host)>nbMaxAgent){
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
		if (observingService.simulationHasEnded()){
			this.logMonologue("I've finished!!",LogService.onBoth);
			observingService.writeResult();
			this.wwait(10000);
			//				for (final ResourceIdentifier h : this.hostsStates4simulationResult.keySet())
			//					HostDisponibilityTrunk.remove(h);
//			this.notify(new SimulationEndedMessage());
//			this.sendNotificationNow();
			//				this.logMonologue("notifications Sended", onBoth);

			this.logMonologue("my job is done! cleaning my lab bench...",LogService.onBoth);
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
		return agents.size();
	}

	public ExperimentationParameters getSimulationParameters() {
		return this.p;
	}

	//
	// Methods
	//

	/*
	 * Protocol
	 */
	
	public abstract LinkedList<ExperimentationParameters> generateSimulation(String[] protocoleArgs);

	/*
	 * Déploiement
	 */

	public abstract Integer getNumberOfAgentPerMachine();

	

	//
	// Subclass
	//

	public class NotEnoughMachinesException extends Exception{
		private static final long serialVersionUID = -7238636027171768604L;}
	



}
