package vieux.src.dcop;

public class Constraint {

	Graph graph;

	public Variable first;
	public Variable second;

	public int d1;
	public int d2;

	public int[][] f;

	public int[] m1;
	public int[] m2;
	public int m;

	public Constraint(Variable a, Variable b) {
		assert a.graph == b.graph;
		first = a;
		second = b;
		graph = a.graph;
		d1 = a.domain;
		d2 = b.domain;
		f = new int[d1][d2];
		first.addConstraint(this);
		second.addConstraint(this);
		m = -1;
		m1 = new int[d1];
		m2 = new int[d2];
	}

	public Variable getNeighbor(Variable v) {
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

	public int[] encode() {
		int[] msg = new int[4 + d1 * d2];
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

	public void cache() {
		if (m >= 0)
			return;
		for (int i = 0; i < d1; i++) {
			int best = -1;
			int max = -1;
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
			int max = -1;
			for (int j = 0; j < d1; j++) {
				if (f[j][i] > max) {
					max = f[j][i];
					best = j;
				}
			}
			m2[i] = best;
		}
	}

	public int evaluate() {
		//System.out.println("(" + first.id + "," + first.value + ") (" + second.id + "," + second.value + ")");
		if (Helper.app != null)
			Helper.app.numberEval++;
		if (first.value == -1 && second.value == -1)
			return m;
		if (first.value == -1 && second.value != -1)
			return f[m2[second.value]][second.value];
		if (first.value != -1 && second.value == -1)
			return f[first.value][m1[first.value]];
		return f[first.value][second.value];
	}

	public long evaluate(Integer val1, Integer val2) {
		return f[val1][val2];
	}
}
