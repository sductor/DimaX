package frameworks.faulttolerance.dcop.dcop;


public abstract class AbstractConstraint<Value> {

	DcopAbstractGraph graph;

	public AbstractVariable<Value> first;
	public AbstractVariable<Value> second;

	public int d1;
	public int d2;

	public AbstractConstraint(AbstractVariable<Value> a, AbstractVariable<Value> b) {
		assert a.graph == b.graph;
		first = a;
		second = b;
		graph = a.graph;
		d1 = a.getDomain();
		d2 = b.getDomain();
	}

	public AbstractVariable<Value> getNeighbor(AbstractVariable<Value> v) {
		if (v == first)
			return second;
		if (v == second)
			return first;
		return null;
	}
	public int getNeighbor(int vid) {
		if (vid == first.id)
			return second.id;
		if (vid == second.id)
			return first.id;
		return -1;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CONSTRAINT ");
		buffer.append(first.id);
		buffer.append(" ");
		buffer.append(second.id);
		buffer.append(Helper.newline);
		return buffer.toString();
	}

	public double[] encode() {
		double[] msg = new double[4];
		msg[0] = first.id;
		msg[1] = d1;
		msg[2] = second.id;
		msg[3] = d2;
		return msg;
	}

	public abstract double evaluate();

	public abstract double evaluate(Integer val1, Integer val2);
}
