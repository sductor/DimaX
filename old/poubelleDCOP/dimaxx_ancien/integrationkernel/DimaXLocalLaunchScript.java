package dimaxx.integrationkernel;

import darx.Darx;
import darx.NameServerImpl;
import dima.introspectionbasedagents.libraries.loggingactivity.LoggerManager;
import dimaxx.deploymentAndExecution.LocalHost;

public class DimaXLocalLaunchScript {

	Integer nameServerPort=null;


	private void launchNameServer(final Integer port) {
		this.nameServerPort = port;

		final String[] s = new String[2];
		s[0] = "-p";
		s[1] = port.toString();
		NameServerImpl.main(s);
	}

	private void launchDarXServer(final Integer port){
		if (this.nameServerPort!=null){
			final String[] s = new String[5];
			s[0] = "-ns";
			s[1] = LocalHost.getUrl();
			s[2] = this.nameServerPort.toString();
			s[3] = "-p";
			s[4] = port.toString();
			Darx.main(s);
		} else
			LoggerManager.writeException(this, "name server has not been initialized");
	}

	public void launchDARX(final Integer nameServer, final Integer... darxsString) {
		this.launchNameServer(nameServer);
		for (final Integer port : darxsString)
			this.launchDarXServer(port);
	}
}
