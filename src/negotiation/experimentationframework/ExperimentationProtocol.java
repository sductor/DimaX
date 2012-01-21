package negotiation.experimentationframework;

import java.util.LinkedList;

import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.server.HostIdentifier;

public interface ExperimentationProtocol extends DimaComponentInterface{

	/*
	 *  Lancement
	 */

	public LinkedList<ExperimentationParameters> generateSimulation();

	//Return new laborantin and update machines usage
	public Laborantin createNewLaborantin(ExperimentationParameters p, APILauncherModule api)
	throws NotEnoughMachinesException, CompetenceException;

	/*
	 * Déploiement
	 */

	public Integer getMaxNumberOfAgentPerMachine(HostIdentifier id);


	/*
	 * Primitive
	 */

	public String getDescription();
}
