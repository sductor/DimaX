package frameworks.faulttolerance.experimentation;

import java.io.File;

import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.ExperimentLogger;
import frameworks.experimentation.ObservingGlobalService;

public class SimpleExperiementsLogger implements ExperimentLogger{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6044162255660088602L;

	@Override
	public void addAndWriteResults(
			final ObservingGlobalService observingGlobalService, final File f) {
		LogService.logOnFile(f, ((ReplicationObservingGlobalService)observingGlobalService).getResult(), true, false);

	}

}
