package dimaxx.experimentation;

import java.io.File;

import dima.basicinterfaces.DimaComponentInterface;

import negotiation.faulttolerance.experimentation.ReplicationObservingGlobalService;

public interface ExperimentLogger extends DimaComponentInterface{

	public abstract void addAndWriteResults(ObservingGlobalService observingGlobalService, File f);

//	public abstract void write(File f);

}