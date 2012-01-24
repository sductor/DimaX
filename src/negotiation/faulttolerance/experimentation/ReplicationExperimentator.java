package negotiation.faulttolerance.experimentation;

import java.io.IOException;

import org.jdom.JDOMException;

import dima.introspectionbasedagents.services.CompetenceException;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.Experimentator;

public class ReplicationExperimentator extends Experimentator {
	

	public ReplicationExperimentator()
			throws CompetenceException {
		super(new ReplicationExperimentationProtocol());
	}

	public static void main(String[] args) throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		ReplicationExperimentator exp = new ReplicationExperimentator();
		exp.run(args);
	}
}
