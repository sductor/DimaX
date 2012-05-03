package dimaxx.experimentation;

import java.util.LinkedList;

import dima.basicagentcomponents.AgentIdentifier;


// TODO utiliser des classes représentant chaque paramètre en particulier
// (nom, type, méthode s'appliquant à une collection de ExperimentationParameters)

public abstract class AutomatedExperimentationParameters<Agent extends Laborantin>
	extends ExperimentationParameters<Agent> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2799455313325959306L;
    
    public AutomatedExperimentationParameters(
	    final AgentIdentifier experimentatorId,
	    final String resultPath) {
	super(experimentatorId, resultPath);
    }

    @Override
    public LinkedList<ExperimentationParameters<Agent>> generateSimulation() {
	// TODO introspection avec les annotations.
    }
}
