package negotiation.experimentationframework;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

//This class contains the datas that an agent send back to the laborantin
public interface ExperimentationResults extends DimaComponentInterface{

	public abstract AgentIdentifier getId();

	public abstract long getUptime();

	public abstract boolean isLastInfo();
	
	public abstract boolean isHost();

	public abstract void setLastInfo();
}
