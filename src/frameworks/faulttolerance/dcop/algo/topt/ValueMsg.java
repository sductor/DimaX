package frameworks.faulttolerance.dcop.algo.topt;

import frameworks.faulttolerance.dcop.daj.Message;
<<<<<<< HEAD
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
>>>>>>> dcopX
=======
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
>>>>>>> dcopX

public class ValueMsg extends Message {
	int id;
	int value;
	int ttl;

	public ValueMsg() {
		super();
	}

<<<<<<< HEAD
<<<<<<< HEAD
	public ValueMsg(AbstractVariable v, int t) {
=======
	public ValueMsg(ReplicationVariable v, int t) {
>>>>>>> dcopX
=======
	public ValueMsg(ReplicationVariable v, int t) {
>>>>>>> dcopX
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
