package examples.dcop.algo.topt;

import examples.dcop.daj.DcopMessage;

public class LockMsg extends DcopMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = 6127789331346846921L;
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

	@Override
	public String getText() {
		return (this.lock ? "LOCK " : "UNLOCK ") + this.gid + "\n";
	}

	@Override
	public int getSize() {
		return 6;
		// return 6 + 8 * node.getSize();
	}
}

