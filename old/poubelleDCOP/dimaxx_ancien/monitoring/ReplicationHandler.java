package dimaxx.monitoring;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.BasicAgentCompetence;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.competences.CompetentAgent;
import dimaxx.server.HostIdentifier;

public class ReplicationHandler extends  BasicAgentCompetence<CompetentAgent> {
	private static final long serialVersionUID = 5676505021652595874L;

	public ReplicationHandler(final BasicCompetentAgent ag) {
		super(ag);
		// TODO Auto-generated constructor stub
	}

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

//	public boolean replicate(HostIdentifier h){
//
//	}
//
//	public boolean destroy(HostIdentifier h){
//
//	}
}
