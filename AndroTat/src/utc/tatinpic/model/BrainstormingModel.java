package utc.tatinpic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utc.tatinpic.semantics.BSJason;
import utc.tatinpic.semantics.ContentOntology;
import utc.tatinpic.semantics.MessageContentBroker;

public class BrainstormingModel extends PhaseModel implements Serializable {
	
	private static String ROOT_CONTENT = "Default Main group";
	private static String ROOT_ID = "1:p0";
	
	private KlonkTreeNode root;
	
	// Map<klonk id , klonk node where the klonk is attached>
	public Map<String, KlonkTreeNode> graph = new HashMap<String, KlonkTreeNode>();

	public BrainstormingModel(KlonkTreeNode root) {
		super();
		this.root = root;
	}

	public BrainstormingModel(String name,Klonk.Category category) {
		super();
		KlonkTreeNode k = new KlonkTreeNode(name,name + ROOT_ID,category);
		root = k;
		graph.put(ROOT_ID, k);
	}


	public KlonkTreeNode getRoot() {
		return root;
	}
	
	public void setRoot(KlonkTreeNode root) {
		this.root = root;
	}

	public boolean isRoot(Klonk klonk) {
		return graph.get(klonk.getIdentification().getId()) == getRoot();
	}

	public Klonk createKlonk(Klonk klonk) {
		KlonkTreeNode k = new KlonkTreeNode(klonk);
		graph.put(klonk.getIdentification().getId(), k);
		getRoot().add(k);
		return klonk;
	}

	public Klonk createKlonkGroup(Klonk klonk, ArrayList<String> refs,
			String option) {
		Klonk result = null;
		KlonkTreeNode k = new KlonkTreeNode(klonk);
		graph.put(klonk.getIdentification().getId(), k);
		
		if (refs != null) {
			if (refs.isEmpty()) {
				//ErrorManager.getInstance().setError(ErrorManager.ERROR_NULL_REFS);
				return result;
			}
			else
			  for (String s : refs)
				k.add(graph.get(s));
		}
		else {
			//ErrorManager.getInstance().setError(ErrorManager.ERROR_NULL_REFS);
			return result;
		}
		result = klonk;
		if (option.equals(ContentOntology.MAIN_GROUP)) {
			int nb = getRoot().getChildCount();
			for (int i = 0; i < nb; i++) {
				k.add((KlonkTreeNode) getRoot().getFirstChild());
			}
			graph.remove(getRoot().getUserObject().getIdentification().getId());
			root = k;
		} else {
			getRoot().add(k);
		}
		return result;
	}

	private void addToGroup(Klonk klonk, Klonk groupKlonk) {
		KlonkTreeNode k = graph.get(klonk.getIdentification().getId());
		KlonkTreeNode groupk = graph.get(groupKlonk.getIdentification().getId());
		if (k != null && groupk != null) {
			groupk.add(k);
		}
	}

	public Klonk addToGroup(ArrayList<String> l, Klonk groupKlonk) {
		for (String s : l) {
			Klonk k = graph.get(s).getUserObject();
			if (k != null)
				addToGroup(k, groupKlonk);
		}
		return groupKlonk;
	}
	public Klonk addToGroup(ArrayList<String> l, String toGroup) {
		Klonk k = graph.get( toGroup).getUserObject();
		if (k != null) {
		   k = addToGroup(l,k);
		}
		return k;
	}
	
	public Klonk editKlonk(String sto, String content) {
		KlonkTreeNode kn = graph.get(sto);
		Klonk k = null;
		if (kn != null) {
			k = kn.getUserObject();
			editKlonk(k,content);
		}
		//else
			//ErrorManager.getInstance().setError(ErrorManager.ERROR_REF);
		return k;
	}
	private void editKlonk(Klonk klonk, String newText) {
		klonk.setText(newText);
	}

	public Klonk deleteKlonk(String klonkId) {
		KlonkTreeNode kn = graph.get(klonkId);
		Klonk result = null;
		// Can't delete root node
		if (kn != null && !kn.isRoot()) {
			KlonkTreeNode parent = (KlonkTreeNode) kn.getParent();
			for (int i = 0; i < kn.getChildCount(); ++i) {
				parent.add(kn.getChildAt(i));
			}
			parent.remove(kn);
			graph.remove(klonkId);
			result = kn.getUserObject();
		}
		//else
			//ErrorManager.getInstance().setError(ErrorManager.ERROR_REF);
		return result;
	}
	public ArrayList<String> getChildren(String klonkid) {
		 ArrayList<String> children = new  ArrayList<String>();
		 KlonkTreeNode node = graph.get(klonkid);
		 if (node != null) {
			 for (int i = 0 ; i < node.getChildCount() ; i++) {
				  children.add(node.getChildAt(i).getUserObject().getIdentification().getId());
			  }
		 }
		 return children;
	}
	public void update(String cmd, String ref) {
		MessageContentBroker cb = new MessageContentBroker(cmd);

		if (cb.getAction().equals(ContentOntology.CREATE)) {
			Klonk k = new Klonk(new Identification(ref));
			k.setText(cb.getValue());
			if (cb.getType().equals(ContentOntology.GROUP)
					|| cb.getType().equals(ContentOntology.MAIN_GROUP)) {
				ArrayList<String> refs = cb.getRef();
				createKlonkGroup(k, refs, cb.getType());
			} else {
				createKlonk(k);
			}
		}
		if (cb.getAction().equals(ContentOntology.ADD)) {
			ArrayList<String> refs = cb.getRef();
			String sto = cb.getTo();
			if (refs != null ) {
				addToGroup(refs, sto);
			}
		} 
		if (cb.getAction().equals(ContentOntology.EDIT)) {
			String sto = cb.getTo();
			if (sto != null) {
				KlonkTreeNode kn = graph.get(sto);
				kn.setContent(cb.getValue());
			}
		}
	}

	public void print() {
		print(getRoot(), 0);
	}

	private void print(KlonkTreeNode node, int indent) {
		for (int j = 0; j < indent; j++)
			System.out.print("-");
		System.out.println(node.getUserObject().getText());
		for (int i = 0; i < node.getChildCount(); i++)
			print(node.getChildAt(i), indent + 1);
	}

	public String printJason() {
		BSJason bsjason = printJason(getRoot());
		return bsjason.jason();
	}
	public BSJason getJasonFormat() {
		return printJason(getRoot());
	}
	private BSJason printJason(KlonkTreeNode node) {
		
		Klonk k = node.getUserObject();
		BSJason bsj = k.getJasonFormat();
		if (node.getChildCount() == 0) {
			bsj.setType(ContentOntology.POSTIT);
			bsj.setChildren(null);
		} else {
			bsj.setType(ContentOntology.GROUP);
			ArrayList<BSJason> children = new ArrayList<BSJason>();
			for (int i = 0; i < node.getChildCount(); i++) {
				children.add(printJason(node.getChildAt(i)));
			}
			bsj.setChildren(children);
		}
		return bsj;
	}
	
	public int getKlonkLevel(Klonk k) {
		  int n = -1;
		  KlonkTreeNode node = graph.get(k.getIdentification().getId());
		  if (node != null) {
			  n = node.getDepth();
		  }
		  return n;
	  }
	public Klonk getKlonk(String klonkID) {
		return graph.get(klonkID).getUserObject();
	}
}
