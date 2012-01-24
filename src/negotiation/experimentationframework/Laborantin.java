package negotiation.experimentationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.APIAgent;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
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

	protected HashMap<AgentIdentifier, BasicCompetentAgent> agents =
			new HashMap<AgentIdentifier, BasicCompetentAgent>();
	Map<BasicCompetentAgent, HostIdentifier> locations;

	private final Collection<AgentIdentifier> remainingAgent=new ArrayList<AgentIdentifier>();
	private final Collection<AgentIdentifier> remainingHost=new ArrayList<AgentIdentifier>();

	APILauncherModule api;

	//
	// Constructor
	//

	public Laborantin(final ExperimentationParameters p, final APILauncherModule api, final int numberOfAgentPerMAchine)
			throws CompetenceException, IfailedException, NotEnoughMachinesException{
		super("Laborantin_of_"+p.getName());
		this.p = p;
		this.api=api;
		this.numberOfAgentPerMAchine=numberOfAgentPerMAchine;
		this.initialisation();
	}


	protected void initialisation()
			throws IfailedException, CompetenceException, NotEnoughMachinesException{
		//		setLogKey(PatternObserverService._logKeyForObservation, true, false);
		this.p.initiateParameters();
		this.logMonologue("Launching : \n"+this.p,LogService.onBoth);
		System.err.println("launching :\n--> "+new Date().toString()+" simulation named : ******************     "+
				this.getSimulationParameters().getName());//agents.values());

		this.instanciate(this.p);

		for (final AgentIdentifier id : this.agents.keySet())
			if (id instanceof ResourceIdentifier)
				this.remainingHost.add(id);
			else
				this.remainingAgent.add(id);
		this.logMonologue("Those are my agents!!!!! :\n"+this.agents,LogService.onFile);
		//		this.agents.put(getIdentifier(), this);
		this.setObservation();
		this.addObserver(this.p.experimentatorId, SimulationEndedMessage.class);
		//		if (true)
		//		//			throw new RuntimeException();
		//				launch();
		//		throw new RuntimeException();
		this.locations = this.generateLocations(
				this.api,
				this.agents.values(),
				this.numberOfAgentPerMAchine);
	}
	//
	@ProactivityInitialisation
	public void startSimu(){
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

	abstract protected void setObservation();

	protected abstract void updateAgentInfo(ExperimentationResults notification);

	protected abstract void updateHostInfo(ExperimentationResults notification);


	protected abstract void writeResult();

	//
	// Behaviors
	//


	@MessageHandler
	@NotificationEnvelope
	public final void receiveAgentInfo(final NotificationMessage<ExperimentationResults> n){
		this.updateInfo(n.getNotification());
	}

	//Pansement moche pour le surclassage de ExperiementationResults que java n'arrive pas a g��rer %��$��*��%!!!!
	protected void updateInfo(final ExperimentationResults r) {
		if (r.isHost())
			this.updateHostInfo(r);
		else
			this.updateAgentInfo(r);

		if (r.isLastInfo()){
			if (r.isHost())
				this.remainingHost.remove(r.getId());
			else
				this.remainingAgent.remove(r.getId());

			this.logMonologue(r.getId()
					+" has finished!, " +
					"\n * remaining agents "+this.remainingAgent.size()+
					"\n * remaining hosts "+this.remainingHost.size(),LogService.onFile);
		}
	}

	boolean endRequestSended= false;
	@StepComposant()
	@Transient
	public boolean endSimulation(){
		if (this.getUptime()>4*this.p.getMaxSimulationTime() && (this.remainingAgent.size()>0 || this.remainingHost.size()>0)){
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
				this.writeResult();
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
	// Writing Primitives
	//

	protected String getQuantilePointObs(
			final String entry,
			final Collection<Double> agent_values, final double significatifPercent, final int totalNumber){
		String result =
				entry+" min;\t "
						+entry+" firstTercile;\t "+entry+"  mediane;\t  "+entry+" lastTercile;\t "
						+entry+"  max ;\t "+entry+" sum ;\t "+entry+" mean ;\t percent of agent aggregated=\n";
		final HeavyDoubleAggregation variable = new HeavyDoubleAggregation();
		for (final Double d :  agent_values)
			variable.add(d);
				if (!variable.isEmpty() && variable.getNumberOfAggregatedElements()>(int) (significatifPercent*totalNumber))
					result += variable.getMinElement()+";\t " +
							variable.getQuantile(1,3)+";\t " +
							variable.getMediane()+";\t " +
							variable.getQuantile(2,3)+";\t " +
							variable.getMaxElement()+";\t " +
							variable.getSum()+";\t " +
							variable.getRepresentativeElement()+";\t " +
							(double) variable.getNumberOfAggregatedElements()/(double)  totalNumber+"\n";
				else
					result += "-;-;-;-;-;-  ("+(double)variable.getNumberOfAggregatedElements()/(double)  totalNumber+")\n";

				return result;
	}

	protected String getQuantileTimeEvolutionObs(final String entry,
			final HeavyDoubleAggregation[] variable, final double significatifPercent, final int totalNumber){
		String result =
				entry+" min;\t "
						+entry+" firstTercile;\t "+entry+"  mediane;\t  "+entry+" lastTercile;\t "
						+entry+"  max ;\t "+entry+" sum ;\t "+entry+" mean ;\t percent of agent aggregated=\n";
		for (int i = 0; i < this.p.numberOfTimePoints(); i++){
			result += this.p.geTime(i)/1000.+" ; ";
			if (!variable[i].isEmpty() && variable[i].getNumberOfAggregatedElements()>significatifPercent*totalNumber)
				result +=
				variable[i].getMinElement()+";\t " +
						variable[i].getQuantile(1,3)+";\t " +
						variable[i].getMediane()+";\t " +
						variable[i].getQuantile(2,3)+";\t " +
						variable[i].getMaxElement()+";\t " +
						variable[i].getSum()+";\t " +
						variable[i].getRepresentativeElement()+"; (" +
						(double) variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			else
				result += "-;-;-;-;-;-;  ("+(double)variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
		}
		return result;
	}

	protected String getMeanTimeEvolutionObs(final String entry, final LightAverageDoubleAggregation[] variable,
			final double significatifPercent, final int totalNumber){
		String result = "t (seconds);\t "+entry+" ;\t percent of agent aggregated=\n";
		for (int i = 0; i < this.p.numberOfTimePoints(); i++){
			result += this.p.geTime(i)/1000.+" ;\t ";
			if (variable[i].getNumberOfAggregatedElements()>significatifPercent*totalNumber)
				result+=variable[i].getRepresentativeElement()+";\t (" +
						(double) variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			else
				result += "-;\t ("+(double)variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
		}
		return result;
	}

	protected Double getPercent(final int value, int total){
		return (double) value/(double) total*100;
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
