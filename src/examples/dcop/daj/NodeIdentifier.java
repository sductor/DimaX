package examples.dcop.daj;

import com.sun.org.apache.xpath.internal.operations.String;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;

public class NodeIdentifier extends AgentName {
	Integer id;
	
	public NodeIdentifier(Integer id) {
		super(id.toString());
		this.id=id;
	}

	public int asInt() {
		return id;
	}


}
