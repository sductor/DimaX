package examples.dcop.algo.topt;

import java.util.ArrayList;

import examples.dcop.daj.DCOPMessage;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Variable;



public class LocalConstraintMsg extends DCOPMessage {
	int id;
	int domain;
	int ttl;
	ArrayList<int[]> data;

	public LocalConstraintMsg() {
		super();
	}

	public LocalConstraintMsg(Variable v, int t) {
		super();
		id = v.id;
		domain = v.domain;
		data = new ArrayList<int[]>();
		for (Constraint c : v.neighbors) {
			data.add(c.encode());
		}
		ttl = t;
	}

	@Override
	public String getText() {
		return ("LOCAL " + id + ";TTL " + ttl);
	}

	public LocalConstraintMsg forward() {
		LocalConstraintMsg msg = new LocalConstraintMsg();
		msg.id = this.id;
		msg.domain = this.domain;
		msg.ttl = this.ttl - 1;
		msg.data = this.data;
		return msg;
	}

	@Override
	public int getSize() {
		int size = 0;
		for (int[] array : data)
			size += array.length * 4;
		return 13 + size;
	}
}
