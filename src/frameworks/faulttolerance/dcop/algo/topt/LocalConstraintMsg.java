package frameworks.faulttolerance.dcop.algo.topt;

import java.util.ArrayList;

import frameworks.faulttolerance.dcop.daj.Message;
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;

public class LocalConstraintMsg<Value> extends Message {
	int id;
	int domain;
	int ttl;
	ArrayList<double[]> data;

	public LocalConstraintMsg() {
		super();
	}

	public LocalConstraintMsg(AbstractVariable<Value> v, int t) {
		super();
		id = v.id;
		domain = v.getDomain();
		data = new ArrayList<double[]>();
		for (AbstractConstraint<Value> c : v.neighbors) {
			data.add(c.encode());
		}
		ttl = t;
	}

	public String getText() {
		return ("LOCAL " + id + ";TTL " + ttl);
	}

	public LocalConstraintMsg<Value> forward() {
		LocalConstraintMsg<Value> msg = new LocalConstraintMsg<Value>();
		msg.id = this.id;
		msg.domain = this.domain;
		msg.ttl = this.ttl - 1;
		msg.data = this.data;
		return msg;
	}

	public int getSize() {
		int size = 0;
		for (double[] array : data)
			size += array.length * 4;
		return 13 + size;
	}
}
