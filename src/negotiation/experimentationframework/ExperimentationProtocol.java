package negotiation.experimentationframework;

import java.lang.reflect.Field;
import java.util.LinkedList;

import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.server.HostIdentifier;

public abstract class ExperimentationProtocol implements DimaComponentInterface{

	//
	// Configuration statique
	// /////////////////////////////////

	//
	// Simulation Configuration
	//


	/**
	 * 
	 */
	private static final long serialVersionUID = -11300850940001517L;
	public static final long _simulationTime = (1000 * 20);
	public static final long _state_snapshot_frequency = ExperimentationProtocol._simulationTime / 10;

	public static final int startingNbAgents = 12;
	public static final int startingNbHosts = 6;

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect = 200;//500;//
	public static final long _initiatorPropositionFrequency = -1;//(long) (ExperimentationProtocol._timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;


	/**
	 * Clés statiques
	 */

	//Protocoles
	public final static String key4mirrorProto = "mirror protocol";
	private final static String key4CentralisedstatusProto = "Centralised status protocol";
	private final static String key4statusProto = "status protocol";
	private final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	private final static String key4greedySelect = "greedy select";
	private final static String key4rouletteWheelSelect = "roolette wheel select";
	public final static String key4AllocSelect = "alloc select";


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
			for (final Field f : ReplicationExperimentationProtocol.class
					.getFields())
				result += f.getName() + " : "
						+ f.get(ReplicationExperimentationProtocol.class)
						+ "\n";
					result += "**************";
					return result;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getKey4greedyselect() {
		return ExperimentationProtocol.key4greedySelect;
	}

	public static String getKey4roulettewheelselect() {
		return ExperimentationProtocol.key4rouletteWheelSelect;
	}

	public static String getKey4allocselect() {
		return ExperimentationProtocol.key4AllocSelect;
	}

	public static String getKey4mirrorproto() {
		return ExperimentationProtocol.key4mirrorProto;
	}

	public static String getKey4centralisedstatusproto() {
		return ExperimentationProtocol.key4CentralisedstatusProto;
	}

	public static String getKey4statusproto() {
		return ExperimentationProtocol.key4statusProto;
	}

	public static String getKey4multilatproto() {
		return ExperimentationProtocol.key4multiLatProto;
	}
}
