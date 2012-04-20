package examples.dcop.dcop;

public class Dependence {
	public int cid;
	public int pid;
	public int[] values;

	public Dependence(final Variable child, final Constraint c) {
		this.cid = child.id;
		final Variable parent = c.getNeighbor(child);
		this.pid = parent.id;
		this.values = new int[parent.domain];
		for (int i=0;i<this.values.length;i++) {
			this.values[i] = -1;
		}
	}

}
