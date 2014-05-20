package utc.androTat.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DbHelper";

	private static final boolean POPULATE_DB = true;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
	
	private static final int DATABASE_VERSION = 16;
	private static final String DATABASE_NAME = "androtat";

	public  static final String SESSIONS_TABLE_NAME = "sessions";
	public  static final String SESSION_ID = "_id";
	public  static final String SESSION_NAME = "name";
	public  static final String[] SESSION_COLUMNS = {SESSION_ID, SESSION_NAME}; 
	private static final String SESSIONS_TABLE_CREATE =
	                "CREATE TABLE " + SESSIONS_TABLE_NAME + " (" +
	                SESSION_ID+" integer primary key autoincrement," +
	                SESSION_NAME+" TEXT" +
	                ");";
	
	public  static final String NOTES_TABLE_NAME = "notes";
	public  static final String NOTE_ID = "_id";
	public  static final String NOTE_TITLE = "title";
	public  static final String NOTE_CONTENT = "content";
	public  static final String NOTE_AUTHOR = "author";
	public  static final String NOTE_SESSION_ID = "sessionID";
	public  static final String NOTE_CREATION = "creation";

	public  static final String[] NOTES_COLUMNS = {NOTE_ID, NOTE_TITLE, NOTE_CONTENT, NOTE_AUTHOR, NOTE_CREATION, NOTE_SESSION_ID};
	public  static final String NOTES_COLUMNS_STRING = NOTE_ID+", " + NOTE_TITLE+", " + NOTE_CONTENT+", " + NOTE_AUTHOR+", "+ NOTE_CREATION + ", " + NOTE_SESSION_ID;
	private static final String NOTES_TABLE_CREATE =
	                "CREATE TABLE " + NOTES_TABLE_NAME + " (" +
	                NOTE_ID + " integer primary key autoincrement," + 
	                NOTE_TITLE + " TEXT, " +
	                NOTE_CONTENT + " TEXT," +
	                NOTE_AUTHOR + " TEXT," +
	                NOTE_SESSION_ID + " INTEGER," +
	                NOTE_CREATION + " DATETIME," +
	                "FOREIGN KEY("+NOTE_SESSION_ID+") REFERENCES "+SESSIONS_TABLE_NAME+" ("+SESSION_ID+")" +
            		");";
	
	public  static final String TAGS_TABLE_NAME = "tags";
	public  static final String TAG_ID = "_id";
	public  static final String TAG_NAME = "name";
	public  static final String[] TAG_COLUMNS = {TAG_ID, TAG_NAME};
	private static final String TAGS_TABLE_CREATE =
	                "CREATE TABLE " + TAGS_TABLE_NAME + " (" +
	                TAG_ID+" integer primary key autoincrement," +
	                TAG_NAME+" TEXT," +
	                "color TEXT" +
	                ");";
	
	public  static final String LINKS_TABLE_NAME = "link_tag_note";
	public  static final String LINK_NOTE_ID = "noteID";
	public  static final String LINK_TAG_ID = "tagID";
	private static final String LINKS_TABLE_CREATE =
	                "CREATE TABLE " + LINKS_TABLE_NAME + " (" +
	                LINK_NOTE_ID + " INTEGER, " +
	                LINK_TAG_ID + " INTEGER," +
	                "FOREIGN KEY("+LINK_NOTE_ID+") REFERENCES "+NOTES_TABLE_NAME+"("+NOTE_ID+")," +
            		"FOREIGN KEY("+LINK_TAG_ID+") REFERENCES "+TAGS_TABLE_NAME+"("+TAG_ID+")" +
            		");";
	
	public  static final String TABLES_TABLE_NAME = "tables";
	public  static final String TABLE_ID = "_id";
	public  static final String TABLE_NAME = "name";
	public  static final String TABLE_IP = "ip";
	public  static final String[] TABLE_COLUMNS = {TABLE_ID, TABLE_NAME, TABLE_IP}; 
	private static final String TABLES_TABLE_CREATE =
	                "CREATE TABLE " + TABLES_TABLE_NAME + " (" +
	                TABLE_ID +" integer primary key autoincrement," +
	                TABLE_NAME +" TEXT," + TABLE_IP + " TEXT" +
	                ");";
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v(TAG, "IN ON CREATE");
		db.execSQL(NOTES_TABLE_CREATE);
		db.execSQL(TAGS_TABLE_CREATE);
		db.execSQL(SESSIONS_TABLE_CREATE);
		db.execSQL(LINKS_TABLE_CREATE);
		db.execSQL(TABLES_TABLE_CREATE);
		populate(db);
	}
	
	private void populate(SQLiteDatabase db){
		if(!POPULATE_DB) return;
		
		ContentValues values = new ContentValues();
		values.put(SESSION_NAME, "session1");
		long s1id = db.insert(DbHelper.SESSIONS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(SESSION_NAME, "session2");
		db.insert(DbHelper.SESSIONS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(NOTE_SESSION_ID, s1id);
		values.put(NOTE_TITLE, "note1");
		values.put(NOTE_CONTENT, "note1");
		values.put(NOTE_AUTHOR, "bob");
		values.put(NOTE_CREATION, DATE_FORMAT.format(new Date(110, 12, 9)));
		long n1id = db.insert(DbHelper.NOTES_TABLE_NAME, null, values);
		values.clear();
		
		values.put(NOTE_SESSION_ID, s1id);
		values.put(NOTE_TITLE, "note2");
		values.put(NOTE_CONTENT, "note2");
		values.put(NOTE_AUTHOR, "bob");
		
		values.put(NOTE_CREATION, DATE_FORMAT.format(new Date(112,2,4)));
		long n2id = db.insert(DbHelper.NOTES_TABLE_NAME, null, values);
		values.clear();
		
		values.put(TAG_NAME, "tag1");
		long t1id = db.insert(DbHelper.TAGS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(TAG_NAME, "tag2");
		long t2id = db.insert(DbHelper.TAGS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(LINK_TAG_ID, t1id);
		values.put(LINK_NOTE_ID, n1id);
		db.insert(DbHelper.LINKS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(LINK_TAG_ID, t1id);
		values.put(LINK_NOTE_ID, n2id);
		db.insert(DbHelper.LINKS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(LINK_TAG_ID, t2id);
		values.put(LINK_NOTE_ID, n1id);
		db.insert(DbHelper.LINKS_TABLE_NAME, null, values);
		values.clear();
		
		values.put(TABLE_NAME, "tablex");
		values.put(TABLE_IP, "172.26.134.104");
		db.insert(DbHelper.TABLES_TABLE_NAME, null, values);
		values.clear();
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+ NOTES_TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ TAGS_TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ SESSIONS_TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ LINKS_TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ TABLES_TABLE_NAME+";");
		onCreate(db);
	}

}
