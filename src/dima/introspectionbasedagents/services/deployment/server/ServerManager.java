package dima.introspectionbasedagents.services.deployment.server;



public interface ServerManager {

	public HostIdentifier getIdentifier();

	public void launch(String[] args);
}
