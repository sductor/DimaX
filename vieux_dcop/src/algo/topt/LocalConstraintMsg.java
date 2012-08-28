package vieux.src.algo.topt;

import java.util.ArrayList;

import vieux.src.daj.Message;
import vieux.src.dcop.Constraint;
import vieux.src.dcop.Variable;


public class LocalConstraintMsg extends Message {
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

	public int getSize() {
		int size = 0;
		for (int[] array : data)
			size += array.length * 4;
		return 13 + size;
	}
}
