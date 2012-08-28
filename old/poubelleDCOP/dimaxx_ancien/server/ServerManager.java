package dimaxx.server;



public interface ServerManager {

	public HostIdentifier getIdentifier();

	public void launch(String[] args);
}
