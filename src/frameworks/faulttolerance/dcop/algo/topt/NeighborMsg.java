package frameworks.faulttolerance.dcop.algo.topt;

import frameworks.faulttolerance.dcop.daj.Message;
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;

public class NeighborMsg<Value> extends Message {
	int id;
	int ttl;
	int[] neighbors;

	public NeighborMsg() {
		super();
	}

	public NeighborMsg(AbstractVariable<Value> v, int t) {
		super();
		id = v.id;
		neighbors = new int[v.neighbors.size()];
		int i = 0;
		for (AbstractConstraint<Value> c : v.neighbors) {
			neighbors[i] = c.getNeighbor(v).id;
			i++;
		}
		ttl = t;
	}

	public String getText() {
		return ("NEIGHBOR " + id + ";TTL " + ttl);
	}

	public NeighborMsg<Value> forward() {
		NeighborMsg<Value> msg = new NeighborMsg<Value>();
		msg.id = this.id;
		msg.ttl = this.ttl - 1;
		msg.neighbors = this.neighbors;
		return msg;
	}

	public int getSize() {
		return 9 + neighbors.length * 4;
	}
}
