package negotiation.experimentationframework;

import java.util.LinkedList;
import negotiation.experimentationframework.MachineNetwork.NotEnoughMachinesException;

import dima.introspectionbasedagents.competences.CompetenceException;
import dimaxx.server.HostIdentifier;

public interface ExperimentationProtocol {

	/*
	 *  Lancement
	 */
	
	public LinkedList<ExperimentationParameters> generateSimulation();
	
	//Return new laborantin and update machines usage
	public Laborantin createNewLaborantin(ExperimentationParameters p, MachineNetwork  machines) 
	throws NotEnoughMachinesException, CompetenceException;
		
	/*
	 * Déploiement
	 */
		
	public int getMaxNumberOfAgentPerMachine(HostIdentifier id);
	
	public int getNumberOfMachinePerSimulation();
	
	/*
	 * Primitive
	 */
	
	public String getDescription();
}
