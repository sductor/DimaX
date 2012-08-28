package vieux.src.algo.topt;

import vieux.src.daj.Message;
import vieux.src.dcop.Constraint;
import vieux.src.dcop.Variable;

public class NeighborMsg extends Message {
	int id;
	int ttl;
	int[] neighbors;

	public NeighborMsg() {
		super();
	}

	public NeighborMsg(Variable v, int t) {
		super();
		id = v.id;
		neighbors = new int[v.neighbors.size()];
		int i = 0;
		for (Constraint c : v.neighbors) {
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
