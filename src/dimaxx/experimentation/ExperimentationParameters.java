package dimaxx.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public abstract class ExperimentationParameters
implements DimaComponentInterface {
	private static final long serialVersionUID = -1735965270944987539L;

	private final String simulationName = ExperimentationParameters.newName();
	protected final AgentIdentifier experimentatorId;


	private final File resultPath;
	
	public ExperimentationParameters(AgentIdentifier experimentatorId, File resultPath ) {
		super();
		this.experimentatorId = experimentatorId;
		this.resultPath = resultPath;
	}

	public static boolean initialisation;
	public abstract  void initiateParameters();	
	public abstract boolean isInitiated();
	
	public File getResultPath() {
		return this.resultPath;
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

	public String getSimulationName() {
		return this.simulationName;
	}

}
