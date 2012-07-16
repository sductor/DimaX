package dima.introspectionbasedagents.services.core.launch;

import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.introspectionbasedagents.services.core.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.services.core.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.core.launch.APIAgent.EndLiveMessage;
import dima.introspectionbasedagents.services.core.launch.APIAgent.StartActivityMessage;

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
