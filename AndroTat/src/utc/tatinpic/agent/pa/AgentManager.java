package utc.tatinpic.agent.pa;

import jade.android.MicroRuntimeService;
import jade.android.RuntimeCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import utc.androTat.main.AndroTatActivity;
import utc.androTat.model.Note;
import utc.tatinpic.model.BrainstormingModel;
import utc.tatinpic.model.Klonk;
import utc.tatinpic.model.KlonkTreeNode;
import utc.tatinpic.semantics.BSJason;
import utc.tatinpic.semantics.Constants;
import utc.tatinpic.semantics.ContentOntology;
import android.content.Context;
import android.content.Intent;

public class AgentManager {
	
	public static final String RESULT_CONNECTED = "connected";
	public static final String RESULT_DISCONNECTED = "disconnected";
	
	@SuppressWarnings("unused")
	private static final String TAG = "AgentManager";

	private boolean isConnected = false;
	private MicroRuntimeService mr;
	private String participant;
	private String agentName;
	private AndroidPersonalAgent agent;
	private Context ctx;	

	public synchronized void setAgent(AndroidPersonalAgent a){agent = a;}
	public synchronized AndroidPersonalAgent getAgent(){return agent;}


	public synchronized void clean(){
		clean(false);
	}
	public synchronized void clean(boolean keepCtx){
		if(mr != null) mr.stopAgentContainer(new RuntimeCallback<Void>(){
			public void onFailure(Throwable arg0) {}
			public void onSuccess(Void arg0) {}
		});
		mr = null;
	}

	public synchronized void doConnect(Context _ctx, String ip, String _name){
		if(isConnected) throw new IllegalStateException("Already connected to a table");
		mr = new MicroRuntimeService();
		participant = _name;
		agentName = _name+"-pa";
		ctx = _ctx;

		mr.startAgentContainer(ip, 1099, new RuntimeCallback<Void>() {
			
			public void onFailure(Throwable arg0) {
				sendErrorToUI(arg0);
				clean();
			}
			
			public void onSuccess(Void arg0) {
				startAgent();
			}
		});
	}

	private synchronized void startAgent(){
		mr.startAgent(agentName, AndroidPersonalAgent.class.getName(), new Object[]{ctx, this}, new RuntimeCallback<Void>() {
			
			public void onFailure(Throwable arg0) {
				sendErrorToUI(arg0);				
				clean();
			}

			public void onSuccess(Void arg0) {
				isConnected = true;
				sendSuccessToUI(AndroTatActivity.INTENT_ACTION_CONNECTED);
			}
		});

	}

	public synchronized void doDisconnect(){
		if(!isConnected) return;
		/* to check */
		if(agent != null) agent.doDelete();
		mr.stopAgentContainer(new RuntimeCallback<Void>(){
		
			public void onFailure(Throwable e) {
				sendErrorToUI(new Exception("unable to disconnect : "+e.getMessage()));				
			}

			public void onSuccess(Void _null) {
				sendSuccessToUI(AndroTatActivity.INTENT_ACTION_DISCONNECTED);
				mr =null;
			}
		});
		clean();
		isConnected = false;
	}
	
	public synchronized void sendErrorToUI(Throwable e){
		Intent i = new Intent(AndroTatActivity.INTENT_ACTION_ERROR);
		i.putExtra(AndroTatActivity.INTENT_EXTRA_ERROR, e.getMessage());
		ctx.sendBroadcast(i);
	}
	
	public synchronized void sendSuccessToUI(String res){
		Intent i = new Intent(res);
		ctx.sendBroadcast(i);
	}
	
	public synchronized void createPostit(String value) {
		agent.createPostit(ContentOntology.formatWorkbenchAgentName(participant), value);
	}
	public synchronized void modifyPostit(BSJason klonk) {
		agent.modifyPostit(ContentOntology.formatWorkbenchAgentName(participant), klonk);
	}
	public synchronized void deletePostit(BSJason klonk) {
		agent.deletePostit(ContentOntology.formatWorkbenchAgentName(participant), klonk);
	}
	public synchronized void viewAction(String action, String idpodtit) {
		agent.viewAction(ContentOntology.formatWorkbenchAgentName(participant), action, idpodtit);
	}
	public synchronized void askModel() {
		agent.askModel(ContentOntology.formatWorkbenchAgentName(participant));
	}
	public void askPdf() {
		agent.askPdf(Constants.BRAINSTORMING_AGENT);
	}
	
	public static Note convert(Klonk k){
		Note n = new Note();
		n.setContent(k.getText());
		n.setTitle(k.getIdentification().getId());
		n.setAuthor(k.getIdentification().getAuthor());
		return n;
	}
	
	public static BSJason convert(Note n){
		BSJason k = new BSJason();
		k.setId(n.getOTId());
		k.setValue(n.getContent());
		return k;
	}
	
	public static Vector<Note> convert(BrainstormingModel bsm){
		Vector<Note> listN = new Vector<Note>();
		Collection<KlonkTreeNode> listK = bsm.graph.values();
		
		for(KlonkTreeNode ktn : listK){
			listN.add(convert(ktn.getUserObject()));
		}
		
		return listN;
	}
	
	public static Note convert(BSJason bsj){
		Note n = new Note();
		n.setContent(bsj.getValue());
		n.setOTId(bsj.getId());
		n.setId(0L);
		return n;
	}
	
	public static Vector<Note> convertToList(BSJason bsj){
		Vector<Note> list = new Vector<Note>();
		ArrayList<BSJason> c = bsj.getChildren();
		if(c==null || c.size() == 0 ) list.add(convert(bsj));
		else for(BSJason b : bsj.getChildren()){
			list.addAll(convertToList(b));
		}
		return list;
	}

	
}
