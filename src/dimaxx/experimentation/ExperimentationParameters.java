package dimaxx.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import sun.security.action.GetLongAction;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;


/**
 * Expérimentation parameters contient les paramaètres pour lancer une expériences.
 * Ces paramètres sont supposé être initialement représenté de façon légère en mémoire.
 * La méthode initiate génére un jeu de parametre fixe
 * La méthode instanciate crèè les agents à partir de ces paramètres
 *
 * generateSimulation, et createLAborantin font partie des méthodes de protocles :
 * elle permettent à l'expérimentator de lancer un ensemble d'expériences
 *
 * Les résultats sont transmis par la compétence ObservingSelfCompétence de chaque agent à la compétence ObservingGlobalCompetence
 *
 *
 * @author Sylvain Ductor
 *
 * @param <Agent> le type de laborantin associé
 */
public abstract class ExperimentationParameters<Agent extends Laborantin>
extends BasicAgentModule<Agent> {
	private static final long serialVersionUID = -1735965270944987539L;

	public static boolean currentlyInstanciating;
	final File resultPath;

//	public static final long _maxSimulationTime = 1000 * 10;
//	public static final long _maxSimulationTime = 1000 * 30;
	public static final long _maxSimulationTime = 60000 * 1;
	//
	// Fields
	//

	private final String simulationName = ExperimentationParameters.newName();
	protected final AgentIdentifier experimentatorId;



	public ExperimentationParameters(final AgentIdentifier experimentatorId, final String protocolId) {
		super();
		this.experimentatorId = experimentatorId;
		this.resultPath = new File(LogService.getMyPath()+"result_"+protocolId+"/"+getSimulationName());
	}

	//
	// Accessors
	//

	public String getSimulationName() {
		return this.simulationName;
	}

	public File getResultPath() {
		return this.resultPath;
	}

	//
	// Abstract Methods
	//

	/*
	 * Creation
	 */

	/**
	 * Generate parameters values
	 *
	 */
	public abstract  void initiateParameters() throws IfailedException;

	/**
	 * Instanciate the agents
	 *
	 * @throws IfailedException
	 * @throws CompetenceException
	 */

	protected abstract Collection<? extends BasicCompetentAgent> instanciateAgents() throws CompetenceException;

	/*
	 *
	 */
	//
	// Methods
	//

	/*
	 * Protocol
	 */

	//Le directory est crée à la main ici : voir replExpParam
	public abstract LinkedList<ExperimentationParameters<Agent>> generateSimulation();

	public abstract  Laborantin createLaborantin(final APILauncherModule api)throws CompetenceException, IfailedException,NotEnoughMachinesException;


	/*
	 * Déploiement
	 */

	public abstract Integer getMaxNumberOfAgent(HostIdentifier h);



	//
	// Primitive
	//

	private static int id = -1;
	private static String newName(){
		ExperimentationParameters.id++;
		return "simu_"+ExperimentationParameters.id;
	}

	@Override
	public String toString(){
		String result ="****************************************************************************************************************************\n";
		result+= "Simulation with parameters :\n";
		for (final Field f : this.getClass().getFields()) {
			try {
				result += f.getName()+" : "+f.get(this)+"\n";
			} catch (final Exception e) {
				LogService.writeException("immmmmmmmpppppppppoooooooossssssssiiiiiiiiiibbbbllllllllllle",e);
			}
		}
		result+="**************";
		return result;
	}


}

////Return new laborantin and update machines usage
//public abstract Laborantin createNewLaborantin(ExperimentationParameters p, APILauncherModule api)
//		throws NotEnoughMachinesException, CompetenceException, IfailedException;