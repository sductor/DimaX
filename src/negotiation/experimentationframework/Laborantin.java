package negotiation.experimentationframework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.JDOMException;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.APIAgent;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.core.observingagent.PatternObserverService;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import static dima.introspectionbasedagents.services.core.loggingactivity.LogService.*;
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

	private final ExperimentationParameters p;

	protected HashMap<AgentIdentifier, BasicCompetentAgent> agents =
			new HashMap<AgentIdentifier, BasicCompetentAgent>();
	Map<AgentIdentifier, HostIdentifier> locations;

	private final Collection<AgentIdentifier> remainingAgent=new ArrayList<AgentIdentifier>();
	private final Collection<AgentIdentifier> remainingHost=new ArrayList<AgentIdentifier>();

	APILauncherModule api;

	//
	// Constructor
	//

	public Laborantin(final ExperimentationParameters p, APILauncherModule api)
			throws CompetenceException, IfailedException, NotEnoughMachinesException{
		super("Laborantin_of_"+p.getName());
		this.p = p;
		this.api=api;
		initialisation();		
	}


	protected void initialisation() 
			throws IfailedException, CompetenceException, NotEnoughMachinesException{
		//		setLogKey(PatternObserverService._logKeyForObservation, true, false);
		this.p.initiateParameters();
		this.logMonologue("Launching : \n"+this.p,onBoth);
		System.err.println("launching :\n--> "+new Date().toString()+" simulation named : ******************     "+
				this.getSimulationParameters().getName());//agents.values());

		this.instanciate(p);

		for (final AgentIdentifier id : this.agents.keySet()){
			if (id instanceof ResourceIdentifier){
				this.remainingHost.add(id);
			}else{
				this.remainingAgent.add(id);	
			}
		}
		this.logMonologue("Those are my agents!!!!! :\n"+this.agents,LogService.onFile);
		//		this.agents.put(getIdentifier(), this);
		setObservation();
		addObserver(p.experimentatorId, SimulationEndedMessage.class);
		//		if (true)
		//		//			throw new RuntimeException();
		//				launch();
		//		throw new RuntimeException();
		locations = generateLocations(
				api, 
				agents.values(), 
				Experimentator.myProtocol.getMaxNumberOfAgentPerMachine(null));
	}
	//
	@ProactivityInitialisation
	public void startSimu(){
		//		System.out.println(agents);
		//		System.out.println(api.getAvalaibleHosts());
		APIAgent.launch(api,agents.values(), locations);
		wwait(1000);
		System.err.println("!!!!!!!!!!!!!!!!!!!!!STARTING!!!!!!!!!!!!!!!!!!!!!!!");
		APIAgent.startActivities(api, agents.values());
	}

	//
	// Implemented
	//

	public Map<AgentIdentifier, HostIdentifier> generateLocations(
			APILauncherModule api, 
			Collection<BasicCompetentAgent> collection, 
			int nbMaxAgent) throws NotEnoughMachinesException{
		Map<AgentIdentifier, HostIdentifier> result = new HashMap<AgentIdentifier, HostIdentifier>();
		Map<HostIdentifier, Integer> hostsLoad = new HashMap<HostIdentifier, Integer>();

		for (HostIdentifier h : api.getAvalaibleHosts()){
			if (api.getAgentsRunningOn(h).size()<nbMaxAgent)
				hostsLoad.put(h, api.getAgentsRunningOn(h).size());
		}

		Collection<HostIdentifier> hosts = new ArrayList<HostIdentifier>();
		hosts.addAll(hostsLoad.keySet());
		Iterator<HostIdentifier> itHosts = hosts.iterator();

		for (BasicCompetentAgent id : collection){
			if (hosts.isEmpty())
				throw new NotEnoughMachinesException();

			if (!itHosts.hasNext())
				itHosts = hosts.iterator();
			HostIdentifier host = itHosts.next();

			if (hostsLoad.get(host)>nbMaxAgent){
				itHosts.remove();
			} else {
				assert host!=null:
					"wtfffffffffffffffffffffffffffffffff";
				result.put(id.getIdentifier(), host);
				hostsLoad.put(host, new Integer(hostsLoad.get(host)+1));
			}
		}
		return result;
	}



	//
	// Accessors
	//

	public BasicCompetentAgent getAgent(AgentIdentifier id){
		return this.agents.get(id);
	}

	public void addAgent(BasicCompetentAgent ag){
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
		updateInfo(n.getNotification());
	}

	//Pansement moche pour le surclassage de ExperiementationResults que java n'arrive pas a g��rer %��$��*��%!!!!
	protected void updateInfo(final ExperimentationResults r) {
		if (r.isHost())
			updateHostInfo(r);
		else
			updateAgentInfo(r);
		
		if (r.isLastInfo()){
			if (r.isHost())
				this.remainingHost.remove(r.getId());
			else
				this.remainingAgent.remove(r.getId());
			
			this.logMonologue(r.getId()
					+" has finished!, " +
					"\n * remaining agents "+this.remainingAgent.size()+
					"\n * remaining hosts "+this.remainingHost.size(),LogService.onScreen);
		}
	}

	boolean endRequestSended= false;
	@StepComposant()
	@Transient
	public boolean endSimulation(){
		 if (this.getUptime()>4*p.getMaxSimulationTime() && (this.remainingAgent.size()>0 || this.remainingHost.size()>0)){
			signalException("i should have end!!!!(rem ag, rem host)="
					+this.remainingAgent+","+this.remainingHost);
			for (final AgentIdentifier r : remainingHost){
				this.sendMessage(r, new SimulationEndedMessage());
			}
			for (final AgentIdentifier r : remainingAgent){
				this.sendMessage(r, new SimulationEndedMessage());
			}
			remainingAgent.clear();
			remainingHost.clear();
			return false;
		} else if (this.remainingAgent.size()<=0){
//			this.logMonologue("Every agent has finished!!",onBoth);
			if (this.remainingHost.size()<=0){
				this.logMonologue("I've finished!!",onBoth);
				this.writeResult();
				this.wwait(10000);
				//				for (final ResourceIdentifier h : this.hostsStates4simulationResult.keySet())
				//					HostDisponibilityTrunk.remove(h);
				this.notify(new SimulationEndedMessage());
				this.sendNotificationNow();
				//				this.logMonologue("notifications Sended", onBoth);

				logMonologue("my job is done! cleaning my lab bench...",onBoth);
				this.agents.clear();
				this.agents=null;
				this.setAlive(false);

				return true;
			} else if (!this.endRequestSended){
				this.logMonologue("all agents lost! ending ..",onBoth);
				for (final ResourceIdentifier r : this.getSimulationParameters().getHostsIdentifier()){
					this.sendMessage(r, new SimulationEndedMessage());
				}
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
				entry+" min; "
						+entry+" firstTercile; "+entry+"  mediane;  "+entry+" lastTercile; "
						+entry+"  max ; "+entry+" sum ; "+entry+" mean ; percent of agent aggregated=\n";
		final HeavyDoubleAggregation variable = new HeavyDoubleAggregation();
		for (Double d :  agent_values)
			variable.add(d);
				if (!variable.isEmpty() && variable.getNumberOfAggregatedElements()>(int) (significatifPercent*totalNumber))
					result += variable.getMinElement()+"; " +
							variable.getQuantile(1,3)+"; " +
							variable.getMediane()+"; " +
							variable.getQuantile(2,3)+"; " +
							variable.getMaxElement()+"; " +
							variable.getSum()+"; " +
							variable.getRepresentativeElement()+"; " +
							(double) variable.getNumberOfAggregatedElements()/(double)  totalNumber+"\n";
				else
					result += "-;-;-;-;-;-  ("+(double)variable.getNumberOfAggregatedElements()/(double)  totalNumber+")\n";

				return result;
	}

	protected String getQuantileTimeEvolutionObs(final String entry,
			final HeavyDoubleAggregation[] variable, final double significatifPercent, final int totalNumber){
		String result =
				entry+" min; "
						+entry+" firstTercile; "+entry+"  mediane;  "+entry+" lastTercile; "
						+entry+"  max ; "+entry+" sum ; "+entry+" mean ; percent of agent aggregated=\n";
		for (int i = 0; i < p.numberOfTimePoints(); i++){
			result += p.geTime(i)/1000.+" ; ";
			if (!variable[i].isEmpty() && variable[i].getNumberOfAggregatedElements()>significatifPercent*totalNumber)
				result +=
				variable[i].getMinElement()+"; " +
						variable[i].getQuantile(1,3)+"; " +
						variable[i].getMediane()+"; " +
						variable[i].getQuantile(2,3)+"; " +
						variable[i].getMaxElement()+"; " +
						variable[i].getSum()+"; " +
						variable[i].getRepresentativeElement()+"; (" +
						(double) variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			else
				result += "-;-;-;-;-;-;  ("+(double)variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
		}
		return result;
	}

	protected String getMeanTimeEvolutionObs(final String entry, final LightAverageDoubleAggregation[] variable,
			final double significatifPercent, final int totalNumber){
		String result = "t (seconds); "+entry+" ; percent of agent aggregated=\n";
		for (int i = 0; i < p.numberOfTimePoints(); i++){
			result += p.geTime(i)/1000.+" ; ";
			if (variable[i].getNumberOfAggregatedElements()>significatifPercent*totalNumber)
				result+=variable[i].getRepresentativeElement()+"; (" +
						(double) variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			else
				result += "-; ("+(double)variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
		}
		return result;
	}

	protected Double getAgentPercent(final int n){
		return (double) n/(double) this.getSimulationParameters().nbAgents*100;
	}

	//
	// Subclass
	//

	public class NotEnoughMachinesException extends Exception{}

}
