package frameworks.faulttolerance.dcop.dcop;

import java.util.Arrays;

public abstract class AbstractConstraint {

	DcopAbstractGraph graph;

	public AbstractVariable first;
	public AbstractVariable second;

	public int d1;
	public int d2;

	public AbstractConstraint(AbstractVariable a, AbstractVariable b) {
		assert a.graph == b.graph;
		first = a;
		second = b;
		graph = a.graph;
		d1 = a.domain;
		d2 = b.domain;
		first.addConstraint(this);
		second.addConstraint(this);
	}

	public AbstractVariable getNeighbor(AbstractVariable v) {
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
		double[] msg = new double[4 + d1 * d2];
		msg[0] = first.id;
		msg[1] = d1;
		msg[2] = second.id;
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
