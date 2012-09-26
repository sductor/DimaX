package frameworks.faulttolerance.dcop.dcop;

import java.util.Vector;

public class ClassicalVariable extends AbstractVariable<Integer>{

	private int _value;
	public Vector<AbstractConstraint> neighbors;
	
	public ClassicalVariable(int i, int d, DcopAbstractGraph g) {
		super(i,d,g);
		init();
	}

	public ClassicalVariable(String s, DcopAbstractGraph g) {
		super(s,g);
		init();
	}

	private void init() {
		value = -1;
		neighbors = new Vector<AbstractConstraint>();
	}

	public void uninstanciate(){
		value = -1;		
	}
	public void backupValue() {
		_value = value;
	}

	public void recoverValue() {
		value = _value;
	}

	public boolean addConstraint(AbstractConstraint c) {
		return neighbors.add(c);
	}
	
	public boolean hasNeighbor(int nid) {
		for (AbstractConstraint c : neighbors) {
			if (c.getNeighbor(this).id == nid)
				return true;
		}
		return false;
	}
	
	public AbstractConstraint getNeighbor(int nid) {
		for (AbstractConstraint c : neighbors) {
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
		for (AbstractConstraint c : neighbors) {
			double v = c.evaluate();
			if (v == -1)
				return -1;
			reward += v;
		}
		return reward;
	}

	@Override
	public boolean isInstaciated() {
		return value!=-1;
	}
}
