package examples.dcop_old.api;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.jdom.JDOMException;

import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationProtocol;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;

public class DCOPExperimentator extends Experimentator  {

	public DCOPExperimentator(ExperimentationProtocol myProtocol)
			throws CompetenceException {
		super(myProtocol);
	}

	public static void main(final String[] args) throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		final DCOPExperimentator exp = new DCOPExperimentator(new DCOPExperimentationProtocol());
		exp.run(args);
	}

	public String getDescription(){
		return super.getDescription()+" simus of "
				+ExperimentationProtocol._simulationTime/1000.+
				"secs  on "+this.getApi().getAvalaibleHosts().size()+" machine";//+ReplicationExperimentationProtocol.nbSimuPerMAchine+" simu per machine"
	}
	
}
