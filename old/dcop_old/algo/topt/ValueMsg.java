package examples.dcop_old.algo.topt;

import examples.dcop_old.daj.DCOPMessage;
import examples.dcop_old.dcop.Variable;

public class ValueMsg extends DCOPMessage {
	private int id;
	int value;
	int ttl;

	public ValueMsg() {
		super();
	}

	public ValueMsg(Variable v, int t) {
		super();
		id=v.id;
		value = v.value;
		// if (value == -1)
		// System.out.println("Surprise");
		ttl = t;
	}

	@Override
	public String getText() {
		return ("ID " + getId() + ";VALUE " + value + ";TTL " + ttl);
	}

	public ValueMsg forward() {
		ValueMsg msg = new ValueMsg();
		msg.id=this.getId();
		msg.value = this.value;
		msg.ttl = this.ttl - 1;
		return msg;
	}

	@Override
	public int getSize() {
		return 13;
	}

	public int getId() {
		return id;
	}
}
