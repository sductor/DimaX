package examples.dcop_old.algo.topt;

public class DPOPTreeNode extends TreeNode {

	public RewardMatrix mat;

	public int gid;
	public int reward;
	
	public DPOPTreeNode(int i, int g, int val, boolean f, TreeNode p) {
		super(i, val, f, p);
		mat = null;
		reward = 0;
		gid = g;
	}

	@Override
	public String toString() {
		return "" + id + " " + value + " " + (fixed ? "*" : "-") + " " + reward
				+ "\n" + mat + "\n";
	}
	
	@Override
	public int hashCode() {
		return this.gid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DPOPTreeNode) {
			DPOPTreeNode n = (DPOPTreeNode) obj;
			return this.gid == n.gid && this.id == n.id;
		} else
			return false;		
	}

}
