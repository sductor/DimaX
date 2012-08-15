package dima.introspectionbasedagents.services;

import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.kernel.CompetentComponent;

public interface AgentModule<Agent extends CompetentComponent> extends DimaComponentInterface {

	public abstract void setMyAgent(final Agent ag);

}