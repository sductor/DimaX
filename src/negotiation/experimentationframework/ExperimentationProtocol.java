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


	public static final long _simulationTime = (1000 * 10);
	public static final long _state_snapshot_frequency = ExperimentationProtocol._simulationTime / 10;

	public static final int nbAgents = 7;
	public static final int nbHosts = 5;

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect = 1000;//500;//
	public static final long _initiatorPropositionFrequency = (long) (_timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;


	/**
	 * Clés statiques
	 */

	//Protocoles
	private final static String key4mirrorProto = "mirror protocol";
	private final static String key4CentralisedstatusProto = "Centralised status protocol";
	private final static String key4statusProto = "status protocol";
	private final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	private final static String key4greedySelect = "greedy select";
	private final static String key4rouletteWheelSelect = "roolette wheel select";
	private final static String key4AllocSelect = "alloc select";


	/*
	 *  Lancement
	 */

	public abstract LinkedList<ExperimentationParameters> generateSimulation();

	//Return new laborantin and update machines usage
	public abstract Laborantin createNewLaborantin(ExperimentationParameters p, APILauncherModule api)
			throws NotEnoughMachinesException, CompetenceException;

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
		return key4greedySelect;
	}

	public static String getKey4roulettewheelselect() {
		return key4rouletteWheelSelect;
	}

	public static String getKey4allocselect() {
		return key4AllocSelect;
	}

	public static String getKey4mirrorproto() {
		return key4mirrorProto;
	}

	public static String getKey4centralisedstatusproto() {
		return key4CentralisedstatusProto;
	}

	public static String getKey4statusproto() {
		return key4statusProto;
	}

	public static String getKey4multilatproto() {
		return key4multiLatProto;
	}
}
