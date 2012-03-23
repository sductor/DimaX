package framework.dcop.algo.topt;

import framework.dcop.dcop.Variable;
import framework.dcop.dimaxdaj.DCOPMessage;

public class ValueMsg extends DCOPMessage {
	int id;
	int value;
	int ttl;

	public ValueMsg() {
		super();
	}

	public ValueMsg(Variable v, int t) {
		super();
		id = v.id;
		value = v.value;
		// if (value == -1)
		// System.out.println("Surprise");
		ttl = t;
	}

	@Override
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

	@Override
	public int getSize() {
		return 13;
	}
}
