package dimaxx.deploymentAndExecution;

import java.io.IOException;
import java.util.Collection;

import org.jdom.JDOMException;

public class NetworkExitingShutdownHook extends Thread{

	//
	// Fields
	//

	String destructionCommand = "killall -9 java; killall -9 java_vm";
	Collection<RemoteHostExecutor> hosts;

	//
	// Constructor
	//

	public NetworkExitingShutdownHook(final Collection<RemoteHostExecutor> hosts) {
		super();
		this.hosts = hosts;
	}

	public NetworkExitingShutdownHook(final String exitCommand,
			final Collection<RemoteHostExecutor> hosts) {
		super();
		this.destructionCommand = exitCommand;
		this.hosts = hosts;
	}

	//
	// Accessors
	//

	public Collection<RemoteHostExecutor> getAllHosts(){
		return this.hosts;
	}

	//
	// Methods
	//

	/**
	 * Self Destruction Using Thread hook
	 * Détruit les processus java lancés par ssh lorsque le systeme recoit le signal d'extinction
	 * @author Ductor Sylvain
	 */
	public void activate(){

		// Adding ShutdownHook : Stop automatically all the remote JVM at the end
		// of the application
		Runtime.getRuntime().addShutdownHook(this);
	}


	@Override
	public void run() {
		this.destroyAllMachines();
	};

	//
	// Main
	//

	/**
	 * Kills all the machines of the xml
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void main(final String[] args) throws JDOMException,
	IOException {
		new NetworkExitingShutdownHook(new DeploymentScript(args[0]).getAllHosts()).destroyAllMachines();;
	}

	//
	// Primitives
	//

	private void destroyAllMachines(){
		System.out.println("\n\n EXITING!!!!!");

		for (final RemoteHostExecutor h : this.getAllHosts())
			if (!h.getUrl().equals(LocalHost.getUrl()))
				try {
					System.out.println("\n *** Connecting "+h+" ...");
					h.execute(this.destructionCommand);
					System.out.println("   ---> Application on "+h+" successfully destroyed");
//					h.disconnect();
				} catch (final Exception e) {
					System.err.println("Application on "+h+" has not been destroyed");
					e.printStackTrace();
				}
	}
}
