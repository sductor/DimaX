package dimaxx.experimentation;

import java.io.File;

import negotiation.faulttolerance.experimentation.ReplicationObservingGlobalService;

public interface ExperimentLogger {

	public abstract void addAndWriteResults(ObservingGlobalService observingGlobalService, File f);

//	public abstract void write(File f);

}