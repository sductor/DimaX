package framework.dcop.algo.topt;

import framework.dcop.dimaxdaj.DCOPMessage;

public class CommitMsg extends DCOPMessage {
	int gid;
	int attempt;
	TreeNode node;

	public CommitMsg() {
		super();
	}

	public CommitMsg(int i, int a, TreeNode n) {
		gid = i;
		attempt = a;
		node = n;
	}

	@Override
	public String getText() {
		return ("COMMIT ") + gid + "\n";
	}

	@Override
	public int getSize() {
		return 5;
	}
}
