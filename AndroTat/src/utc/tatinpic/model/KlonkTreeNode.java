/**
 * 
 */
package utc.tatinpic.model;

import jade.util.leap.Serializable;

import java.util.ArrayList;

/**
 * @author Aymeric PELLE
 */

public class KlonkTreeNode implements Serializable {
	Klonk userObject;
	KlonkTreeNode parent;
	ArrayList<KlonkTreeNode> children;
	/**
	 * @param object
	 */
	public KlonkTreeNode(Klonk k) {
	    setUserObject(k);
	    children = new ArrayList<KlonkTreeNode>() ;
	}

	public KlonkTreeNode(String klonkcontent, String id,Klonk.Category category) {
		Identification ident = new Identification(id);
		Klonk kl = new Klonk(ident);
		kl.setText(klonkcontent);
		kl.addCategory(category);
		setUserObject(kl);
		children = new ArrayList<KlonkTreeNode>() ;
	}

	protected boolean checkUserObject(Object object) {
		return object instanceof Klonk;
	}

	public Klonk getUserObject() {
		return userObject;
	}
	public void setUserObject(Klonk userObject) {
		this.userObject = userObject;
	}
	public KlonkTreeNode getParent() {
		return parent;
	}

	public void setParent(KlonkTreeNode parent) {
		this.parent = parent;
	}
    public boolean isRoot() {
	  return parent == null;
    }
	

	/*
	 * set the text to the klonk user object
	 */
	public void setContent(String s) {
		Klonk k = getUserObject();
		k.setText(s);
	}
	public int getChildCount() {
		return children.size();
	}
	public KlonkTreeNode getChildAt(int index) {
		return children.get(index);
	}
	public KlonkTreeNode getFirstChild() {
		if (children.size() > 0)
		  return children.get(0);
		return null;
	}
	public void add(KlonkTreeNode node) {
		children.add(node);
		KlonkTreeNode knparent = node.getParent();
		if (knparent != null) {
			knparent.remove(node);
		}
		node.setParent(this);
	}
	public void remove(KlonkTreeNode node) {
		children.remove(node);
		node.setParent(null);
	}
	public int getLevel() {
		if (parent == null)
			return 0;
		return 1 + getParent().getLevel();
	}
	public int getDepth() {
		if (children.size() == 0)
			return 0;
	    int max = 0;
		for (KlonkTreeNode node : children) {
			int n = node.getDepth();
			if (n > max)
				max = n;
		}
		return 1 + max;
	}
}
