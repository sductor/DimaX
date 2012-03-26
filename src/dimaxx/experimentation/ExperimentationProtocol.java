package dimaxx.experimentation;

import java.lang.reflect.Field;
import java.util.LinkedList;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;

public abstract class ExperimentationProtocol implements DimaComponentInterface{
	private static final long serialVersionUID = -11300850940001517L;

	public static final long _simulationTime = 1000 * 10;

	/*
	 *  Lancement
	 */

	public abstract LinkedList<ExperimentationParameters> generateSimulation();

	//Return new laborantin and update machines usage
	public abstract Laborantin createNewLaborantin(ExperimentationParameters p, APILauncherModule api)
			throws NotEnoughMachinesException, CompetenceException, IfailedException;

	/*
	 * Déploiement
	 */

	public abstract Integer getMaxNumberOfAgentPerMachine(HostIdentifier id);


	/*
	 * Primitive
	 */

	public String getDescription() {
		try {
			String result = "**************\n";
			result += "Static parameters are :\n";
			for (final Field f : this.getClass()
					.getFields()) {
				result += f.getName() + " : "
						+ f.get(this)
						+ "\n";
			}
			result += "**************";
			return result;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

}
