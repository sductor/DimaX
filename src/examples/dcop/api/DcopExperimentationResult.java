package examples.dcop.api;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.SimpleExperimentationResults;

public class DcopExperimentationResult extends SimpleExperimentationResults {


	public int value;
	
	public DcopExperimentationResult(AgentIdentifier id,
			Date agentCreationTime, boolean lastInfo, int value) {
		super(id, agentCreationTime, lastInfo);
		this.value = value;
	}


}
