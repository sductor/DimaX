package dimaxx.experimentation;

import java.util.Collection;
import java.util.LinkedList;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;

// TODO utiliser des classes représentant chaque paramètre en particulier
// (nom, type, méthode s'appliquant à une collection de ExperimentationParameters)

public abstract class AutomatedExperimentationParameters<Agent extends Laborantin>
	extends ExperimentationParameters<Agent> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2799455313325959306L;

    public AutomatedExperimentationParameters(
	    final AgentIdentifier experimentatorId, final String resultPath) {
	super(experimentatorId, resultPath);
    }

    @Override
    public LinkedList<ExperimentationParameters<Agent>> generateSimulation() {
	// TODO introspection avec les annotations.
    }

    public abstract class Parameter<Type> {
	private Type value;
	private DispersionSymbolicValue dispersion;

	public Parameter(final Type value,
		final DispersionSymbolicValue dispersion) {
	    this.value = value;
	    this.dispersion = dispersion;
	}

	public abstract Collection<AutomatedExperimentationParameters<Agent>> varyParameter(
		Collection<AutomatedExperimentationParameters<Agent>> exps);
    }
}
