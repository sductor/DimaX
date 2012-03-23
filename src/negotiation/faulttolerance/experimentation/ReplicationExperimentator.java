package negotiation.faulttolerance.experimentation;

import java.io.IOException;


import org.jdom.JDOMException;

import dima.introspectionbasedagents.services.CompetenceException;
import framework.experimentation.Experimentator;

public class ReplicationExperimentator extends Experimentator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5800762843932232122L;

	public ReplicationExperimentator()
			throws CompetenceException {
		super(new ReplicationExperimentationProtocol());
	}

	public static void main(final String[] args) throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		final ReplicationExperimentator exp = new ReplicationExperimentator();
		exp.run(args);
	}
}
