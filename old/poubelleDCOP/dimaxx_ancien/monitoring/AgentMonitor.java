package dimaxx.monitoring;

import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.basicinterfaces.ProactiveComponentInterface;


public interface AgentMonitor extends ProactiveComponentInterface, IdentifiedComponentInterface {

	public void activate();
}
