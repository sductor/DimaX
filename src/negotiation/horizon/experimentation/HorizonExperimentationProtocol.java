package negotiation.horizon.experimentation;

import java.util.LinkedList;

import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.server.HostIdentifier;
import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.IfailedException;
import negotiation.experimentationframework.Laborantin;
import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;

public class HorizonExperimentationProtocol extends ExperimentationProtocol {

    @Override
    public Laborantin createNewLaborantin(ExperimentationParameters p,
	    APILauncherModule api) throws NotEnoughMachinesException,
	    CompetenceException, IfailedException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public LinkedList<ExperimentationParameters> generateSimulation() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Integer getMaxNumberOfAgentPerMachine(HostIdentifier id) {
	// TODO Auto-generated method stub
	return null;
    }

}
