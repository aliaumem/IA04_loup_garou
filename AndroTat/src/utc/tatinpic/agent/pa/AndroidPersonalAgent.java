package utc.tatinpic.agent.pa;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import utc.androTat.main.AndroTatActivity;
import utc.tatinpic.agent.exchange.BSModelObject;
import utc.tatinpic.model.BrainstormingModel;
import utc.tatinpic.semantics.BSJason;
import utc.tatinpic.semantics.Constants;
import utc.tatinpic.semantics.ContentOntology;
import utc.tatinpic.semantics.MessageContentBroker;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AndroidPersonalAgent extends Agent {
	
	private static final long serialVersionUID = 6565659362105171603L;
	Context ctx;
	AgentManager myManager;
	
	private static final String TAG = "AndroidPersonalAgent";
	
	@Override
	protected void setup() {
		Object[] args = getArguments();
		ctx = (Context) args[0];
		myManager = (AgentManager) args[1];
		myManager.setAgent(this);
		super.setup();
		addBehaviour(new ReceptionBehaviour());
	}
	
	public void createPostit(String toreceiver, String value) {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST); 
		String content = buildCreateKlonkContent(value != null ? value : "no text");
  	    message.setContent(content);
		AID receiver = new AID(toreceiver, AID.ISLOCALNAME);
		message.addReceiver(receiver);
		message.setReplyWith("Nothing");
		message.setProtocol(ContentOntology.EXTERNAL_PROTOCOL);
		addBehaviour(new SimpleMessageBehaviour(message));
	}
	public void modifyPostit(String toreceiver, BSJason klonk) {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST); 
		String content = buildModifyKlonkContent(klonk);
  	    message.setContent(content);
		AID receiver = new AID(toreceiver, AID.ISLOCALNAME);
		message.addReceiver(receiver);
		message.setReplyWith("Nothing");
		message.setProtocol(ContentOntology.EXTERNAL_PROTOCOL);
		addBehaviour(new SimpleMessageBehaviour(message));
	}
	public void deletePostit(String toreceiver, BSJason klonk) {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST); 
		String content = buildDeleteKlonkContent(klonk);
  	    message.setContent(content);
		AID receiver = new AID(toreceiver, AID.ISLOCALNAME);
		message.addReceiver(receiver);
		message.setReplyWith("Nothing");
		message.setProtocol(ContentOntology.EXTERNAL_PROTOCOL);
		addBehaviour(new SimpleMessageBehaviour(message));
	}
	public void viewAction(String toreceiver, String action, String idpodtit) {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST); 
		String content = buildViewActionContent(action, idpodtit);
  	    message.setContent(content);
		AID receiver = new AID(toreceiver, AID.ISLOCALNAME);
		message.addReceiver(receiver);
		message.setReplyWith("Nothing");
		message.setProtocol(ContentOntology.EXTERNAL_PROTOCOL);
		addBehaviour(new SimpleMessageBehaviour(message));
	}
	public void askModel(String toreceiver) {
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST); 
		message.setProtocol(ContentOntology.EXTERNAL_PROTOCOL);
		message.setLanguage(ContentOntology.JSON_LANGUAGE);
		AID receiver = new AID(toreceiver, AID.ISLOCALNAME);
		message.addReceiver(receiver);
		MessageContentBroker cb = new MessageContentBroker();	
		cb.putAction(ContentOntology.GET_MODEL);
		message.setContent(cb.jasonString());
		addBehaviour(new SimpleMessageBehaviour(message));
	}
	public void askPdf(String toreceiver) {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setProtocol(ContentOntology.EXCHANGE_PROTOCOL);
        AID receiver = new AID(toreceiver, AID.ISLOCALNAME);
        message.addReceiver(receiver);
        message.addReplyTo(getAID());
        MessageContentBroker cb = new MessageContentBroker();     
        cb.putAction(ContentOntology.GET_MODEL);
        cb.putArg(ContentOntology.EVENT, Constants.PDF_BYTE_EVENT);
        message.setContent(cb.jasonString());
        addBehaviour(new SimpleMessageBehaviour(message));
	}
	private String buildCreateKlonkContent(String value) {
	  MessageContentBroker cb = new MessageContentBroker();	
	  String content = null;
	  cb.putAction(ContentOntology.CREATE);
	  cb.putArg(ContentOntology.TYPE, ContentOntology.POSTIT);
	  cb.putArg(ContentOntology.VALUE, value);	
	  content = cb.jasonString();
	  return content;
    }
	private String buildModifyKlonkContent(BSJason klonk) {
		  MessageContentBroker cb = new MessageContentBroker();	
		  String content = null;
		  cb.putAction(ContentOntology.EDIT);
		  cb.putArg(ContentOntology.TO, klonk.getId());
		  cb.putArg(ContentOntology.VALUE, klonk.getValue());	
		  content = cb.jasonString();
		  return content;
	}
	private String buildDeleteKlonkContent(BSJason klonk) {
		  MessageContentBroker cb = new MessageContentBroker();	
		  String content = null;
		  cb.putAction(ContentOntology.DELETE);
		  cb.putArg(ContentOntology.TO, klonk.getId());
		  content = cb.jasonString();
		  return content;
	}
	private String buildViewActionContent(String action, String idpodtit) {
		MessageContentBroker cb = new MessageContentBroker();	
		  String content = null;
		  cb.putAction(action);
		  cb.putArg(ContentOntology.TO, idpodtit);
		  content = cb.jasonString();
		  return content;
	}
	private class SimpleMessageBehaviour extends OneShotBehaviour {

		private static final long serialVersionUID = 7375640376942587675L;
		ACLMessage message;
		
		public SimpleMessageBehaviour(ACLMessage message) {
			super();
            this.message = message;
		}
    
		@Override
		public void action() {
			   send(message); 
		}	
   }
	private class ReceptionBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = -8494999421130601107L;

		@Override
		public void action() {
			ACLMessage message = myAgent.receive();
			if (message != null) {
				Intent broadcast = new Intent();
				String language = message.getLanguage();
				/**
				 * Receive a message with an object (getMessageObject())
				 */
				
				if(language != null && language.equals(ContentOntology.OBJECT_LANGUAGE)){
					/* BS model */
					try {
						
						if(message.getProtocol().equals(ContentOntology.EXCHANGE_PROTOCOL)){
							// PDF
							byte[] content = (byte[]) message.getContentObject();
							broadcast.setAction(AndroTatActivity.INTENT_ACTION_PDF_OK);
							broadcast.putExtra(AndroTatActivity.INTENT_EXTRA_PDF, content);
							ctx.sendBroadcast(broadcast);
							
						}else{
						
							BSModelObject bsmo = (BSModelObject) message.getContentObject();
							BrainstormingModel bsm = bsmo.getContent();
							broadcast.setAction(AndroTatActivity.INTENT_ACTION_MODEL_OK);
							broadcast.putExtra(AndroTatActivity.INTENT_EXTRA_MODEL, bsm);
							ctx.sendBroadcast(broadcast);
						
						}
						
					} catch (Exception e) {
						broadcast.setAction(AndroTatActivity.INTENT_ACTION_ERROR);
						broadcast.putExtra(AndroTatActivity.INTENT_EXTRA_ERROR, "Erreur with object : "+e.getMessage());
						Log.e(TAG, e.getClass().getName()+":"+e.getMessage());
						e.printStackTrace();
						ctx.sendBroadcast(broadcast);
					}
					
				}else if (language != null && language.equals(ContentOntology.JSON_LANGUAGE)) {
					/* BSJason model */
					try {
						String content = message.getContent();
						broadcast.setAction(AndroTatActivity.INTENT_ACTION_MODEL_OK);
						broadcast.putExtra(AndroTatActivity.INTENT_EXTRA_MODEL, content);
						ctx.sendBroadcast(broadcast);
					} catch (Exception e) {
						broadcast.setAction(AndroTatActivity.INTENT_ACTION_ERROR);
						broadcast.putExtra(AndroTatActivity.INTENT_EXTRA_ERROR, "Erreur with object : "+e.getMessage());
						Log.e(TAG, e.getClass().getName()+":"+e.getMessage());
						e.printStackTrace();
						ctx.sendBroadcast(broadcast);
					}
				}
				else {  
				/**
				 * Receive a message with a string content
				 */
					broadcast.setAction(AndroTatActivity.STRING_ANSWER);
					String[] parameters = new String[]{"B"+message.getContent()};
				    Log.v("agent",message.getContent());
					broadcast.putExtra(AndroTatActivity.INTENT_PARAMETERS, parameters);
					ctx.sendBroadcast(broadcast);
				}
				
			}
			else block();
		}
	}
}
