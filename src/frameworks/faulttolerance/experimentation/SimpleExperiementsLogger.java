package frameworks.faulttolerance.experimentation;

import java.io.File;

import dima.introspectionbasedagents.services.loggingactivity.LogService;

import frameworks.experimentation.ExperimentLogger;
import frameworks.experimentation.ObservingGlobalService;

public class SimpleExperiementsLogger implements ExperimentLogger{

	@Override
	public void addAndWriteResults(
			ObservingGlobalService observingGlobalService, File f) {
		LogService.logOnFile(f, ((ReplicationObservingGlobalService)observingGlobalService).getResult(), true, false);	
		
	}

}
