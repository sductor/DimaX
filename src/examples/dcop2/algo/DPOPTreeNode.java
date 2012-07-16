package examples.dcop2.algo;

public class DPOPTreeNode extends TreeNode {

	public RewardMatrix mat;

	public int gid;
	public int reward;

	public DPOPTreeNode(final int i, final int g, final int val, final boolean f, final TreeNode p) {
		super(i, val, f, p);
		this.mat = null;
		this.reward = 0;
		this.gid = g;
	}

	@Override
	public String toString() {
		return "" + this.id + " " + this.value + " " + (this.fixed ? "*" : "-") + " " + this.reward
				+ "\n" + this.mat + "\n";
	}

	@Override
	public int hashCode() {
		return this.gid;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DPOPTreeNode) {
			final DPOPTreeNode n = (DPOPTreeNode) obj;
			return this.gid == n.gid && this.id == n.id;
		} else {
			return false;
		}
	}

}
