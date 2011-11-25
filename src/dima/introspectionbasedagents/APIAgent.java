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
	
	public void setApi(APILauncherModule api) {
		this.api = api;
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

	public  void initAPI(String machinesFile) 
			throws JDOMException,IOException, CompetenceException {
		api = new APILauncherModule(machinesFile);
		api.setMyAgent(this);
	}
	public  void initAPI(File machinesFile) 
			throws JDOMException,IOException, CompetenceException {
		api = new APILauncherModule(machinesFile);
		api.setMyAgent(this);
	}

	/*
	 * 
	 */
	
	public void launchMySelf(){
		api.init();
	}

	public void launch(Collection<BasicCompetentAgent> ags, Map<AgentIdentifier, HostIdentifier> locations) {
		for (final BasicCompetentAgent c : ags)
			c.launchWith(api, locations.get(c.getIdentifier()));
	}

	public  void launch(Collection<BasicCompetentAgent> ags) {
		for (final BasicCompetentAgent c : ags)
			c.launchWith(api);
	}
	
	//

	public static void launch(APILauncherModule api, Collection<BasicCompetentAgent> ags, Map<AgentIdentifier, HostIdentifier> locations) {
		for (final BasicCompetentAgent c : ags){
//			assert locations.containsKey(c.getIdentifier()) : "id :"+c.getIdentifier()+"\n locations : "+locations;
			c.launchWith(api, locations.get(c.getIdentifier()));
		}
	}

	public static void launch(APILauncherModule api, Collection<BasicCompetentAgent> ags) {
		for (final BasicCompetentAgent c : ags)
			c.launchWith(api);
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


	public void startActivities(Collection<BasicCompetentAgent> ags){
		api.start(ags);		
	}

	//

	public static void startActivities(APILauncherModule api, Collection<BasicCompetentAgent> ags){
		api.start(ags);		
	}
	
	public static void startActivity(APILauncherModule api, BasicCompetentAgent ag){
		api.startActivity(ag);		
	}

	public static void startActivities(APILauncherModule api) {
		api.startApplication();
	}
}
