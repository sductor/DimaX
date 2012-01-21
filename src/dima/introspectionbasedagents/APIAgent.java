package dima.introspectionbasedagents;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dimaxx.server.HostIdentifier;

public class APIAgent extends BasicCompetentAgent {


	/**
	 *
	 */
	private static final long serialVersionUID = 8785216532127504439L;
	private APILauncherModule api = null;

	/*
	 *
	 */

	public APIAgent(final AgentIdentifier newId)
			throws CompetenceException {
		super(newId);
	}
	public APIAgent(final String newId) throws CompetenceException {
		super(newId);
	}

	/*
	 *
	 */

	public APILauncherModule getApi() {
		return this.api;
	}

	public void setApi(final APILauncherModule api) {
		this.api = api;
	}

	public Map<AgentIdentifier, HostIdentifier> getLocations(){
		return this.api.locations;
	}

	/*
	 *
	 */

	public void initAPI(final boolean threaded) throws CompetenceException {
		this.api = new APILauncherModule(threaded);
		this.api.setMyAgent(this);
	}

	public  void initAPI(final int nameServer_port, final int server_port) throws CompetenceException {
		this.api = new APILauncherModule(nameServer_port, server_port);
		this.api.setMyAgent(this);
	}

	public  void initAPI(final String machinesFile)
			throws JDOMException,IOException, CompetenceException {
		this.api = new APILauncherModule(machinesFile);
		this.api.setMyAgent(this);
	}
	public  void initAPI(final File machinesFile)
			throws JDOMException,IOException, CompetenceException {
		this.api = new APILauncherModule(machinesFile);
		this.api.setMyAgent(this);
	}

	/*
	 *
	 */

	public void launchMySelf(){
		this.api.init();
	}

	public void launch(final Collection<BasicCompetentAgent> ags, final Map<AgentIdentifier, HostIdentifier> locations) {
		for (final BasicCompetentAgent c : ags)
			c.launchWith(this.api, locations.get(c.getIdentifier()));
	}

	public  void launch(final Collection<BasicCompetentAgent> ags) {
		for (final BasicCompetentAgent c : ags)
			c.launchWith(this.api);
	}

	//

	public static void launch(final APILauncherModule api, final Map<BasicCompetentAgent, HostIdentifier> locations) {
		for (final BasicCompetentAgent c : locations.keySet())
			c.launchWith(api, locations.get(c));
	}

	public static void launch(final APILauncherModule api, final Collection<BasicCompetentAgent> ags) {
		for (final BasicCompetentAgent c : ags)
			c.launchWith(api);
	}
	/*
	 *
	 */

	public void startApplication() {
		this.api.startApplication();
	}

	public void startActivity(final BasicCompetentAgent ag) {
		this.api.startActivity(ag);
	}


	public void startActivities(final Collection<BasicCompetentAgent> ags){
		this.api.start(ags);
	}

	//

	public static void startActivities(final APILauncherModule api, final Collection<BasicCompetentAgent> ags){
		api.start(ags);
	}

	public static void startActivity(final APILauncherModule api, final BasicCompetentAgent ag){
		api.startActivity(ag);
	}

	public static void startActivities(final APILauncherModule api) {
		api.startApplication();
	}
}
