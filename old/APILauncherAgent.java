package dima.introspectionbasedagents;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.server.HostIdentifier;

public class APILauncherAgent extends BasicCompetentAgent{

	private APILauncherModule myAPILauncher = new APILauncherModule(this);
	
	public APILauncherAgent(String newId, boolean threaded) throws CompetenceException {
		super(newId);
		if (threaded)
			myAPILauncher.initWithFipa();
		else 
			myAPILauncher.initNotThreaded();
	}

	public APILauncherAgent(AgentIdentifier newId, int nameServer_port, int server_port) throws CompetenceException {
		super(newId);
		myAPILauncher.initLocalDarx(nameServer_port, server_port);
	}

	public APILauncherAgent(AgentIdentifier newId, 
			File machinesFile) 
					throws CompetenceException, JDOMException,IOException {
		super(newId);
		myAPILauncher.initDeployedDarx(machinesFile);
	}
	
	public APILauncherAgent(AgentIdentifier newId, 
			File machinesFile,
			Collection<HostIdentifier> hosts) 
					throws CompetenceException, JDOMException,IOException {
		super(newId);
		myAPILauncher.initDeployedDarx(machinesFile, hosts);
	}
	
	public APILauncherAgent(AgentIdentifier newId, 
			File machinesFile,
			HashMap<AgentIdentifier, HostIdentifier> locations) 
					throws CompetenceException, JDOMException,IOException{
		super(newId);
		myAPILauncher.initDeployedDarx(machinesFile, locations);
	}

	/*
	 * 
	 */
	
	public void launch(Collection<BasicCompetentAgent> ags) {
		myAPILauncher.launch(ags);
	}

	/*
	 * 
	 */
	
	public void startAll() {
		myAPILauncher.startAll();
	}

	public void start(BasicCompetentAgent ag) {
		myAPILauncher.start(ag);
	}
	

}
