package negotiation.experimentationframework;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public abstract class ExperimentationParameters
implements DimaComponentInterface {
	private static final long serialVersionUID = -1735965270944987539L;

	//	final AgentIdentifier experimentatorId;

	private final String simulationName = ExperimentationParameters.newName();
	private final File f;



	protected final AgentIdentifier experimentatorId;
	public int nbAgents;
	public int nbHosts;

	public String _usedProtocol;
	public  String _agentSelection;
	public  String _hostSelection;
	public String _socialWelfare;

	List<AgentIdentifier> replicasIdentifier  = new ArrayList<AgentIdentifier>();
	List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>();


	//
	// Constructor
	//

	public ExperimentationParameters(
			final File f,
			final AgentIdentifier experimentatorId,
			final int nbAgents, final int nbHosts) {
		this.f = f;
		this.experimentatorId=experimentatorId;
		this.nbAgents = nbAgents;
		this.nbHosts= nbHosts;
	}

	//
	// Accessors
	//


	public int getNbAgents() {
		return this.nbAgents;
	}

	public int getNbHosts() {
		return this.nbHosts;
	}

	public int getNbAgentsNHosts() {
		return this.nbAgents+this.nbHosts;
	}

	public List<AgentIdentifier> getReplicasIdentifier() {
		return this.replicasIdentifier;
	}

	public List<ResourceIdentifier> getHostsIdentifier() {
		return this.hostsIdentifier;
	}

	public static int getNumberOfTimePoints() {
		return (int) (ExperimentationProtocol._simulationTime / ExperimentationProtocol._state_snapshot_frequency);
	}

	public static int getTimeStep(final ExperimentationResults ag) {
		return Math
				.max(0,
						(int) (ag.getUptime() / ExperimentationProtocol._state_snapshot_frequency) - 1);
	}

	public Long geTime(final int i) {
		return (i + 1)
				* ExperimentationProtocol._state_snapshot_frequency;
	}

	public long getMaxSimulationTime() {
		return ExperimentationProtocol._simulationTime;
	}
	public String getName() {
		return this.getSimulationName();
	}

	//
	// Methods
	//

	public void initiate(){}

	public final void initiateParameters(){
		this.initiateAgentsAndHosts();
		this.initiate();
	}

	private void initiateAgentsAndHosts(){
		/*
		 * Agents and hosts names
		 */

		for (int i=0; i<this.nbAgents; i++)
			this.replicasIdentifier.add(//new AgentName("_--simu="+p.toString()+"--_DomainAgent_"+i));
					new AgentName("#"+this.getSimulationName()+"#DomainAgent_"+i));
		for (int i=0; i<this.nbHosts; i++)
			this.hostsIdentifier.add(//new ResourceIdentifier("_--simu="+p.toString()+"--_HostManager_"+i,77));
					new ResourceIdentifier("#"+this.getSimulationName()+"#HostManager_"+i,77));
	}


	/*
	 *
	 */

	/*
	 *
	 */


	//
	// Primitive
	//

	private static int id = -1;
	private static String newName(){
		ExperimentationParameters.id++;
		return "simu_"+ExperimentationParameters.id;
	}
	@Override
	public String toString(){
		String result ="****************************************************************************************************************************\n";
		result+= "Simulation with parameters :\n";
		for (final Field f : this.getClass().getFields())
			try {
				result += f.getName()+" : "+f.get(this)+"\n";
			} catch (final Exception e) {
				LogService.writeException("immmmmmmmpppppppppoooooooossssssssiiiiiiiiiibbbbllllllllllle",e);
			}
		result+="**************";
		return result;
	}

	public File getF() {
		return this.f;
	}

	public String getSimulationName() {
		return this.simulationName;
	}
}
