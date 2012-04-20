package examples.dcop_old.daj;

import dima.basicagentcomponents.AgentName;

public class NodeIdentifier extends AgentName {
	
	public NodeIdentifier(Integer i){
		super (i.toString());
	}

	public Integer getAsInt(){
		return new Integer(getId().toString());
	}
}
