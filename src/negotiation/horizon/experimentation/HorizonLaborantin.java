package negotiation.horizon.experimentation;

import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;

public class HorizonLaborantin extends Laborantin {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 5398776856110049630L;

    public HorizonLaborantin(final HorizonExperimentationParameters p,
	    final APILauncherModule api) throws CompetenceException,
	    IfailedException, NotEnoughMachinesException {
	super(p, new HorizonObservingGlobalService(), api);
    }

}
