package dima.introspectionbasedagents.monitoring;

import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.basicinterfaces.ProactiveComponentInterface;


public interface AgentMonitor extends ProactiveComponentInterface, IdentifiedComponentInterface {

	public void activate();
}
