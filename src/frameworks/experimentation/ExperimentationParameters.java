package frameworks.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.Laborantin.NotEnoughMachinesException;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.faulttolerance.experimentation.ReplicationLaborantin;


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
extends BasicAgentModule<Agent> implements Comparable{
	private static final long serialVersionUID = -1735965270944987539L;


	public long randSeed;




	public static boolean currentlyInstanciating;
	protected final File resultPath;
	final File finalResultPath;

//			public static final long _maxSimulationTime = 1000 * 10; //10 secondes
//			public static final long _maxSimulationTime = 1000 * 30; //30 secondes
//		public static final long _maxSimulationTime = 60000 * 1; //1 minute
//			public static final long _maxSimulationTime = 60000 * 5;//5 minutes
//	public static final long _maxSimulationTime = 60000 * 6;//5 minute s
//			public static final long _maxSimulationTime = 60000 * 10;//10 minutes
	//		public static final long _maxSimulationTime = 60000 * 15;//15 minutes
//	public static final long _maxSimulationTime = 60000 * 20;//20 minutes
//		public static final long _maxSimulationTime = 60000 * 30;//30 minutes
//		public static final long _maxSimulationTime = 60000 * 45;//45 minutes
//			public static final long _maxSimulationTime = 60000 * 60;//60 minutes
			public static final long _maxSimulationTime = 60000 * 90;//90 minutes

	public static int nbPart=1;

	//			public  long maxIndividualComputingTime = 60000;//1 min
	//	public  long maxIndividualComputingTime = 120000;//2 min
	//	public  long maxIndividualComputingTime = 3000;//30 sec
	public  long maxIndividualComputingTime = ExperimentationParameters._maxSimulationTime/45;

	//
	// Fields
	//

	private final String simulationName = ExperimentationParameters.newName();
	protected final AgentIdentifier experimentatorId;



	public ExperimentationParameters(final AgentIdentifier experimentatorId, final String protocolId) {
		super();
		this.experimentatorId = experimentatorId;
		this.resultPath = new File(LogService.getMyPath()+"result_"+protocolId+"/"+this.getSimulationName());
		this.finalResultPath = new File(LogService.getMyPath()+"result_"+protocolId+"/FINAL");
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

	public void setSeed(final Long seed) {
		this.randSeed = seed;
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

	protected abstract Collection<? extends CompetentComponent> instanciateAgents() throws CompetenceException;

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
		return "simu_part"+ReplicationLaborantin.informativeParameterNumber+"_"+ReplicationExperimentationParameters.nbPart+"__#"+ExperimentationParameters.id;
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

	public abstract boolean isValid();



}

////Return new laborantin and update machines usage
//public abstract Laborantin createNewLaborantin(ExperimentationParameters p, APILauncherModule api)
//		throws NotEnoughMachinesException, CompetenceException, IfailedException;