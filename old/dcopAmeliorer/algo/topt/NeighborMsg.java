package examples.dcopAmeliorer.algo.topt;

import examples.dcopAmeliorer.daj.DcopMessage;
import examples.dcopAmeliorer.dcop.Constraint;
import examples.dcopAmeliorer.dcop.Variable;

public class NeighborMsg extends DcopMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = -212952221871390354L;
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
		return ("NEIGHBOR " + this.id + ";TTL " + this.ttl);
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
