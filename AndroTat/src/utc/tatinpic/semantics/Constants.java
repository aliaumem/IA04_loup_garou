package utc.tatinpic.semantics;

public class Constants {
  public static String TABLE_AGENT = "TableAgent";
  public static String BOARD_AGENT = "BoardAgent";
  public static String BOARD_DIRECTORY = "rsc/transfer/";
  public static String SOCKET_AGENT = "socket";
  public static String BRAINSTORMING_AGENT = "BrainstormingAgent";
  public static String PLANNING_AGENT = "PlanningAgent";
  
  public static String UTC_INET_ADDRESS = "172.17.255.255";
  public static String OMAS_INET_ADDRESS = "172.17.131.103";
  public static String HOME_IPADDRESS = "192.168.100.11";
  public static String OFFICE_IPADDRESS = "172.17.131.103";
  public static String TG_IPADDRESS = "172.17.1.34";
  public static String O108_IPADDRESS = "172.21.131.102";
  public static String TABLEX_IPADDRESS = "172.17.131.121";
  
  public static int OMAS_SOCKET_PORT = 40001; // OMAS port for OMAS-JADE communication
  public static int JADE_SOCKET_PORT = 40002; // JADE port for OMAS-JADE communication
  public static int DEMO_SOCKET_PORT = 40001; // port for OMAS-JADE communication demo on the same station
  public static int BUFFER_SIZE = 61440;  // Buffer size for OMAS-JADE communication

  // Regular expression for  player
  public static String re = "[/]{2} *((?:\\w|[:-])+)";
  
  
  //Event for querying phase model under a specific form
  public static int EXCEL_EVENT = 0;
  public static int MM_EVENT = 1;
  public static int PDF_EVENT = 2;
  public static int BRAINSTORMING_PHASE_EVENT = 3;
  public static int PDF_BYTE_EVENT  = 4;
  
  // Asked by the whiteboard to the table for having the lists of phase names
  public static int LIST_PHASE_EVENT = 4;
  
  // Application event
  public static String NEW_SESSION = "new-session";
  public static String AGENT_GUI_EVENT = "gui-event";
  public static String START_JADE_PLATFORM = "start-jade";
  public static String SELECT_PHASE = "select-phase";
}
