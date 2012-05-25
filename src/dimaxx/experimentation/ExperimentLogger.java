package dimaxx.experimentation;

import java.io.File;

import negotiation.faulttolerance.experimentation.ReplicationObservingGlobalService;

public interface ExperimentLogger {

	public abstract void addResults(ObservingGlobalService observingGlobalService);

	public abstract void write(File f);

}