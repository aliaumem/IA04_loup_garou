package utc.androTat.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ToDelete_Notes {
	//La gestion des IDs sera après directement liée à la base SQLite
	static int nbid=0;
	
	private int id;
	private String title ="";
	private String content ="";
	private Vector<String> tags = new Vector<String>();
	private String author;
	private boolean selected = false;
	private String date="";
	
	ToDelete_Notes(String t, String c, Vector<String> tag, String a){
		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.id=nbid;
		this.title=t;
		this.content=c;
		this.tags=tag;
		this.author=a;
		this.selected=false;
		this.date= DATE_FORMAT.format(new Date());
		nbid++;
	}
	
	
	
	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public Vector<String> getTags() {
		return tags;
	}
	public void setTags(Vector<String> tags) {
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
	
}
