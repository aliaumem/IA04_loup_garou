package utc.androTat.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbOperations {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;

	public DbOperations(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/* NOTE PART */ 

	public Note createNote(String title, String content, String author, Long sessionId, ArrayList<Tag> tags) {
		final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ContentValues values = new ContentValues();
		values.put("content", content);
		values.put("title", title);
		values.put("author", author);
		values.put("sessionID",sessionId);
		values.put("creation", DATE_FORMAT.format(new Date()));

		long insertId = database.insert(DbHelper.NOTES_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbHelper.NOTES_TABLE_NAME, DbHelper.NOTES_COLUMNS,
				DbHelper.NOTE_ID+" = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Note newNote = cursorToNote(cursor);
		addTagsToNote(newNote, tags);
		cursor.close();
		return newNote;
	}

	public void addTagsToNote(Note n, List<Tag> tags){
		ContentValues values = new ContentValues();
		long nID = n.getId();

		for(Tag t:tags){
			values.put("noteID", nID);
			values.put("tagID", t.getId());
			database.insert(DbHelper.LINKS_TABLE_NAME,null, values);
			values.clear();
		}
	}

	public void addTagToNote(Note n, Tag tag) {
		ContentValues values = new ContentValues();
		long nID = n.getId();
		values.put("noteID", nID);
		values.put("tagID", tag.getId());
		database.insert(DbHelper.LINKS_TABLE_NAME,null, values);
		values.clear();
	}

	public void addNotesToTag(){}

	public void deleteNote(Note note) {
		long id = note.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(DbHelper.NOTES_TABLE_NAME, DbHelper.NOTE_ID + " = " + id, null);
	}

	public Cursor getAllNotes(String whereClause) {
		Cursor cursor = database.query(DbHelper.NOTES_TABLE_NAME,
				DbHelper.NOTES_COLUMNS, whereClause, null, null, null, null);
		return cursor;
	}


	public Cursor getTagsForNote(long id) {

		String query = "SELECT *" + 
				" FROM "+ DbHelper.TAGS_TABLE_NAME +" JOIN " + DbHelper.LINKS_TABLE_NAME + 
				" ON ("+DbHelper.TAG_ID +" = "+DbHelper.LINK_TAG_ID + ") " +
				" WHERE "+DbHelper.LINK_NOTE_ID+" = "+ id;

		return database.rawQuery(query,null);

	}

	public Cursor getAllNotesForDate(String d){
		return getAllNotes(DbHelper.NOTE_CREATION+" BETWEEN '"+d+" 00:00:00' AND '"+d+" 23:59:59'");
	}

	public Cursor getAllNotesForSession(Session s){
		return getAllNotes("sessionID = " + s.getId());
	}
	


	public Cursor getAllNotesForTag(String t){
		String query = "SELECT " + DbHelper.NOTES_COLUMNS_STRING + 
				" FROM "+ DbHelper.NOTES_TABLE_NAME +" JOIN " + DbHelper.LINKS_TABLE_NAME + 
				" ON ("+DbHelper.LINK_NOTE_ID+" = "+DbHelper.NOTE_ID+") " +
				" WHERE "+DbHelper.LINK_TAG_ID+" = "+ t;
		return database.rawQuery(query,null);
	}

	public Cursor getNote(long t){
		return getAllNotes("_id = " + t);
	}

	public void updateNote(Note n){
		ContentValues values = new ContentValues();
		long nID = n.getId();
		values.put("content", n.getContent());
		values.put("title", n.getTitle());
		values.put("author", n.getAuthor());
		values.put("sessionID", n.getSessionId());
		values.put("creation", n.getDate());
		database.update(DbHelper.NOTES_TABLE_NAME, values, "_id = "+nID,null);
	}

	public Note cursorToNote(Cursor cursor) {
		Note note = new Note(
				cursor.getString(1),
				cursor.getString(2),
				new ArrayList<String>(),
				cursor.getString(3),cursor.getString(4),cursor.getLong(5));
		note.setId(cursor.getLong(0));
		Cursor curs = getTagsForNote(note.getId());
		ArrayList<Tag> tags = cursorToTagList(curs);
		note.setTags(convertTagListToStringList(tags));
		return note;
	}
	
	public static ArrayList<String> convertTagListToStringList(ArrayList<Tag> tags) {
		ArrayList<String> sTags = new ArrayList<String>();
		Iterator<Tag> it = tags.iterator();
		while (it.hasNext()) {
			sTags.add(((Tag)it.next()).getName());
		}
		return sTags;
	}


	public Vector<Note> cursorToNoteList(Cursor cursor) {
		Vector<Note> notes = new Vector<Note>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Note note = cursorToNote(cursor);
			notes.add(note);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return notes;
	}

	/* GET DATES */

	public Cursor getAllDates(){
		Cursor cursor = database.rawQuery("SELECT DISTINCT date(creation) AS creation, 0 AS '_id' FROM notes", new String[]{});
		return cursor;
	}


	/* TAG PART */

	public Tag createTag(String name) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.TAG_NAME, name);

		long insertId = database.insert(DbHelper.TAGS_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbHelper.TAGS_TABLE_NAME, DbHelper.TAG_COLUMNS,
				DbHelper.TAG_ID + " = " + insertId, null,null, null, null);

		cursor.moveToFirst();
		Tag newTag = cursorToTag(cursor);
		cursor.close();
		return newTag;
	}

	public void deleteTag(Tag t) {
		long id = t.getId();
		database.delete(DbHelper.TAGS_TABLE_NAME, DbHelper.TAG_ID + " = " + id, null);
		database.delete(DbHelper.LINKS_TABLE_NAME, DbHelper.LINK_TAG_ID + " = " + id, null);
	}
	
	public void deleteTagForNote(Tag t, Note n) {
		//long id = t.getId();
		database.delete(DbHelper.LINKS_TABLE_NAME, DbHelper.LINK_NOTE_ID + " = " + n.getId() + 
				" AND " + DbHelper.LINK_TAG_ID + " = " + t.getId(), null);
	}

	public Cursor getAllTags() {
		Cursor cursor = database.query(DbHelper.TAGS_TABLE_NAME,
				DbHelper.TAG_COLUMNS, null, null, null, null, null);
		return cursor;
	}

	public Tag cursorToTag(Cursor cursor) {
		Tag t = new Tag();
		t.setId(cursor.getLong(0));
		t.setName(cursor.getString(1));
		return t;
	}
	
	public ArrayList<Tag> cursorToTagList(Cursor cursor) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Tag t = cursorToTag(cursor);
			tags.add(t);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tags;
	}



	/* SESSION PART */

	public Session createSession(String name) {
		ContentValues values = new ContentValues();
		values.put("name", name);

		long insertId = database.insert(DbHelper.SESSIONS_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbHelper.SESSIONS_TABLE_NAME, DbHelper.SESSION_COLUMNS,
				DbHelper.SESSION_ID + " = " + insertId, null,null, null, null);

		cursor.moveToFirst();
		Session newSession = cursorToSession(cursor);
		cursor.close();
		return newSession;
	}

	public void deleteSession(Session s) {
		long id = s.getId();
		database.delete(DbHelper.SESSIONS_TABLE_NAME, DbHelper.SESSION_ID + " = " + id, null);
	}

	public Cursor getAllSessions() {
		Cursor cursor = database.query(DbHelper.SESSIONS_TABLE_NAME,
				DbHelper.SESSION_COLUMNS, null, null, null, null, null);
		return cursor;
	}
	public Session getSessionWithId(Session s){
		long id = s.getId();
		Log.e("GG", "id="+id);
		Cursor curSess = database.query(DbHelper.SESSIONS_TABLE_NAME, DbHelper.SESSION_COLUMNS, DbHelper.SESSION_ID + " = " + id,null, null, null, null);
		curSess.moveToFirst();
		Session newSession = cursorToSession(curSess);
		curSess.close();
		return newSession;
	}

	public Session cursorToSession(Cursor cursor) {
		Session s = new Session();
		s.setId(cursor.getLong(0));
		s.setName(cursor.getString(1));
		return s;
	}
	public Vector<Session> cursorToSessionList(Cursor cursor) {
		Vector<Session> sessions = new Vector<Session>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Session s = cursorToSession(cursor);
			sessions.add(s);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return sessions;
	}
	/**
	 *  TABLE PART
	 */
	
	//Permet la création de table, pour la connection, dans les préférences, en base de donnée
	public Table createTable(String name, String ip) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.TABLE_NAME, name);
		values.put(DbHelper.TABLE_IP, ip);
		
		long insertId = database.insert(DbHelper.TABLES_TABLE_NAME, null, values);
		Cursor cursor = database.query(DbHelper.TABLES_TABLE_NAME, DbHelper.TABLE_COLUMNS,
							DbHelper.TABLE_ID+" = " + insertId, null,null, null, null);
		cursor.moveToFirst();
		Table newTable = cursorToTable(cursor);
		cursor.close();
		return newTable;
	}
	
	//pour avoir la table pointée
	public Table cursorToTable(Cursor cursor) {
		Table t = new Table();
		t.setId(Long.parseLong(cursor.getString(0)));
		t.setName(cursor.getString(1));
		t.setIp(cursor.getString(2));
		return t;
	}
	
	public void deleteTable(long id) {
		database.delete(DbHelper.TABLES_TABLE_NAME, DbHelper.TABLE_ID + " = " + id, null);
	}
	
	public Cursor getAllTables() {
		Cursor cursor = database.query(DbHelper.TABLES_TABLE_NAME,
				DbHelper.TABLE_COLUMNS, null, null, null, null, null);
		return cursor;
	}
	
	public Vector<Table> cursorToTableList(Cursor cursor) {
		Vector<Table> tables = new Vector<Table>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Table t = cursorToTable(cursor);
			tables.add(t);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tables;
	}
	
	public Cursor searchResult(String query) {
	    String requete = "SELECT *" + 
				" FROM "+ DbHelper.NOTES_TABLE_NAME +
				" WHERE " + DbHelper.NOTE_TITLE + " LIKE \"%" + query + "%\"" +
				" OR " + DbHelper.NOTE_CONTENT + " LIKE \"%" + query + "%\"";

	    Cursor cursor = database.rawQuery(requete,null);
	    if (cursor == null) {
	        return null;
	    } 
	    else if (cursor.getCount()==0){
	    	return null;
	    }
	    else if (!cursor.moveToFirst()) {
	        cursor.close();
	        return null;
	    }
	    return cursor;
	}
}
