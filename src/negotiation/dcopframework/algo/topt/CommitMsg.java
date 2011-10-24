package negotiation.dcopframework.algo.topt;

import negotiation.dcopframework.daj.Message;

public class CommitMsg extends Message {
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
