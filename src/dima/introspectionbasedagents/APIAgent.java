package dima.introspectionbasedagents;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jdom.JDOMException;
import org.w3c.rdf.model.SetModel;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.server.HostIdentifier;

public class APIAgent extends BasicCompetentAgent {


	private APILauncherModule api = null;

	/*
	 * 
	 */
	
	public APIAgent(AgentIdentifier newId)
			throws CompetenceException {
		super(newId);
	}
	public APIAgent(String newId) throws CompetenceException {
		super(newId);
	}
	
	/*
	 * 
	 */

	public APILauncherModule getApi() {
		return api;
	}
	
	/*
	 * 
	 */
	
	public void initAPI(boolean threaded) throws CompetenceException {
		api = new APILauncherModule(threaded);
		api.setMyAgent(this);
	}

	public  void initAPI(int nameServer_port, int server_port) throws CompetenceException {
		api = new APILauncherModule(nameServer_port, server_port);
		api.setMyAgent(this);
	}

	public  void initAPI(File machinesFile) 
			throws JDOMException,IOException, CompetenceException {
		api = new APILauncherModule(machinesFile);
	}

	/*
	 * 
	 */
	
	public void launchMySelf(){
		api.init();
	}

	public void launch(Collection<BasicCompetentAgent> ags, Map<AgentIdentifier, HostIdentifier> locations) {
		api.launch(ags,locations);
	}

	public void launch(Collection<BasicCompetentAgent> ags) {
		api.launch(ags);
	}
	
	public boolean launch(BasicCompetentAgent c) {
		return api.launch(c);
	}

	/*
	 * 
	 */
	
	public void startApplication() {
		api.startApplication();
	}

	public void startActivity(BasicCompetentAgent ag) {
		api.startActivity(ag);
	}

}
