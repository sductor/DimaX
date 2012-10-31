package frameworks.faulttolerance.olddcop.algo.topt;

import frameworks.faulttolerance.olddcop.daj.Message;
import frameworks.faulttolerance.olddcop.dcop.ReplicationVariable;

public class ValueMsg extends Message {
	int id;
	int value;
	int ttl;

	public ValueMsg() {
		super();
	}

	public ValueMsg(ReplicationVariable v, int t) {
		super();
		assert value!=-1;
		id = v.id;
		value = v.getValue();
		// if (value == -1)
		// System.out.println("Surprise");
		ttl = t;
	}

	public String getText() {
		return ("ID " + id + ";VALUE " + value + ";TTL " + ttl);
	}

	public ValueMsg forward() {
		ValueMsg msg = new ValueMsg();
		msg.id = this.id;
		msg.value = this.value;
		msg.ttl = this.ttl - 1;
		return msg;
	}

	public int getSize() {
		return 13;
	}
}
