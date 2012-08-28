package frameworks.faulttolerance.dcop.dcop;

import java.util.Arrays;

public abstract class AbstractConstraint<Value> {

	final DcopAbstractGraph<Value> graph;

	private final AbstractVariable<Value> first;
	private final AbstractVariable<Value> second;

	public final int d1;
	public final int d2;

	public AbstractConstraint(AbstractVariable<Value> a, AbstractVariable<Value> b) {
		assert a.graph == b.graph;
		first=a;
		second=b;
		graph = a.graph;
		d1 = a.domain;
		d2 = b.domain;
		getFirst().addConstraint(this);
		getSecond().addConstraint(this);
	}

	public AbstractVariable<Value> getNeighbor(AbstractVariable<Value> v) {
		if (v == getFirst())
			return getSecond();
		if (v == getSecond())
			return getFirst();
		return null;
	}
	public int getNeighbor(int vid) {
		if (vid == getFirst().id)
			return getSecond().id;
		if (vid == getSecond().id)
			return getFirst().id;
		return -1;
	}

	/*
	 * 
	 */


	public AbstractVariable<Value> getFirst() {
		return first;
	}


	public AbstractVariable<Value> getSecond() {
		return second;
	}

	/*
	 * 
	 */

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CONSTRAINT ");
		buffer.append(getFirst().id);
		buffer.append(" ");
		buffer.append(getSecond().id);
		buffer.append(Helper.newline);
		return buffer.toString();
	}

	public double[] encode() {
		double[] msg = new double[4 + d1 * d2];
		msg[0] = getFirst().id;
		msg[1] = d1;
		msg[2] = getSecond().id;
		msg[3] = d2;
		for (int i = 0; i < d1; i++){
			for (int j = 0; j < d2; j++) {
				msg[4 + i * d2 + j] = f[i][j];
			}
		}
		return msg;
	}

	public abstract double evaluate() ;

	public abstract double evaluate(Integer val1, Integer val2);

}
