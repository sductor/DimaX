package negotiation.experimentationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
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

public abstract class Laborantin extends BasicCompetentAgent {

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -6358568153248160761L;
	private final ExperimentationParameters p;
	int numberOfAgentPerMAchine;

	public static boolean initialisation;

	protected HashMap<AgentIdentifier, BasicCompetentAgent> agents =
			new HashMap<AgentIdentifier, BasicCompetentAgent>();
	Map<BasicCompetentAgent, HostIdentifier> locations;

	final Collection<AgentIdentifier> remainingAgent=new ArrayList<AgentIdentifier>();
	final Collection<AgentIdentifier> remainingHost=new ArrayList<AgentIdentifier>();

	APILauncherModule api;

	//
	// Competence
	//

	@Competence
	public ObservationService myInformationService;

	@Competence
	protected ObservingStatusService myStatusObserver;

	//
	// Constructor
	//

	public Laborantin(final ExperimentationParameters p, final APILauncherModule api, final int numberOfAgentPerMAchine)
			throws CompetenceException, IfailedException, NotEnoughMachinesException{
		super("Laborantin_of_"+p.getName());
		this.p = p;
		this.api=api;
		this.numberOfAgentPerMAchine=numberOfAgentPerMAchine;
		this.myStatusObserver= new ObservingStatusService(this, this.getSimulationParameters());
	}


	//To be called in subclass constructor
	protected void initialisation() throws CompetenceException, NotEnoughMachinesException, IfailedException{
		Laborantin.initialisation=true;
		//		setLogKey(PatternObserverService._logKeyForObservation, true, false);
		this.p.initiateParameters();
		this.logMonologue("Launching : \n"+this.p,LogService.onBoth);
		System.out.println("launching :\n--> "+new Date().toString()+" simulation named : ******************     "+
				this.getSimulationParameters().getName()+"\n"+this.p);//agents.values());

		int count = 5;
		boolean iFailed=false;
		do {
			iFailed=false;
			try {
				this.instanciate(this.p);
			} catch (final IfailedException e) {
				iFailed=true;
				this.logWarning("I'v faileeeeeddddddddddddd RETRYINNNGGGGG", LogService.onBoth);
				count--;
				if (count==0)
					throw e;
			}
		}while(iFailed && count > 0);



		for (final AgentIdentifier id : this.agents.keySet())
			if (id instanceof ResourceIdentifier)
				this.remainingHost.add(id);
				else
					this.remainingAgent.add(id);
				this.logMonologue("Those are my agents!!!!! :\n"+this.agents,LogService.onFile);
				//		this.agents.put(getIdentifier(), this);
				this.getGlobalObservingService().setObservation();
				this.addObserver(this.p.experimentatorId, SimulationEndedMessage.class);
				//		if (true)
				//		//			throw new RuntimeException();
				//				launch();
				//		throw new RuntimeException();
				this.locations = this.generateLocations(
						this.api,
						this.agents.values(),
						this.numberOfAgentPerMAchine);

				Laborantin.initialisation=false;
	}

	protected abstract ObservingGlobalService getGlobalObservingService();


	//
	@ProactivityInitialisation
	public void startSimu() throws CompetenceException{
		//		System.out.println(agents);
		//		System.out.println(api.getAvalaibleHosts());
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

		for (final HostIdentifier h : api.getAvalaibleHosts())
			if (api.getAgentsRunningOn(h).size()<nbMaxAgent)
				hostsLoad.put(h, api.getAgentsRunningOn(h).size());

				final Collection<HostIdentifier> hosts = new ArrayList<HostIdentifier>();
				hosts.addAll(hostsLoad.keySet());
				Iterator<HostIdentifier> itHosts = hosts.iterator();

				for (final BasicCompetentAgent id : collection)
					if (hosts.isEmpty())
						throw new NotEnoughMachinesException();
						else {
							if (!itHosts.hasNext())
								itHosts = hosts.iterator();

							HostIdentifier host = itHosts.next();

							while (hostsLoad.get(host)>nbMaxAgent){
								itHosts.remove();
								if (itHosts.hasNext())
									host = itHosts.next();
								else
									throw new NotEnoughMachinesException();
							}

							assert host!=null:
								"wtfffffffffffffffffffffffffffffffff";
							result.put(id, host);
							hostsLoad.put(host, new Integer(hostsLoad.get(host)+1));
						}
				return result;
	}



	//
	// Accessors
	//

	public BasicCompetentAgent getAgent(final AgentIdentifier id){
		return this.agents.get(id);
	}

	public void addAgent(final BasicCompetentAgent ag){
		this.agents.put(ag.getIdentifier(),ag);
	}


	public ExperimentationParameters getSimulationParameters() {
		return this.p;
	}

	public int getAliveAgentsNumber(){
		return this.remainingAgent.size();
	}

	//
	// Methods
	//

	protected abstract void instanciate(ExperimentationParameters p)
			throws IfailedException, CompetenceException;


	//
	// Behaviors
	//

	boolean endRequestSended= false;
	@StepComposant()
	@Transient
	public boolean endSimulation(){
		if (this.getUptime()>10*this.p.getMaxSimulationTime() && (this.remainingAgent.size()>0 || this.remainingHost.size()>0)){
			this.signalException("i should have end!!!!(rem ag, rem host)="
					+this.remainingAgent+","+this.remainingHost);
			for (final AgentIdentifier r : this.remainingHost)
				this.sendMessage(r, new SimulationEndedMessage());
					for (final AgentIdentifier r : this.remainingAgent)
						this.sendMessage(r, new SimulationEndedMessage());
							this.remainingAgent.clear();
							this.remainingHost.clear();
							return false;
		} else if (this.remainingAgent.size()<=0){
			//			this.logMonologue("Every agent has finished!!",onBoth);
			if (this.remainingHost.size()<=0){
				this.logMonologue("I've finished!!",LogService.onBoth);
				this.getGlobalObservingService().writeResult();
				this.wwait(10000);
				//				for (final ResourceIdentifier h : this.hostsStates4simulationResult.keySet())
				//					HostDisponibilityTrunk.remove(h);
				this.notify(new SimulationEndedMessage());
				this.sendNotificationNow();
				//				this.logMonologue("notifications Sended", onBoth);

				this.logMonologue("my job is done! cleaning my lab bench...",LogService.onBoth);
				this.agents.clear();
				this.agents=null;
				this.setAlive(false);

				return true;
			} else if (!this.endRequestSended){
				this.logMonologue("all agents lost! ending ..",LogService.onBoth);
				for (final ResourceIdentifier r : this.getSimulationParameters().getHostsIdentifier())
					this.sendMessage(r, new SimulationEndedMessage());
						this.endRequestSended=true;
						return false;
			} else
				return false;
		} else{
			this.observer.autoSendOfNotifications();
			return false;
		}
	}


	//
	// Subclass
	//

	public class NotEnoughMachinesException extends Exception{

		/**
		 *
		 */
		private static final long serialVersionUID = -7238636027171768604L;}

}
