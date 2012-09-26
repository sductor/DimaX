package frameworks.faulttolerance.dcop.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.daj.Message;
<<<<<<< HEAD
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.negotiation.rationality.AgentState;
>>>>>>> dcopX
=======
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.negotiation.rationality.AgentState;
>>>>>>> dcopX

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

<<<<<<< HEAD
<<<<<<< HEAD
	public LocalConstraintMsg(AbstractVariable v, int t) {
=======
	public LocalConstraintMsg(ReplicationVariable v, int t) {
>>>>>>> dcopX
=======
	public LocalConstraintMsg(ReplicationVariable v, int t) {
>>>>>>> dcopX
		super();
		id = v.id;
		domain = v.getDomain();
		data = new ArrayList<double[]>();
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractConstraint c : v.neighbors) {
=======
=======
>>>>>>> dcopX
		if (!DCOPFactory.isClassical())
			dataStates=new HashMap<Integer, AgentState>();
		state = v.getState();
		for (MemFreeConstraint c : v.getNeighbors()) {
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
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
