package vieux.dcop_old.algo.topt;

import vieux.dcop_old.agent.DcopMessage;
import vieux.dcop_old.agent.NodeIdentifier;

public class LockMsg extends DcopMessage {
	NodeIdentifier gid;
	int val;
	int attempt;
	TreeNode node;
	boolean lock;

	public LockMsg() {
		super();
	}

	public LockMsg(NodeIdentifier i, int v, int a, TreeNode n, boolean l) {
		gid = i;
		val = v;
		attempt = a;
		node = n;
		lock = l;
	}

	public String getText() {
		return (lock ? "LOCK " : "UNLOCK ") + gid + "\n";
	}

	public int getSize() {
		return 6;
		// return 6 + 8 * node.getSize();
	}
}

