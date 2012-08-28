package negotiation.dcopframework.algo.topt;

import negotiation.dcopframework.daj.Message;

public class CommitMsg extends Message {
	int gid;
	int attempt;
	TreeNode node;

	public CommitMsg() {
		super();
	}

	public CommitMsg(final int i, final int a, final TreeNode n) {
		this.gid = i;
		this.attempt = a;
		this.node = n;
	}

	@Override
	public String getText() {
		return "COMMIT " + this.gid + "\n";
	}

	@Override
	public int getSize() {
		return 5;
	}
}
