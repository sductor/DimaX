package vieux.dcopAmeliorer.api;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.experimentation.SimpleExperimentationResults;

public class DcopExperimentationResult extends SimpleExperimentationResults {


	/**
	 *
	 */
	private static final long serialVersionUID = -1453643761628989735L;
	public int value;

	public DcopExperimentationResult(final AgentIdentifier id,
			final Date agentCreationTime, final boolean lastInfo, final int value) {
		super(id, agentCreationTime, lastInfo);
		this.value = value;
	}


}
