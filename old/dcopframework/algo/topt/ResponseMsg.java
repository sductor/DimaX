package negotiation.dcopframework.algo.topt;

import negotiation.dcopframework.daj.Message;

public class ResponseMsg extends Message {
	int id;
	TreeNode node;
	int attempt;
	boolean accept;

	public ResponseMsg() {
		super();
	}

	public ResponseMsg(final int i, final int att, final TreeNode n, final boolean a) {
		this.id = i;
		this.attempt = att;
		this.node = n;
		this.accept = a;
	}

	@Override
	public String getText() {
		return (this.accept ? "ACCEPT " : "DENY ") + this.id + "\n";
	}

	@Override
	public int getSize() {
		return 10;
	}
}

