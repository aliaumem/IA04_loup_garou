package utc.tatinpic.semantics;

public class ContentOntology {
	public static final String EXCHANGE_PROTOCOL = "table2board";
	// Message properties
	public static String ACTION = "action" ;
	public static String ARGS = "args" ;
	public static String ANSWER = "answer";
	public static String ERROR_CONTENTS = "error-contents";
	
	public static String CONTENT = "content" ;
	
	// Args elements
	public static String CATEGORY = "category" ;
	public static String TYPE = "type" ;
	
	public static String VALUE = "value" ;
	public static String REF = "ref" ;
	public static String TO = "to" ;
	
	public static String OBJECT_LANGUAGE = "object";
	public static String JSON_LANGUAGE = "jason";
	public static String PHASE_NAME = "phase-name";
	public static String EVENT = "event";
	
	// Actions on model
	public static String CREATE = "create" ;
	public static String ADD = "add" ;
	public static String EDIT = "edit" ;
	public static String DELETE = "delete" ;
	public static String REMOVE = "remove" ;
	public static String GET_OBJECT = "get-object" ;
	public static String GET_MODEL = "get-model" ;
	
	public static String[] MODEL_ACTIONS = {
		CREATE, ADD, EDIT, DELETE, REMOVE, GET_MODEL 
	};
	// Actions on view
	public static String GET_SELECTED_ITEMS = "get-selected-items" ;
	public static String SELECT = "select";
	public static String UNSELECT = "unselect";
	public static String UNSELECTALL = "unselectall";
	public static String HIGHLIGHT = "highlight";
	
    public static String[]  VIEW_ACTIONS = {
    	GET_SELECTED_ITEMS, SELECT, UNSELECT, UNSELECTALL, HIGHLIGHT
	};
	
	public static String ITEM_SELECTED = "item-selected";
	public static String ITEM_UNSELECTED = "item-unselected";
	
	public static String SELECT_PHASE = "select-phase";
	public static String COMPLETE_TASK = "complete-task";
	public static String UPDATE_TASK = "update-task";
	
	
	// Post-it
	public static String POSTIT = "postit" ;
	public static String GROUP = "group" ;
	public static String MAIN_GROUP = "maingroup" ;
	public static String KLONK_ID = "klonk-id";
	public static String JASON_KLONK = "jason-klonk";
	
	// Performative
	public static int PERFORMATIVE_ANSWER = 20;
	
	// Protocole
	public static String EXTERNAL_PROTOCOL = "external";
	// Format agent name
	public static String formatWorkbenchAgentName(String name) {
		return name + "-workbench"; 
	}
	public static String formatPAAgentName(String agentName) {
		return getParticipantName(agentName) + "-pa"; 
	}
	public static String getParticipantName(String agentName) {
		int index = agentName.indexOf("-workbench");
		return agentName.substring(0, index); 
	}
}
