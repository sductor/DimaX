package examples.dcop.dcop;

public class Dependence {
	public int cid;
	public int pid;
	public int[] values;
	
	public Dependence(Variable child, Constraint c) {
		cid = child.id;
		Variable parent = c.getNeighbor(child);
		pid = parent.id;
		values = new int[parent.domain];
		for (int i=0;i<values.length;i++)
			values[i] = -1;
	}
	
}
