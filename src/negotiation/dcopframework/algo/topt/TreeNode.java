package negotiation.dcopframework.algo.topt;

import java.util.ArrayList;
import java.util.HashSet;

public class TreeNode {
	public int id;
	public int value;
	public boolean fixed;
	public boolean mark;

	public ArrayList<TreeNode> children;

	public TreeNode parent;

	public TreeNode(final int i, final int val, final boolean f, final TreeNode p) {
		this.id = i;
		this.value = val;
		this.fixed = f;
		this.children = new ArrayList<TreeNode>();
		if (p != null)
			p.children.add(this);
		this.parent = p;
		this.mark = false;
	}

	public int getTreeID() {
		if (this.parent == null)
			return this.id;
		return this.parent.getTreeID();
	}

	@Override
	public String toString() {
		return "" + this.id + " " + this.value + "\n";
	}

	public HashSet<Integer> getSet() {
		final HashSet<Integer> set = new HashSet<Integer>();
		set.add(this.id);
		for (final TreeNode n : this.children)
			set.addAll(n.getSet());
				return set;
	}

	public int getSize() {
		return this.getSet().size();
	}

	public TreeNode find(final int i) {
		if (this.id == i)
			return this;
		for (final TreeNode n : this.children) {
			final TreeNode f = n.find(i);
			if (f != null)
				return f;
		}
		return null;
	}

	public void markAll() {
		this.mark = true;
		for (final TreeNode n : this.children)
			n.markAll();
	}
	public int getMarkedNodeSize(){
		return this.getMarkedSet().size();
	}
	public int maxdistanceMarkedNode(){
		int dist = 0;
		for (final TreeNode n : this.children) {
			final int newtemp = n.maxdistanceMarkedNode();
			if(newtemp > dist)
				dist = newtemp;
		}
		if(dist > 0)
			dist++;
		else if(this.mark)
			dist = 1;
		return dist-1;
	}
	public HashSet<Integer> getMarkedSet(){
		final HashSet<Integer> set = new HashSet<Integer>();
		if(this.mark)
			set.add(this.id);
		for(final TreeNode n: this.children)
			set.addAll(n.getMarkedSet());
				return set;
	}
}
