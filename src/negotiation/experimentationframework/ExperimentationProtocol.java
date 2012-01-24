package negotiation.experimentationframework;

import java.util.LinkedList;

import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.server.HostIdentifier;

public interface ExperimentationProtocol extends DimaComponentInterface{

	//
	// Simulation Configuration
	//

	public static final long _simulationTime = (long) (1000 * 5);
	public static final long _state_snapshot_frequency = _simulationTime / 20;

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
