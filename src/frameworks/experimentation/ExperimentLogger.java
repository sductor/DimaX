package frameworks.experimentation;

import java.io.File;

import dima.basicinterfaces.DimaComponentInterface;

public interface ExperimentLogger extends DimaComponentInterface{

	public abstract void addAndWriteResults(ObservingGlobalService observingGlobalService, File f);

	//	public abstract void write(File f);

}