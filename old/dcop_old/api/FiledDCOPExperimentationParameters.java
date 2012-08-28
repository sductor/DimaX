package examples.dcop_old.api;

import java.io.File;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.experimentation.ExperimentationParameters;
import examples.dcop_old.algo.Algorithm;

public class FiledDCOPExperimentationParameters extends ExperimentationParameters{

	String filename;
	int grouping;
	Algorithm algo;
	
	public FiledDCOPExperimentationParameters(AgentIdentifier experimentatorId,File resultPath,
			String filename, int grouping, String algo) {
		super(experimentatorId, resultPath);
		this.filename = filename;
		this.grouping = grouping;
		
		this.algo = Algorithm.KOPTAPO;
		if (algo.equalsIgnoreCase("TOPT"))
			this.algo = Algorithm.TOPTAPO;
		else if (algo.equalsIgnoreCase("KOriginal"))
			this.algo = Algorithm.KOPTORIG;
	}

	public String getFilename() {
		return filename;
	}

	public Algorithm getAlgo() {
		return algo;
	}

	public int getKort() {
		return grouping;
	}

	@Override
	public void initiateParameters() {
				
	}

	@Override
	public boolean isInitiated() {
		return true;
	}

}
