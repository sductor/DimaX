package dima.introspectionbasedagents.services.launch;

import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.introspectionbasedagents.services.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.launch.APIAgent.EndLiveMessage;
import dima.introspectionbasedagents.services.launch.APIAgent.StartActivityMessage;
import dimaxx.server.HostIdentifier;

public interface LaunchableComponent extends IdentifiedComponentInterface{

	/*
	 * Launch
	 */


	public boolean hasAppliStarted();

	boolean launchWith(final APILauncherModule api);

	boolean launchWith(final APILauncherModule api, final HostIdentifier h) ;
	
	boolean start(final StartActivityMessage m);

	boolean endLive(final EndLiveMessage m);

	boolean endLive();
}
