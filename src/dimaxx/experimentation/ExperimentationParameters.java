package dimaxx.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;

public abstract class ExperimentationParameters<Agent extends Laborantin>
extends BasicAgentModule<Agent> {
	private static final long serialVersionUID = -1735965270944987539L;

	public static final long _maxSimulationTime = 1000 * 10;
	public static boolean currentlyInstanciating;
	final String resultPath;
	//
	// Fields
	//
	
	private final String simulationName = ExperimentationParameters.newName();
	protected final String experimentatorId;


	
	public ExperimentationParameters(String experimentatorId, String resultPath) {
		super();
		this.experimentatorId = experimentatorId;
		this.resultPath = resultPath;
	}

	//
	// Accessors
	//

	public String getSimulationName() {
		return this.simulationName;
	}

	//
	// Abstract Methods
	//
	
	/*
	 * Creation
	 */

	/**
	 * Generate parameters values
	 */	
	public abstract  void initiateParameters();	
	public abstract boolean isInitiated();
	
	/**
	 * Instanciate the agents
	 * 
	 * @throws IfailedException
	 * @throws CompetenceException
	 */
	
	protected abstract Collection<? extends BasicCompetentAgent> instanciate() throws IfailedException, CompetenceException;
	
	//
	// Observation
	//
	

	protected ObservingGlobalService getGlobalObservingService(){
		return getMyAgent().observingService;
	}
	
	

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