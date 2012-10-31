package frameworks.faulttolerance.olddcop.algo.topt;

import java.util.ArrayList;
import java.util.HashSet;

public class TreeNode {
	public int id;
	public int value;
	public boolean fixed;
	public boolean mark;

	public ArrayList<TreeNode> children;

	public TreeNode parent;

	public TreeNode(int i, int val, boolean f, TreeNode p) {
		id = i;
		value = val;
		fixed = f;
		children = new ArrayList<TreeNode>();
		if (p != null)
			p.children.add(this);
		parent = p;
		mark = false;
	}

	public int getTreeID() {
		if (this.parent == null)
			return this.id;
		return parent.getTreeID();
	}

	public String toString() {
		return "" + id + " " + value + "\n";
	}

	public HashSet<Integer> getSet() {
		HashSet<Integer> set = new HashSet<Integer>();
		set.add(id);
		for (TreeNode n : children) {
			set.addAll(n.getSet());
		}
		return set;
	}

	public int getSize() {
		return this.getSet().size();
	}

	public TreeNode find(int i) {
		if (this.id == i)
			return this;
		for (TreeNode n : children) {
			TreeNode f = n.find(i);
			if (f != null)
				return f;
		}
		return null;
	}

	public void markAll() {
		this.mark = true;
		for (TreeNode n : children) {
			n.markAll();
		}
	}
	public int getMarkedNodeSize(){
		return getMarkedSet().size();
	}
	public int maxdistanceMarkedNode(){
		int dist = 0;				
		for (TreeNode n : children) {
			int newtemp = n.maxdistanceMarkedNode(); 
			if(newtemp > dist){
				dist = newtemp; 
			}
		}
		if(dist > 0){
			dist++;
		}
		else if(this.mark){
			dist = 1;	
		}
		return dist-1;		
	}
	public HashSet<Integer> getMarkedSet(){
		HashSet<Integer> set = new HashSet<Integer>();
		if(this.mark){
			set.add(id);
		}
		for(TreeNode n: children){
			set.addAll(n.getMarkedSet());			
		}
		return set;
	}
}
