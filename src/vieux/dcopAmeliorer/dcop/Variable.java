package vieux.dcopAmeliorer.dcop;

import java.util.Vector;

public class Variable {

	Graph graph;

	public int id;
	public int domain;
	public int value;
	private int _value;
	public Vector<Constraint> neighbors;
	public boolean fixed;

	public Variable(final int i, final int d, final Graph g) {
		this.id = i;
		this.graph = g;
		this.domain = d;
		this.init();
	}

	public Variable(final String s, final Graph g) {
		final String[] ss = s.split(" ");
		//		assert ss.length >= 5;
		this.id = Integer.parseInt(ss[1]);
		this.domain = Integer.parseInt(ss[3]);
		this.graph = g;
		this.init();
	}

	public void init() {
		this.value = -1;
		this.neighbors = new Vector<Constraint>();
		this.fixed = false;
	}

	public void backupValue() {
		this._value = this.value;
	}

	public void recoverValue() {
		this.value = this._value;
	}

	public boolean addConstraint(final Constraint c) {
		return this.neighbors.add(c);
	}

	public boolean hasNeighbor(final int nid) {
		for (final Constraint c : this.neighbors) {
			if (c.getNeighbor(this).id == nid) {
				return true;
			}
		}
		return false;
	}

	public Constraint getNeighbor(final int nid) {
		for (final Constraint c : this.neighbors) {
			if (c.getNeighbor(this).id == nid) {
				return c;
			}
		}
		return null;
	}

	public void clear() {
		if (!this.fixed) {
			this.value = -1;
		}
	}

	@Override
	public String toString() {
		return "VARIABLE " + this.id + " 1 " + this.domain + Helper.newline;
	}

	public int getDegree() {
		return this.neighbors.size();
	}

	public int evaluate() {
		if (this.value == -1) {
			return -1;
		}
		int reward = 0;
		for (final Constraint c : this.neighbors) {
			final int v = c.evaluate();
			if (v == -1) {
				return -1;
			}
			reward += v;
		}
		return reward;
	}
}
