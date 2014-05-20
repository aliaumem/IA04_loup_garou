package utc.androTat.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Note implements Serializable {
	
	private static final long serialVersionUID = -8285761313002478498L;

	//La gestion des IDs sera après directement liée à la base SQLite
	static int nbid=0;
	
	private long id;
	private String title ="";
	private String OTId = null; // on table Id
	private String content ="";
	private ArrayList<String> tags = new ArrayList<String>();
	private String author;
	private String date="";
	private long sessionId;
	private boolean selected = false;
	
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public  static final String NOTES_TABLE_NAME = "notes";
	public  static final String NOTE_ID = "_id";
	public  static final String NOTE_TITLE = "title";
	public  static final String NOTE_CONTENT = "content";
	public  static final String NOTE_AUTHOR = "author";
	public  static final String NOTE_SESSION_ID = "sessionID";
	public  static final String NOTE_CREATION = "creation";
	
	public Note(String t, String c, ArrayList<String> tag, String a, String d,long s){
		this.id=nbid;
		this.title=t;
		this.content=c;
		this.tags=tag;
		this.author=a;
		this.sessionId=s;
		this.selected=false;
		this.date= d;
		nbid++;
	}
	
	public Note() {
		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.id=-1;
		this.title="";
		this.content="";
		this.tags= new ArrayList<String>();
		this.author="";
		this.selected=false;
		this.setSessionId((long) -1);
		this.date= DATE_FORMAT.format(new Date());
	}

	
	
	public String getDate() {
		return date;
	}
	
	public Calendar getDateAsCalendar(){
		Calendar a = Calendar.getInstance();
		try {
			a.setTime(DATE_FORMAT.parse(getDate()));
		} catch (ParseException e) {
			a.setTimeInMillis(0);
		}
		return a;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isOT(){
		return (OTId != null);
	}
	public String getOTId() {
		return OTId;
	}
	public void setOTId(String id) {
		this.OTId = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public void switchSelected() {
		this.selected = !this.selected;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
}
