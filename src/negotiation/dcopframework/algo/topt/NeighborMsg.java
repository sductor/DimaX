package negotiation.dcopframework.algo.topt;

import negotiation.dcopframework.daj.Message;
import negotiation.dcopframework.dcop.Constraint;
import negotiation.dcopframework.dcop.Variable;

public class NeighborMsg extends Message {
	int id;
	int ttl;
	int[] neighbors;

	public NeighborMsg() {
		super();
	}

	public NeighborMsg(final Variable v, final int t) {
		super();
		this.id = v.id;
		this.neighbors = new int[v.neighbors.size()];
		int i = 0;
		for (final Constraint c : v.neighbors) {
			this.neighbors[i] = c.getNeighbor(v).id;
			i++;
		}
		this.ttl = t;
	}

	@Override
	public String getText() {
		return "NEIGHBOR " + this.id + ";TTL " + this.ttl;
	}

	public NeighborMsg forward() {
		final NeighborMsg msg = new NeighborMsg();
		msg.id = this.id;
		msg.ttl = this.ttl - 1;
		msg.neighbors = this.neighbors;
		return msg;
	}

	@Override
	public int getSize() {
		return 9 + this.neighbors.length * 4;
	}
}
