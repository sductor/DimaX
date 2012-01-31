package negotiation.experimentationframework;

import java.util.LinkedList;

import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.server.HostIdentifier;

public interface ExperimentationProtocol extends DimaComponentInterface{

	//
	// Configuration statique
	// /////////////////////////////////

	//
	// Simulation Configuration
	//


	public static final long _simulationTime = (1000 * 5);
	public static final long _state_snapshot_frequency = ExperimentationProtocol._simulationTime / 3;

	public static final int nbAgents = 5;
	public static final int nbHosts = 3;

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect = -1;//500;//
	public static final long _initiatorPropositionFrequency = -1;// (long) (_timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;


	/**
	 * Clés statiques
	 */

	//Protocoles
	final static String key4mirrorProto = "mirror protocol";
	final static String key4CentralisedstatusProto = "Centralised status protocol";
	final static String key4statusProto = "status protocol";
	final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	final static String key4greedySelect = "greedy select";
	final static String key4rouletteWheelSelect = "roolette wheel select";
	final static String key4AllocSelect = "alloc select";


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
