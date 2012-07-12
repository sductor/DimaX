package dima.introspectionbasedagents.services.launch;

import java.util.ArrayList;
import java.util.Collection;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ResumeActivity;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.launch.APIAgent.EndLiveMessage;
import dima.introspectionbasedagents.services.launch.APIAgent.StartActivityMessage;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.server.HostIdentifier;

public class ApiLaunchService extends BasicAgentCompetence<BasicCompetentAgent>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 612659273424027325L;

	APILauncherModule myApi;

	private boolean appliHasStarted=false;

	//
	// Constructor
	//

	public ApiLaunchService(final BasicCompetentAgent basicCompetentAgent) throws UnrespectedCompetenceSyntaxException {
		super(basicCompetentAgent);
	}

	//
	// Accessors
	//


	public boolean hasAppliStarted() {
		return this.appliHasStarted;
	}

	//
	// Primitives
	//

	public boolean launchWith(final APILauncherModule api){
		this.myApi=api;
		return api.launch(this.getMyAgent());
	}



	public boolean destroy(final BasicCompetentAgent c) {
		return this.myApi.destroy(c);
	}

	public boolean launchWith(final APILauncherModule api, final HostIdentifier h){
		this.myApi=api;
		return api.launch(this.getMyAgent(),h);
	}

	//
	// Behaviors
	//

	@ResumeActivity
	public final void apiActivityResuming(){
		System.out.println("yoooooooooooooooooooooooooooooooooooooooooooo");
		final Collection<AbstractMessage> messages = new ArrayList<AbstractMessage>();
		while (this.getMyAgent().getMailBox().hasMail()){
			final AbstractMessage m = this.getMyAgent().getMailBox().readMail();
			if (m instanceof StartActivityMessage) {
				this.start((StartActivityMessage)m);
			} else {
				messages.add(m);
			}
		}
		for (final AbstractMessage m : messages) {
			this.getMyAgent().getMailBox().writeMail(m);
		}
	}

	/*
	 * 
	 */

	@MessageHandler
	public boolean start(final StartActivityMessage m){
		this.appliHasStarted=true;
		this.getMyAgent().creation = m.getStartDate();
		//		this.logMonologue("Starting!!!! on "+ m.getStartDate().toLocaleString(),LogService.onFile);
		return true;
	}

	@MessageHandler
	public boolean endLive(final EndLiveMessage m){
		this.getMyAgent().setAlive(false);
		this.getMyAgent().setActive(false);
		this.myApi.destroy(this.getMyAgent());
		return true;
	}

	public boolean endLive(){
		this.getMyAgent().setAlive(false);
		this.getMyAgent().setActive(false);
		this.myApi.destroy(this.getMyAgent());
		return true;
	}
}
