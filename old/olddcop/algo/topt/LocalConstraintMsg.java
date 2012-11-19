package frameworks.faulttolerance.olddcop.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;

import frameworks.faulttolerance.olddcop.DCOPFactory;
import frameworks.faulttolerance.olddcop.daj.Message;
import frameworks.faulttolerance.olddcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.olddcop.dcop.ReplicationVariable;
import frameworks.negotiation.rationality.AgentState;

public class LocalConstraintMsg extends Message {
	int id;
	public AgentState state;
	int domain;
	int ttl;
	ArrayList<double[]> data;
	HashMap<Integer,AgentState>dataStates;

	public LocalConstraintMsg() {
		super();
	}

	public LocalConstraintMsg(ReplicationVariable v, int t) {
		super();
		id = v.id;
		domain = v.getDomain();
		data = new ArrayList<double[]>();
		if (!DCOPFactory.isClassical())
			dataStates=new HashMap<Integer, AgentState>();
		state = v.getState();
		for (MemFreeConstraint c : v.getNeighbors()) {
			data.add(c.encode());
			if (!DCOPFactory.isClassical()){
				dataStates.put(c.getNeighbor(v).id, c.getNeighbor(v).getState());
			}
		}
		ttl = t;
	}

	public String getText() {
		return ("LOCAL " + id + ";TTL " + ttl);
	}

	public LocalConstraintMsg forward() {
		LocalConstraintMsg msg = new LocalConstraintMsg();
		msg.id = this.id;
		msg.state = this.state;
		msg.domain = this.domain;
		msg.ttl = this.ttl - 1;
		msg.data = this.data;
		msg.dataStates=this.dataStates;
		return msg;
	}

	public int getSize() {
		int size = 0;
		for (double[] array : data)
			size += array.length * 4;
		return 13 + size;
	}
}