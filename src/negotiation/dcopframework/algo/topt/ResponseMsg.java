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

	public ResponseMsg(int i, int att, TreeNode n, boolean a) {
		id = i;
		attempt = att;
		node = n;
		accept = a;
	}

	@Override
	public String getText() {
		return (accept ? "ACCEPT " : "DENY ") + id + "\n";
	}

	@Override
	public int getSize() {
		return 10;
	}
}

