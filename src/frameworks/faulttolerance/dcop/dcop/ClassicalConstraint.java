package frameworks.faulttolerance.dcop.dcop;

import java.util.Arrays;

public class ClassicalConstraint extends AbstractConstraint<Integer> {

	public double[][] f;

	private int[] m1;
	private int[] m2;
	private double m;

	public ClassicalConstraint(AbstractVariable<Integer> a, AbstractVariable<Integer> b) {
		super(a,b);
		if (!(a instanceof ClassicalVariable) && !(b instanceof ClassicalVariable))
			throw new RuntimeException();
		f = new double[d1][d2];
		m = -1;
		m1 = new int[d1];
		m2 = new int[d2];
	}

	public ClassicalVariable getFirst() {
		return (ClassicalVariable) super.getFirst();
	}


	public ClassicalVariable getSecond() {
		return (ClassicalVariable)  super.getFirst();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CONSTRAINT ");
		buffer.append(getFirst().id);
		buffer.append(" ");
		buffer.append(getSecond().id);
		buffer.append(Helper.newline);
		for (int i = 0; i < d1; i++) {
			for (int j = 0; j < d2; j++) {
				buffer.append("F ");
				buffer.append(i);
				buffer.append(" ");
				buffer.append(j);
				buffer.append(" ");
				buffer.append(f[i][j]);
				buffer.append(Helper.newline);
			}
		}
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

	public void cache() {
		if (m >= 0)
			return;
		for (int i = 0; i < d1; i++) {
			int best = -1;
			double max = -1;
			for (int j = 0; j < d2; j++) {
				if (f[i][j] > max) {
					max = f[i][j];
					best = j;
				}
			}
			m1[i] = best;
			if (max > m)
				m = max;
		}
		for (int i = 0; i < d2; i++) {
			int best = -1;
			double max = -1;
			for (int j = 0; j < d1; j++) {
				if (f[j][i] > max) {
					max = f[j][i];
					best = j;
				}
			}
			m2[i] = best;
		}
	}

	public double evaluate() {
		//System.out.println("(" + first.id + "," + first.value + ") (" + second.id + "," + second.value + ")");
		if (Helper.app != null)
			Helper.app.numberEval++;
		if (getFirst().value == -1 && getSecond().value == -1)
			return m;
		if (getFirst().value == -1 && getSecond().value != -1){
			if (m2[getSecond().value]==-1)
				return Double.NEGATIVE_INFINITY;
			return f[m2[getSecond().value]][getSecond().value];
		}			
		if (getFirst().value != -1 && getSecond().value == -1){
			if (m1[getFirst().value]==-1)
				return Double.NEGATIVE_INFINITY;
			return f[getFirst().value][m1[getSecond().value]];
		}			
		return f[getFirst().value][getSecond().value];
	}

	public double evaluate(Integer val1, Integer val2) {
		return f[val1][val2];
	}
}
