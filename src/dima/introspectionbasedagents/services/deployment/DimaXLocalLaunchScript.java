package dima.introspectionbasedagents.services.deployment;

import darx.Darx;
import darx.NameServerImpl;
import dima.introspectionbasedagents.services.deployment.hosts.LocalHost;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public class DimaXLocalLaunchScript {

	Integer nameServer=null;


	private void launchNameServer(final Integer port) {
		this.nameServer = port;

		final String[] s = new String[2];
		s[0] = "-p";
		s[1] = port.toString();
		NameServerImpl.main(s);
	}

	private void launchDarXServer(final Integer port){
		if (this.nameServer!=null){
			final String[] s = new String[5];
			s[0] = "-ns";
			s[1] = LocalHost.getUrl();
			s[2] = this.nameServer.toString();
			s[3] = "-p";
			s[4] = port.toString();
			Darx.main(s);
		} else {
			LogService.writeException(this, "name server has not been initialized");
		}
	}

	public void launchDARX(final Integer nameServer, final Integer... darxsString) {
		this.launchNameServer(nameServer);
		for (final Integer port : darxsString) {
			this.launchDarXServer(port);
		}
	}
}
