package negotiation.horizon.experimentation;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import negotiation.horizon.negociatingagent.SingleNodeParameters;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;

public class HorizonExperimentationParameters extends
	ExperimentationParameters<HorizonLaborantin> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -1044813274277565650L;

    protected final int nbVirtualNetworks, nbSubstrateNodes,
	    nbVirtualNodesperNetwork;

    private HorizonInstanceGraph hig;

    public HorizonExperimentationParameters(AgentIdentifier experimentatorId,
	    String protocolId, final int nbVirtualNetworks,
	    final int nbVirtualNodesperNetwork, final int nbSubstrateNodes,
	    final SingleNodeParameters hostCapacity,
	    final SingleNodeParameters virtualNodeCapacity) {
	super(experimentatorId, protocolId);
	this.nbSubstrateNodes = nbSubstrateNodes;
	this.nbVirtualNetworks = nbVirtualNetworks;
	this.nbVirtualNodesperNetwork = nbVirtualNodesperNetwork;
    }

    @Override
    public Laborantin createLaborantin(final APILauncherModule api)
	    throws CompetenceException, IfailedException,
	    NotEnoughMachinesException {
	final HorizonLaborantin l = new HorizonLaborantin(this, api);
	this.setMyAgent(l);
	return l;
    }

    @Override
    public LinkedList<ExperimentationParameters<HorizonLaborantin>> generateSimulation() {

	// new
	// File(LogService.getMyPath()+"result_"+getProtocolId()+"/").mkdirs();

	Collection<HorizonExperimentationParameters> simuToLaunch = new HashSet<HorizonExperimentationParameters>();
	simuToLaunch.add(HorizonExperimentationParameters
		.getDefaultParameters());
	return null;
    }

    static HorizonExperimentationParameters getDefaultParameters() {
	return new HorizonExperimentationParameters(new AgentName(
		"HorizonExperimentator"), getProtocolId(), 1, 3, 4,
		new SingleNodeParameters(10, 3), new SingleNodeParameters(2, 1));
    }

    @Override
    public Integer getMaxNumberOfAgent(HostIdentifier h) {
	return new Integer(this.nbSubstrateNodes + this.nbVirtualNetworks + 1);
    }

    @Override
    public void initiateParameters() throws IfailedException {
	this.hig = new HorizonInstanceGraph(this.getMyAgent(), this);

    }

    @Override
    protected Collection<? extends BasicCompetentAgent> instanciateAgents()
	    throws CompetenceException {
	// TODO Auto-generated method stub
	return null;
    }

}
