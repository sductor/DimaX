package dima.introspectionbasedagents.services.core.darxkernel;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.core.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.shells.CompetentComponent;
import dima.support.GimaObject;

public abstract class ReplicationHandler<Agent extends CompetentComponent> extends GimaObject implements AgentCompetence<Agent> {

	/**
	 *
	 */
	private static final long serialVersionUID = -7218484427250695958L;
	private Collection<HostIdentifier> myReplicas;
	//Lance des exception si la rep a déja été faite

	public static void replicateOn(final HostIdentifier h) {
		// TODO Auto-generated method stub

	}

	public static void killReplicaOn(final HostIdentifier h) {
		// TODO Auto-generated method stub

	}

	public static void replicate(final AgentIdentifier r) {
		// TODO Auto-generated method stub

	}

	public static void killReplica(final AgentIdentifier r) {
		// TODO Auto-generated method stub

	}
}
