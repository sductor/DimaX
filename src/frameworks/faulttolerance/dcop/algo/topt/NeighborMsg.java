package frameworks.faulttolerance.dcop.algo.topt;

import frameworks.faulttolerance.dcop.daj.Message;
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
>>>>>>> dcopX

public class NeighborMsg extends Message {
	int id;
	int ttl;
	int[] neighbors;

	public NeighborMsg() {
		super();
	}

<<<<<<< HEAD
	public NeighborMsg(AbstractVariable v, int t) {
=======
	public NeighborMsg(ReplicationVariable v, int t) {
>>>>>>> dcopX
		super();
		id = v.id;
		neighbors = new int[v.getNeighbors().size()];
		int i = 0;
<<<<<<< HEAD
		for (AbstractConstraint c : v.neighbors) {
=======
		for (MemFreeConstraint c : v.getNeighbors()) {
>>>>>>> dcopX
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
