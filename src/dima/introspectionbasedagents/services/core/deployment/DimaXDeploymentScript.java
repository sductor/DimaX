package dima.introspectionbasedagents.services.core.deployment;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;

import darx.Darx;
import darx.NameServerImpl;
import dima.introspectionbasedagents.services.core.deployment.hosts.HostsPark;
import dima.introspectionbasedagents.services.core.deployment.hosts.RemoteHostExecutor;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;

public class DimaXDeploymentScript extends HostsPark{

	//
	// Constructor
	//

	public DimaXDeploymentScript(final String listeMachines) throws JDOMException, IOException{
		super(listeMachines);
		super.activateSelfDestruction();
		try{Thread.sleep(2000);}catch(final Exception e){};
	}

	public DimaXDeploymentScript(final File machineFile) throws JDOMException,
	IOException {
		super(machineFile);
		super.activateSelfDestruction();
		try{Thread.sleep(2000);}catch(final Exception e){};
	}

	//
	// Accessors
	//



	public Class<?> getNameServerClass() {
		return NameServerImpl.class;
	}

	public Class<?> getDarxServerClass() {
		return Darx.class;
	}

	//
	// Methods
	//

	public void launchNameServer() {
		//		Integer port=7777;
		//		final String[] s = new String[2];
		//		s[0] = "-p";
		//		s[1] = port.toString();
		//		NameServerImpl.main(s);;
		try {
			this.execute(this.getNameServer(), this.getNameServerClass(), this.getNameServerCommandArgs());
			try{Thread.sleep(2000);}catch(final Exception e){};
		} catch (final Exception e) {
			LogService.writeException(this, "Error while instanciating name server", e);
		}
	}

	public void launchDarXServer(final RemoteHostExecutor host){
		try {
			System.out.println("\n       ******** Launching "+host);
			host.executeWithJava(this.getDarxServerClass(), this.getDarxServerCommandArgs(host));
			try{Thread.sleep(2000);}catch(final Exception e){};
		} catch (final Exception e) {
			LogService.writeException(this, "Error while instanciating "+ host, e);
		}
	}

	public void launchAllDarXServer() {
		for (final RemoteHostExecutor host : this.getHosts()) {
			this.launchDarXServer(host);
		}
	}

	//
	// Primitives
	//

	private final String getNameServerCommandArgs(){
		return	"-p "+
				this.getNameServer().getPort().toString();

	}

	private final String getDarxServerCommandArgs(final RemoteHostExecutor host){
		return "-ns "+this.getNameServer().getUrl()+" "+this.getNameServer().getPort().toString()
				+" -p "+host.getPort().toString();
	}

}



//
///**
// * @param host
// * @param command
// * @throws JSchException
// * @throws WrongOSException
// * @throws ErrorOnProcessExecutionException
// */
//private void executeRemote(final HostIdentifier host, final String command)
//throws JSchException, ErrorOnProcessExecutionException,
//WrongOSException {
//	if (this.hostsParameters.containsKey(host))
//		this.executeRemote(this.hostsParameters.get(host), command);
//	else
//		LoggerManager.writeException(this, "unknow host " + host);
//}
//
//private void launchNameServer() {
//	if (this.myMachines.getNameServerIdentifier().getUrl().equals(
//			this.getIdentifier().getUrl()))
//		NameServerImpl.main(new String[] {
//				"-p",
//				this.myMachines.getNameServerIdentifier().getPortNumber()
//				.toString() });
//	else {
//		final String nameServerLaunchCommand = this.getExecutionCommand(
//				this.myMachines.getNameServerIdentifier(),
//				NameServerImpl.class, "-p "
//				+ this.myMachines.getNameServerIdentifier()
//				.getPortNumber());
//		try {
//			this.myMachines.executeRemote(this.myMachines
//					.getNameServerIdentifier(), nameServerLaunchCommand);
//		} catch (final Exception e) {
//			System.err.println("Impossible de démarrer le name server!!");
//			// System.exit(-1);
//			e.printStackTrace();
//		}
//	}
//}
//@TransientStepComposant()
//public boolean launchRemoteServers() {
//	if (this.currentStatus.equals(LaunchStatus.LaunchingRemoteServers)) {
//		System.out
//		.println("\n          *****   Demarrage de DarX sur les machines distantes  ("
//				+ this.myMachines.getAllHosts().size()
//				+ ") *****\n");
//
//		for (final HostIdentifier host : this.myMachines.getAllHosts()) {
//			this.observationService.notify(new LogMonologue(
//					"Creating.... : " + host));
//			this.createRemoteDimaXServer(host);
//			this.currentServerRequests++;
//		}
//
//		this.currentStatus = LaunchStatus.RemoteServersRequested;
//
//		if (this.currentServerRequests == 0)
//			this.currentStatus = LaunchStatus.InstantiatingHosts;
//
//		return true;
//	} else
//		return false;
//}
//private void createRemoteDimaXServer(final HostIdentifier host) {
//
//	final String commandArgs =
//
//	final String command = this.getExecutionCommand(host,
//			ServerManager.class, commandArgs);
//
//	if (!host.getUrl().equals(ServerManager.getServerIdentifier().getUrl()))
//		/** Machine distante */
//		try {
//			this.myMachines.executeRemote(host, command);
//		} catch (final Exception e) {
//			this.observationService.notify(new LogException(
//					"Impossible de démarrer sur " + host, e));
//			System.err.println("Impossible de démarrer sur " + host);
//			e.printStackTrace();
//		}
//		else if (!host.getPortNumber().equals(
//				ServerManager.getServerIdentifier().getPortNumber()))
//			/** Machine local sur un autre port */
//			try {
//				this.myMachines.execute(command);
//			} catch (final Exception e) {
//				LoggerManager.writeException(this, "Error while instanciating "
//						+ host, e);
//			}
//			else
//				this.observationService.notify(new LogException(host
//						+ " is my adress"));
//}