package examples.dcop_old.api;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationProtocol;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;

public class DCOPExperimentationProtocol extends ExperimentationProtocol {

	@Override
	public LinkedList<ExperimentationParameters> generateSimulation(String[] args) {
		LinkedList<ExperimentationParameters> expPs = new LinkedList<ExperimentationParameters>();
		expPs.add(new FiledDCOPExperimentationParameters(Experimentator.myId, new File("yo"), "conf/1.dcop", 3, "TOPT"));
		return expPs;
	}

	@Override
	public Laborantin createNewLaborantin(ExperimentationParameters p,
			APILauncherModule api) throws NotEnoughMachinesException,
			CompetenceException, IfailedException {
		return new DCOPLaborantin(p,api,Integer.MAX_VALUE);
	}

	@Override
	public Integer getMaxNumberOfAgentPerMachine(HostIdentifier id) {
		return Integer.MAX_VALUE;
	}
	
}