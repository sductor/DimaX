package vieux.dcopAmeliorer.algo.topt;

import java.util.ArrayList;

import vieux.dcopAmeliorer.daj.DcopMessage;
import vieux.dcopAmeliorer.dcop.Constraint;
import vieux.dcopAmeliorer.dcop.Variable;


public class LocalConstraintMsg extends DcopMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = -487123256063832097L;
	int id;
	int domain;
	int ttl;
	ArrayList<int[]> data;

	public LocalConstraintMsg() {
		super();
	}

	public LocalConstraintMsg(final Variable v, final int t) {
		super();
		this.id = v.id;
		this.domain = v.domain;
		this.data = new ArrayList<int[]>();
		for (final Constraint c : v.neighbors) {
			this.data.add(c.encode());
		}
		this.ttl = t;
	}

	@Override
	public String getText() {
		return "LOCAL " + this.id + ";TTL " + this.ttl;
	}

	public LocalConstraintMsg forward() {
		final LocalConstraintMsg msg = new LocalConstraintMsg();
		msg.id = this.id;
		msg.domain = this.domain;
		msg.ttl = this.ttl - 1;
		msg.data = this.data;
		return msg;
	}

	@Override
	public int getSize() {
		int size = 0;
		for (final int[] array : this.data) {
			size += array.length * 4;
		}
		return 13 + size;
	}
}
