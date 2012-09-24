package frameworks.faulttolerance.dcop.algo.topt;

import frameworks.faulttolerance.dcop.daj.Message;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;

public class NeighborMsg extends Message {
	int id;
	int ttl;
	int[] neighbors;

	public NeighborMsg() {
		super();
	}

	public NeighborMsg(ReplicationVariable v, int t) {
		super();
		id = v.id;
		neighbors = new int[v.getNeighbors().size()];
		int i = 0;
		for (MemFreeConstraint c : v.getNeighbors()) {
			neighbors[i] = c.getNeighbor(v).id;
			i++;
		}
		ttl = t;
	}

	public String getText() {
		return ("NEIGHBOR " + id + ";TTL " + ttl);
	}

	public NeighborMsg forward() {
		NeighborMsg msg = new NeighborMsg();
		msg.id = this.id;
		msg.ttl = this.ttl - 1;
		msg.neighbors = this.neighbors;
		return msg;
	}

	public int getSize() {
		return 9 + neighbors.length * 4;
	}
}
