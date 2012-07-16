package dima.introspectionbasedagents.services.core.deployment.server;



public interface ServerManager {

	public HostIdentifier getIdentifier();

	public void launch(String[] args);
}
