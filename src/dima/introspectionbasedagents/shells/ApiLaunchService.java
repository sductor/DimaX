package dima.introspectionbasedagents.shells;

import java.util.ArrayList;
import java.util.Collection;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.introspectionbasedagents.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ResumeActivity;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.shells.APIAgent.EndLiveMessage;
import dima.introspectionbasedagents.shells.APIAgent.SigKillOrder;
import dima.introspectionbasedagents.shells.APIAgent.StartActivityMessage;
import dimaxx.server.HostIdentifier;

public class ApiLaunchService extends BasicAgentCompetence<BasicCompetentAgent>{

	APILauncherModule myApi;

	private boolean appliHasStarted=false;

	//
	// Constructor
	//
	
	public ApiLaunchService(BasicCompetentAgent basicCompetentAgent) throws UnrespectedCompetenceSyntaxException {
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
		return api.launch(getMyAgent());
	}

	
	
	public boolean destroy(BasicCompetentAgent c) {
		return myApi.destroy(c);
	}

	public boolean launchWith(final APILauncherModule api, final HostIdentifier h){
		this.myApi=api;
		return api.launch(getMyAgent(),h);
	}
	
	//
	// Behaviors
	//
	
	@ResumeActivity
	public final void apiActivityResuming(){
		System.out.println("yoooooooooooooooooooooooooooooooooooooooooooo");
		final Collection<AbstractMessage> messages = new ArrayList<AbstractMessage>();
		while (getMyAgent().getMailBox().hasMail()){
			final AbstractMessage m = getMyAgent().getMailBox().readMail();
			if (m instanceof StartActivityMessage) {
				this.start((StartActivityMessage)m);
			} else {
				messages.add(m);
			}
		}
		for (final AbstractMessage m : messages) {
			getMyAgent().getMailBox().writeMail(m);
		}
	}

	/*
	 * 
	 */
	
	@MessageHandler
	public boolean start(final StartActivityMessage m){
		appliHasStarted=true;
		getMyAgent().creation = m.getStartDate();
		//		this.logMonologue("Starting!!!! on "+ m.getStartDate().toLocaleString(),LogService.onFile);
		return true;
	}
	
	@MessageHandler 
	public boolean endLive(final EndLiveMessage m){
		getMyAgent().setAlive(false);
		getMyAgent().setActive(false);
		 myApi.destroy(getMyAgent());
		return true;
	}	
	
	public boolean endLive(){
		getMyAgent().setAlive(false);
		getMyAgent().setActive(false);
		 myApi.destroy(getMyAgent());
		return true;
	}
}
