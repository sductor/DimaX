package negotiation.horizon.experimentation;

import java.io.IOException;

import negotiation.experimentationframework.Experimentator;

import org.jdom.JDOMException;

import dima.introspectionbasedagents.services.CompetenceException;

public class HorizonExperimentator extends Experimentator {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 4113274680875749664L;

    public HorizonExperimentator() throws CompetenceException {
	super(new HorizonExperimentationProtocol());
    }

    public static void main(final String[] args) throws CompetenceException,
	    IllegalArgumentException, IllegalAccessException, JDOMException,
	    IOException {
	final HorizonExperimentator exp = new HorizonExperimentator();
	exp.run(args);
    }
}
