package frameworks.faulttolerance.dcop.dcop;

import java.util.Vector;

public class Variable {

	final DcopAbstractGraph graph;

	public int id;
	public int domain;
	public int value;
	private int _value;
	public Vector<Constraint> neighbors;
	public boolean fixed;
	
	public Variable(int i, int d, DcopAbstractGraph g) {
		assert g!=null;
		id = i;
		graph = g;
		domain = d;
		init();
	}

	public Variable(String s, DcopAbstractGraph g) {
		assert g!=null;
		String[] ss = s.split(" ");
//		assert ss.length >= 5;
		id = Integer.parseInt(ss[1]);
		domain = Integer.parseInt(ss[3]);
		graph = g;
		init();
	}

	public void init() {
		value = -1;
		neighbors = new Vector<Constraint>();
		fixed = false;
	}

	public void backupValue() {
		_value = value;
	}

	public void recoverValue() {
		value = _value;
	}

	public boolean addConstraint(Constraint c) {
		return neighbors.add(c);
	}
	
	public boolean hasNeighbor(int nid) {
		for (Constraint c : neighbors) {
			if (c.getNeighbor(this).id == nid)
				return true;
		}
		return false;
	}
	
	public Constraint getNeighbor(int nid) {
		for (Constraint c : neighbors) {
			if (c.getNeighbor(this).id == nid)
				return c;
		}
		return null;
	}

	public void clear() {
		if (!fixed)
			value = -1;
	}

	public String toString() {
		return "VARIABLE " + id + " 1 " + domain + Helper.newline;
	}

	public int getDegree() {
		return neighbors.size();
	}

	public double evaluate() {
		if (value == -1) {
			return -1;
		}
		double reward = 0;
		for (Constraint c : neighbors) {
			double v = c.evaluate();
			if (v == -1)
				return -1;
			reward += v;
		}
		return reward;
	}
}
