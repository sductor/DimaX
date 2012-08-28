package vieux.myDCOP.algo;


public class LockMsg extends Message {
	int gid;
	int val;
	int attempt;
	TreeNode node;
	boolean lock;

	public LockMsg() {
		super();
	}

	public LockMsg(final int i, final int v, final int a, final TreeNode n, final boolean l) {
		this.gid = i;
		this.val = v;
		this.attempt = a;
		this.node = n;
		this.lock = l;
	}

	public String getText() {
		return (this.lock ? "LOCK " : "UNLOCK ") + this.gid + "\n";
	}

	public int getSize() {
		return 6;
		// return 6 + 8 * node.getSize();
	}
}

