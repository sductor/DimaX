package vieux.myDCOP.dcop;

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

	public Constraint(final Variable a, final Variable b) {
		assert a.graph == b.graph;
		this.first = a;
		this.second = b;
		this.graph = a.graph;
		this.d1 = a.domain;
		this.d2 = b.domain;
		this.f = new int[this.d1][this.d2];
		this.first.addConstraint(this);
		this.second.addConstraint(this);
		this.m = -1;
		this.m1 = new int[this.d1];
		this.m2 = new int[this.d2];
	}

	public Variable getNeighbor(final Variable v) {
		if (v == this.first) {
			return this.second;
		}
		if (v == this.second) {
			return this.first;
		}
		return null;
	}
	public int getNeighbor(final int vid) {
		if (vid == this.first.id) {
			return this.second.id;
		}
		if (vid == this.second.id) {
			return this.first.id;
		}
		return -1;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("CONSTRAINT ");
		buffer.append(this.first.id);
		buffer.append(" ");
		buffer.append(this.second.id);
		buffer.append(Helper.newline);
		for (int i = 0; i < this.d1; i++) {
			for (int j = 0; j < this.d2; j++) {
				buffer.append("F ");
				buffer.append(i);
				buffer.append(" ");
				buffer.append(j);
				buffer.append(" ");
				buffer.append(this.f[i][j]);
				buffer.append(Helper.newline);
			}
		}
		return buffer.toString();
	}

	public int[] encode() {
		final int[] msg = new int[4 + this.d1 * this.d2];
		msg[0] = this.first.id;
		msg[1] = this.d1;
		msg[2] = this.second.id;
		msg[3] = this.d2;
		for (int i = 0; i < this.d1; i++){
			for (int j = 0; j < this.d2; j++) {
				msg[4 + i * this.d2 + j] = this.f[i][j];
			}
		}
		return msg;
	}

	public void cache() {
		if (this.m >= 0) {
			return;
		}
		for (int i = 0; i < this.d1; i++) {
			int best = -1;
			int max = -1;
			for (int j = 0; j < this.d2; j++) {
				if (this.f[i][j] > max) {
					max = this.f[i][j];
					best = j;
				}
			}
			this.m1[i] = best;
			if (max > this.m) {
				this.m = max;
			}
		}
		for (int i = 0; i < this.d2; i++) {
			int best = -1;
			int max = -1;
			for (int j = 0; j < this.d1; j++) {
				if (this.f[j][i] > max) {
					max = this.f[j][i];
					best = j;
				}
			}
			this.m2[i] = best;
		}
	}

	public int evaluate() {
		//System.out.println("(" + first.id + "," + first.value + ") (" + second.id + "," + second.value + ")");
		//		if (Helper.app != null)
		//			Helper.app.numberEval++;
		if (this.first.value == -1 && this.second.value == -1) {
			return this.m;
		}
		if (this.first.value == -1 && this.second.value != -1) {
			return this.f[this.m2[this.second.value]][this.second.value];
		}
		if (this.first.value != -1 && this.second.value == -1) {
			return this.f[this.first.value][this.m1[this.first.value]];
		}
		return this.f[this.first.value][this.second.value];
	}

	public long evaluate(final Integer val1, final Integer val2) {
		return this.f[val1][val2];
	}
}
