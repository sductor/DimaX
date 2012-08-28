package frameworks.faulttolerance.dcop.dcop;

import java.util.Vector;

public abstract class AbstractVariable<Value> {

	final DcopAbstractGraph graph;

	Value value;
	
	public int id;
	public int domain;
	public boolean fixed = false;

	public Vector<AbstractConstraint> neighbors;
	
	public AbstractVariable(int i, int d, DcopAbstractGraph g) {
		assert g!=null;
		id = i;
		graph = g;
		domain = d;
	}

	public AbstractVariable(String s, DcopAbstractGraph g) {
		assert g!=null;
		String[] ss = s.split(" ");
//		assert ss.length >= 5;
		id = Integer.parseInt(ss[1]);
		domain = Integer.parseInt(ss[3]);
		graph = g;
	}
	
	public abstract double evaluate() ;

	public abstract void backupValue();

	public abstract void clear();
	
	public abstract void recoverValue() ;

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


	public abstract void uninstanciate();
	public abstract boolean isInstaciated();

	public String toString() {
		return "VARIABLE " + id + " 1 " + domain + Helper.newline;
	}

	public int getDegree() {
		return neighbors.size();
	}

}
