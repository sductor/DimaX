package framework.dcop.algo.topt;

import framework.dcop.dimaxdaj.DCOPMessage;

public class LockMsg extends DCOPMessage {
	int gid;
	int val;
	int attempt;
	TreeNode node;
	boolean lock;

	public LockMsg() {
		super();
	}

	public LockMsg(int i, int v, int a, TreeNode n, boolean l) {
		gid = i;
		val = v;
		attempt = a;
		node = n;
		lock = l;
	}

	@Override
	public String getText() {
		return (lock ? "LOCK " : "UNLOCK ") + gid + "\n";
	}

	@Override
	public int getSize() {
		return 6;
		// return 6 + 8 * node.getSize();
	}
}

