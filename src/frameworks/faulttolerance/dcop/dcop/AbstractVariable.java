package frameworks.faulttolerance.dcop.dcop;

import java.util.Vector;

public abstract class AbstractVariable<Value> {

	final DcopAbstractGraph graph;

	public int id;
	protected Value domain;
	private int value;
	private int _value;
	public Vector<AbstractConstraint<Value>> neighbors;
	public boolean fixed;
	
	public AbstractVariable(int i, Value d, DcopAbstractGraph g) {
		assert g!=null;
		id = i;
		graph = g;
		domain=d;
		init();
	}



	public void init() {
		setValue(-1);
		neighbors = new Vector<AbstractConstraint<Value>>();
		fixed = false;
	}

	public void backupValue() {
		_value = getValue();
	}

	public void recoverValue() {
		setValue(_value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	
	public boolean addConstraint(AbstractConstraint<Value> c) {
		return neighbors.add(c);
	}
	
	public boolean hasNeighbor(int nid) {
		for (AbstractConstraint<Value> c : neighbors) {
			if (c.getNeighbor(this).id == nid)
				return true;
		}
		return false;
	}
	
	public AbstractConstraint<Value> getNeighbor(int nid) {
		for (AbstractConstraint<Value> c : neighbors) {
			if (c.getNeighbor(this).id == nid)
				return c;
		}
		return null;
	}

	public void clear() {
		if (!fixed)
			setValue(-1);
	}

	public String toString() {
		return "VARIABLE " + id + " 1 " + getDomain() + Helper.newline;
	}

	public int getDegree() {
		return neighbors.size();
	}

	public abstract double evaluate();

	public abstract int getDomain();
}
