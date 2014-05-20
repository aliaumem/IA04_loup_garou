package utc.androTat.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import utc.androTat.model.DbHelper;
import utc.androTat.model.DbOperations;
import utc.androTat.model.Note;
import utc.androTat.model.Session;
import utc.androTat.model.Table;
import utc.androTat.model.Tag;
import utc.tatinpic.agent.pa.AgentManager;
import utc.tatinpic.semantics.BSJason;
import utc.tatinpic.semantics.ContentOntology;
import utc.tatinpic.semantics.MessageContentBroker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AndroTatActivity extends Activity  {
	private static final int CODE_SETTINGS_ACTIVITY = 1;
	private static final int CODE_CREATE_ACTIVITY = 2;
	private static final int CODE_EDIT_ACTIVITY = 3;

	public static final String INTENT_ACTION_CONNECTED = "utc.androTat.agent.connected";
	public static final String INTENT_ACTION_DISCONNECTED = "utc.androTat.agent.disconnected";
	public static final String INTENT_ACTION_ERROR = "utc.androTat.agent.error";
	public static final String INTENT_ACTION_MODEL_OK = "utc.androTat.agent.hasModel";
	public static final String INTENT_ACTION_PDF_OK = "utc.androTat.agent.hasPdf";
	public static final String STRING_ANSWER = "utc.androTat.agent.other"; 

	public static final String INTENT_EXTRA_ERROR = "error";
	public static final String INTENT_EXTRA_MODEL = "modelObject";
	public static final String INTENT_EXTRA_PDF = "pdfObject";

	private static final String TAG = "AndroTatActivity";
	public static final String INTENT_PARAMETERS = null; 

	private NoteAdapter nAdapter = new NoteAdapter();
	SimpleCursorAdapter sAdapter;
	ArrayAdapter<String> pAdapter;
	SimpleCursorAdapter tAdapter;
	SimpleCursorAdapter dAdapter;
	DbOperations dbo;

	Cursor cursorD = null;
	Cursor cursorS = null;
	Cursor cursorT = null;

	private Button buttonPDF =null;
	private Button buttonSessions =null;
	private Button buttonDates =null;
	private Button buttonTags =null;
	private Button connectTable = null;
	private LinearLayout contentConnectedSlider = null;
	private Button summaryButton = null;
	private Button managePostItsButton = null;
	private Spinner spinnerTable = null;
	private ImageButton openConnectTableButton = null;

	private TextView sendToTableView = null;
	private TextView titleView = null;

	private SlidingDrawer tableSliding = null;
	private Tag selectedTagElement;

	//Pour adapter l'affichage
	int typeSection = R.string.empty;

	private ListView listSessions;
	private ListView listTags;
	private ListView listDates;
	private ListView listPDF;
	private View leftMenu;
	private Note selectedNote;

	//	private RelativeLayout tableLayout;
	//	private MarginLayoutParams tableLayoutParams;

	private AgentManager agentManager = new AgentManager();
	private AgentIntentReceiver receiver;
	private boolean isWorkingOnConnection = false;
	private boolean isConnectedTable = false;

	//Liste des notes
	GridView  listeNotes = null;
	Vector<Note> listNotes = new Vector<Note>();

	//Les param?tres
	String login = "";
	Table table_connectee = new Table();
	Cursor table_cursor = null;

	//Cr?ation de l'actionbar
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar, menu);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}

	//Action pour chaque menuItem clique
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent();
		nAdapter = (NoteAdapter) listeNotes.getAdapter();
		switch (item.getItemId()) {
		case R.id.itemSelect:
			nAdapter.selectAllNotes();
			return true;
		case R.id.itemNew:
			i = new Intent(this, CreateEditActivity.class);
			startActivityForResult(i, CODE_CREATE_ACTIVITY);
			return true;
		case R.id.itemRemove:
			if (nAdapter.numberSelectedNote() > 0) removeItemDialog(nAdapter);
			return true;
		case R.id.itemParam:
			i = new Intent(this, ParameterActivity.class);
			startActivityForResult(i, CODE_SETTINGS_ACTIVITY);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//Permet de r?cup?rer le login.
	private String getLogin() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String login = preferences.getString("login", "");
		return login;
	}

	//Permet de r?cup?rer la list de table.
	/*private void getTables() {
		Cursor tables_cursor = dbo.getAllTables();
		tables = dbo.cursorToTableList(tables_cursor);
	}*/

	//Lance un dialogue qui demande la confirmation ˆ l'utilisateur pour supprimer les notes sŽlectionnŽes.
	private void removeItemDialog(final NoteAdapter nAdapterR) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Etes-vous sur de vouloir supprimer ces notes ?")
		.setCancelable(false)
		.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				nAdapterR.removeListItem();
			}
		})
		.setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}



	protected void onDestroy() {
		super.onDestroy();
		agentManager.doDisconnect();
		unregisterReceiver(receiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		dbo = new DbOperations(getApplicationContext());


		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// open db access
		dbo.close();
		dbo.open();
		//Pour la recherche
		handleIntent(getIntent());

		cursorD=dbo.getAllDates();
		cursorS=dbo.getAllSessions();
		cursorT=dbo.getAllTags();

		leftMenu = (View ) findViewById(R.id.leftMenu);	
		summaryButton = (Button) findViewById(R.id.downloadSummary);
		managePostItsButton = (Button) findViewById(R.id.managePostIts);
		sendToTableView = (TextView) findViewById(R.id.tableArea);
		titleView = (TextView) findViewById(R.id.titleSelection);
		tableSliding = (SlidingDrawer)	findViewById(R.id.slidingDrawerTable);
		openConnectTableButton = (ImageButton) findViewById(R.id.openConnectTable);
		contentConnectedSlider = (LinearLayout) findViewById(R.id.contentConnectedSlider);
		listPDF = (ListView) findViewById(R.id.listPDF);

		//Cr?ation de la liste des notes
		listeNotes = (GridView ) findViewById(R.id.listNotes);
		listeNotes.setAdapter(nAdapter);


		//Création des listes de gauche
		
		//pAdapter = new SimpleAdapter(getApplicationContext(), new ArrayList<HashMap<String, String>>(), android.R.layout.simple_list_item_1, new String[] {"filename"}, new int[] {android.R.id.text1});
		  //      lv.setAdapter(adapter);
		


		sAdapter = new SimpleCursorAdapter(getApplicationContext(), 
				android.R.layout.simple_list_item_single_choice, 
				cursorS, 
				new String[]{DbHelper.SESSION_NAME}, 
				new int[]{android.R.id.text1});
		listSessions = (ListView) findViewById(R.id.listSessions);
		listSessions.setAdapter(sAdapter);
		listSessions.setChoiceMode(1);

		tAdapter = new SimpleCursorAdapter(getApplicationContext(), 
				android.R.layout.simple_list_item_single_choice, 
				cursorT, 
				new String[]{DbHelper.TAG_NAME}, 
				new int[]{android.R.id.text1});
		listTags = (ListView) findViewById(R.id.listTags);
		listTags.setAdapter(tAdapter);
		listTags.setChoiceMode(1);

		dAdapter = new SimpleCursorAdapter(getApplicationContext(), 
				android.R.layout.simple_list_item_single_choice, 
				cursorD, 
				new String[]{DbHelper.NOTE_CREATION}, 
				new int[]{android.R.id.text1});

		// Affichage des dates au format europ�en
		dAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor, int column) {
				if( column == 0 ){ // let's suppose that the column 0 is the date
					TextView tv = (TextView) view;
					String dateStr = cursor.getString(cursor.getColumnIndex("creation"));
					tv.setText(AndroTatActivity.convertDateStringFromUSToEu(dateStr));
					return true;
				}
				return false;
			}
		});

		listDates = (ListView) findViewById(R.id.listDates);
		listDates.setAdapter(dAdapter);
		listDates.setChoiceMode(1);

		/** Lors de la selection d'un element de la liste Session */
		listSessions.setLongClickable(true);
		listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View v, int position,long id) {

				//On recuper notre objet selectionne
				Cursor c = ((SimpleCursorAdapter)adapter.getAdapter()).getCursor();
				c.moveToPosition(position);
				Session selectedElement =  dbo.cursorToSession(c);

				//recupere les ressources et checked
				Resources res = getResources();
				final CheckedTextView view = (CheckedTextView)v;
				view.setChecked(true);

				//Mise à jours du titre
				String chaine = res.getString(R.string.selectedCategory, res.getString(typeSection), selectedElement.getName());
				TextView vue = (TextView)findViewById(R.id.titleSelection);
				vue.setText(chaine);

				//Si l'element n'est pas déjà dans la liste
				Cursor curs = dbo.getAllNotesForSession(selectedElement);
				listNotes = dbo.cursorToNoteList(curs);

				//On indique que l'ensemble de données a été modifié
				nAdapter.notifyDataSetChanged();


			}
		});
		//Long click sur session pour supprimer
		listSessions.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapter, View arg1,
					int position, long arg3) {
				//On recuper notre objet selectionne
				Cursor c = ((SimpleCursorAdapter)adapter.getAdapter()).getCursor();
				c.moveToPosition(position);
				final Session selectedElement =  dbo.cursorToSession(c);
				Cursor curs = dbo.getAllNotesForSession(selectedElement);
				Vector<Note> tempList = dbo.cursorToNoteList(curs);
				//Si aucune note liee supprime
				AlertDialog.Builder builder = new AlertDialog.Builder(AndroTatActivity.this);
				if(tempList.isEmpty()){
					builder.setMessage("Etes-vous sur de vouloir supprimer cette session?");
					builder.setCancelable(true);
					builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// On supprime le tag en BDD
							dbo.deleteSession(selectedElement);
							cursorS.requery();
							sAdapter.notifyDataSetChanged();
						}

					});

					builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
				}
				else{
					builder.setMessage("Impossible de supprimer une session ayant encore des notes! Changez de section les notes associees et recommencez.");
					builder.setNeutralButton("Retour", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
				}
				AlertDialog alert = builder.create();
				alert.show();

				return false;

			}
		});

		/** Lors de la selection d'un element de la liste Date */

		listDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View v, int position,long id) {

				//On recuper notre objet selectionne
				Cursor c = ((SimpleCursorAdapter)adapter.getAdapter()).getCursor();
				c.moveToPosition(position);

				//recupere les ressources et checked
				Resources res = getResources();
				final CheckedTextView view = (CheckedTextView)v;
				view.setChecked(true);
				//Mise à jours du titre
				String chaine = res.getString(R.string.selectedCategory, res.getString(typeSection), AndroTatActivity.convertDateStringFromUSToEu(c.getString(0)));
				TextView vue = (TextView)findViewById(R.id.titleSelection);
				vue.setText(chaine);

				//Si l'element n'est pas déjà dans la liste
				Cursor curs = dbo.getAllNotesForDate(c.getString(0));
				listNotes = dbo.cursorToNoteList(curs);

				//On indique que l'ensemble de données a été modifié
				nAdapter.notifyDataSetChanged();

			}
		});

		/** Lors de la selection d'un element de la liste Tags */

		listTags.setLongClickable(true);
		listTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View v, int position,long id) {

				//On recuper notre objet selectionne
				Cursor c = ((SimpleCursorAdapter)adapter.getAdapter()).getCursor();
				c.moveToPosition(position);
				selectedTagElement =  dbo.cursorToTag(c);

				//recupere les ressources et checked
				Resources res = getResources();
				final CheckedTextView view = (CheckedTextView)v;
				view.setChecked(true);

				//Mise à jours du titre
				String chaine = res.getString(R.string.selectedCategory,  res.getString(typeSection), selectedTagElement.getName());
				TextView vue = (TextView)findViewById(R.id.titleSelection);
				vue.setText(chaine);

				//Si l'element n'est pas déjà dans la liste
				Cursor curs = dbo.getAllNotesForTag(String.valueOf(selectedTagElement.getId()));
				listNotes = dbo.cursorToNoteList(curs);

				//On indique que l'ensemble de données a été modifié
				nAdapter.notifyDataSetChanged();

			}
		});
		listPDF.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View v, int position,long id) {
				try {
					readFile((String)adapter.getAdapter().getItem(position));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		listTags.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapter, View arg1,
					int position, long arg3) {
				//On recuper notre objet selectionne
				Cursor c = ((SimpleCursorAdapter)adapter.getAdapter()).getCursor();
				c.moveToPosition(position);
				final Tag selectedElement =  dbo.cursorToTag(c);

				AlertDialog.Builder builder = new AlertDialog.Builder(AndroTatActivity.this);
				builder.setMessage(R.string.confirmTagDelete);
				builder.setCancelable(true);
				builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// On supprime le tag en BDD
						dbo.deleteTag(selectedElement);
						cursorT = dbo.getAllTags();
						// On r�initialisie l'adapter, afin de r�cup�rer la liste des tags sans tag supprim�
						tAdapter = new SimpleCursorAdapter(getApplicationContext(), 
								android.R.layout.simple_list_item_single_choice, 
								cursorT, 
								new String[]{DbHelper.TAG_NAME}, 
								new int[]{android.R.id.text1});

						listTags.setAdapter(tAdapter);
					}
				});

				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				AlertDialog alert = builder.create();
				alert.show();

				return false;
			}
		});
		/** Gestion des quatres sections */

		//PDF
		buttonPDF = (Button)findViewById(R.id.buttonPdf);
		buttonPDF.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				File myDir=new File(Environment.getExternalStorageDirectory().toString() + "/" + "AndroTat");
				String[] listePDF = myDir.list();
				
				 pAdapter = new ArrayAdapter<String>(getApplicationContext(),
						 		android.R.layout.simple_list_item_1,
						 		listePDF);
				      
				listPDF.setAdapter(pAdapter);

				typeSection=R.string.pdf;
				listSessions.setVisibility(View.GONE);
				listTags.setVisibility(View.GONE);
				listDates.setVisibility(View.GONE);
				listPDF.setVisibility(View.VISIBLE);
				leftMenu.invalidate();
			}
		});
		//Dates
		buttonDates = (Button)findViewById(R.id.buttonDates);
		buttonDates.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				typeSection=R.string.datesLink;
				listSessions.setVisibility(View.GONE);
				listTags.setVisibility(View.GONE);
				listDates.setVisibility(View.VISIBLE);
				listPDF.setVisibility(View.GONE);
				leftMenu.invalidate();
			}
		});
		//Sessions
		buttonSessions = (Button)findViewById(R.id.buttonSessions);
		buttonSessions.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				typeSection=R.string.sessionsLink;
				listSessions.setVisibility(View.VISIBLE);
				listTags.setVisibility(View.GONE);
				listDates.setVisibility(View.GONE);
				listPDF.setVisibility(View.GONE);
				leftMenu.invalidate();
			}
		});
		//Tags
		buttonTags = (Button)findViewById(R.id.buttonTags);
		buttonTags.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				typeSection = R.string.tagsLink;
				Log.v(TAG, "mode : tag");
				listSessions.setVisibility(View.GONE);
				listTags.setVisibility(View.VISIBLE);
				listDates.setVisibility(View.GONE);
				listPDF.setVisibility(View.GONE);
				leftMenu.invalidate();
			}
		});



		tableSliding.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			public void onDrawerClosed() {
				RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(930, 45);
				titleView.setLayoutParams(layoutparams);
			}

		});

		tableSliding.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			public void onDrawerOpened() {
				RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(700, 45);
				titleView.setLayoutParams(layoutparams);
			}
		});

		tableSliding.setOnDrawerScrollListener(new OnDrawerScrollListener() {

			public void onScrollStarted() {
				RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(700, 45);
				titleView.setLayoutParams(layoutparams);
			}

			public void onScrollEnded() {
			}
		});


		spinnerTable = (Spinner) findViewById(R.id.tableSpinner);
		table_cursor = dbo.getAllTables();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				table_cursor,
				new String[]{DbHelper.TABLE_NAME}, 
				new int[]{android.R.id.text1});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTable.setAdapter(adapter);
		table_connectee = dbo.cursorToTable(table_cursor);


		/* register custom receiver */
		receiver = new AgentIntentReceiver();
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(INTENT_ACTION_CONNECTED);
		iFilter.addAction(INTENT_ACTION_DISCONNECTED);
		iFilter.addAction(INTENT_ACTION_ERROR);
		iFilter.addAction(INTENT_ACTION_MODEL_OK);
		iFilter.addAction(INTENT_ACTION_PDF_OK);
		iFilter.addAction(STRING_ANSWER);
		registerReceiver(receiver, iFilter);

		connectTable = (Button)findViewById(R.id.connectTable);
		connectTable.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
		connectTable.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(isWorkingOnConnection){
					Toast.makeText(AndroTatActivity.this, "Work in progress", Toast.LENGTH_SHORT);
				}else if (!isConnectedTable) {
					String user = getLogin();
					String ip = ((Cursor) spinnerTable.getSelectedItem()).getString(2);
					agentManager.doConnect(getBaseContext(), ip, user);
					connectTable.setEnabled(false);
					isWorkingOnConnection = true;
				}else if (isConnectedTable) {
					agentManager.doDisconnect();
					connectTable.setEnabled(false);
					isWorkingOnConnection = true;
				}
			}
		});

		summaryButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				agentManager.askPdf();
			}
		});

		managePostItsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				agentManager.askModel();
			}
		});

		/* TODO change to drag & drop */
		sendToTableView.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				for(Note n : nAdapter.selectedNotes()){
					agentManager.createPostit(n.getContent());					
				}
			}
		});


		/** Lors de la selection d'une note */
		listeNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Note selectedNote = (Note)nAdapter.getItem(position);
				selectedNote.switchSelected();
				if(selectedNote.isOT()){
					String action = selectedNote.isSelected() ? 
							ContentOntology.SELECT : ContentOntology.UNSELECT;
					agentManager.viewAction(action, selectedNote.getOTId());
				}
				nAdapter.notifyDataSetChanged();
			}
		});

		/** Lors de l'Ždition d'une note */
		listeNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {


				//On rŽcup�re la note selectionnee
				selectedNote = (Note) nAdapter.getItem(position);

				if(selectedNote.isOT()){
					agentManager.viewAction(ContentOntology.HIGHLIGHT, selectedNote.getOTId());
					return true;
				}
				Cursor cursTag = dbo.getAllTags();
				ArrayList<Tag> listAllTags = dbo.cursorToTagList(cursTag);
				Intent i = new Intent(v.getContext(), CreateEditActivity.class);

				Cursor cursTagNote = dbo.getTagsForNote(selectedNote.getId());
				ArrayList<Tag> tagsForSelectedNote = dbo.cursorToTagList(cursTagNote);


				//On rajoute les valeurs ˆ lÕIntent
				// en tant quÕextra a ce dernier.
				// Les extras sont diffŽrenciŽs par un ÒidÓ (string)
				i.putExtra(CreateEditActivity.EXTRA_NOTE, selectedNote);
				i.putExtra(CreateEditActivity.EXTRA_ALL_TAGS, listAllTags);
				i.putExtra(CreateEditActivity.EXTRA_NOTE_TAGS, tagsForSelectedNote);

				startActivityForResult(i, CODE_EDIT_ACTIVITY);
				return true;
			}
		});
	}


	/**
	 * Adaptateur de la liste des notes.
	 */
	private class NoteAdapter extends BaseAdapter {

		public int getCount() {
			return listNotes.size();
		}

		public Object getItem(int position) {
			return listNotes.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		//Cette méthode est appelée pour chaque element de la liste!
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			// Si la vue n'est pas recyclée
			if(convertView == null) {
				// On récupère le layout, en gros on crée une vue à partir de l'XML. Operation lourde donc a éviter au max
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.notes, null);
				holder = new ViewHolder();
				//On place les widgets dans le holder
				holder.mContent = (TextView) convertView.findViewById(R.text.content);
				holder.mNote = (ImageView) convertView.findViewById(R.image.note);
				holder.mTitle = (TextView) convertView.findViewById(R.text.title);

				//Et on envoit le bazard en tant que tag
				convertView.setTag(holder);

			}
			else{
				//On récupére le holder existant
				holder = (ViewHolder)convertView.getTag();
			}
			//On définit notre objet depuis l'XML.
			Note element =listNotes.get(position);

			//Pour cela on definit les element du holder avec l'element en cour.
			holder.mContent.setText(element.getContent());
			holder.mTitle.setText(element.getTitle());

			// Choix de la couleur de fond
			boolean s = element.isSelected();
			boolean o = element.isOT();
			if(s && o) 		holder.mNote.setImageResource(R.drawable.note_selected_ot);
			else if(s && !o)holder.mNote.setImageResource(R.drawable.note_selected);
			else if(!s && o)holder.mNote.setImageResource(R.drawable.note_unselected_ot);
			else 			holder.mNote.setImageResource(R.drawable.note_unselected);

			return convertView;
		}



		/**
		 * Vide la liste des notes
		 */

		@SuppressWarnings("unused")
		public void removeAllItem()
		{
			if(!listNotes.isEmpty()){
				listNotes.clear();
				notifyDataSetChanged();
			}		
		}

		//Retourne le nombre de notes sŽlectionnŽe
		public int numberSelectedNote() {
			int i=0;
			for(Note note : listNotes) {
				if (note.isSelected()==true) {
					i++;
				}
			}
			return i;
		}

		//Retourne le nombre de notes sŽlectionnŽe
		public Vector<Note> selectedNotes() {
			Vector<Note> v = new Vector<Note>();
			for(Note note : listNotes) {
				if (note.isSelected()==true) {
					v.add(note);
				}
			}
			return v;
		}

		//SŽlection de toutes les notes ou non
		public void selectAllNotes()
		{
			int i = 0;
			for(Note note : listNotes) {
				if (note.isSelected()==true) i++;
			}
			if (i==getCount()) {
				for(Note note : listNotes) note.setSelected(false);
			}
			else {
				for(Note note : listNotes) note.setSelected(true);
			}
			notifyDataSetChanged();
		}

		//Permet de supprimer une ou plusieurs notes.
		public void removeListItem()
		{
			Vector<Note> notes_remove = new Vector<Note>();
			for(Note note : listNotes) {
				if (note.isSelected()==true) {
					notes_remove.add(note);
				}
			}
			if (notes_remove.size() > 0) {
				for (int i = (notes_remove.size()-1); i >= 0; i--){
					Note n = notes_remove.elementAt(i);
					if(n.isOT()){
						agentManager.deletePostit(AgentManager.convert(n));
					}else{
						removeItem(n);
					}
				}
			}
			notifyDataSetChanged();
		}
	}

	/** 
	 * Rajoute un element à la liste des notes
	 * @param n note a ajouter
	 * @return succes
	 */
	public boolean addItem(Note n)
	{
		//TODO add TAGS
		//On cherche dans tous les elements de la liste si le nouvel element n'existe pas déjà
		for(Note tmp : listNotes)
			if(tmp.equals(n))
			{
				Toast.makeText(AndroTatActivity.this, "Note existant deja!", Toast.LENGTH_SHORT).show();
				return false;
			}
		//Si l'element n'est pas déjà dans la liste

		Iterator<String> it = n.getTags().iterator();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		ArrayList<Tag> allTags = dbo.cursorToTagList(dbo.getAllTags());
		while (it.hasNext()) {
			String sTag = it.next();
			if (sTag.trim().length() > 0) {
				Iterator<Tag> allTagIterator = allTags.iterator();
				boolean founded = false;
				while (allTagIterator.hasNext() && !founded) {
					Tag currentTag = (Tag) allTagIterator.next();
					// Si tag existe deja on le rajouter ? la note
					if (currentTag.getName().equals(sTag)) {
						tags.add(currentTag);
						founded = true;
					}
				}
				// On cr?e le tag
				if (!founded) {
					Tag tag = dbo.createTag(sTag);
					tags.add(tag);
				}
			}
		}

		dbo.createNote(n.getTitle(), n.getContent(), n.getAuthor(),n.getSessionId(), tags);
		//On indique que l'ensemble de données a été modifié

		cursorT.requery();
		cursorS.requery();
		cursorD.requery();
		sAdapter.notifyDataSetChanged();
		tAdapter.notifyDataSetChanged();
		dAdapter.notifyDataSetChanged();
		nAdapter.notifyDataSetChanged();
		return true;
	}

	/**
	 * Update item information
	 * @param n note to update
	 * @return succes
	 */
	public boolean updateItem(Note n)
	{
		//On cherche dans tous les elements de la liste si le nouvel element n'existe pas déjà
		for(Note tmp : listNotes)
			if(tmp.getId()==n.getId())
			{
				tmp.getTags();
				//Mets a jours les donnees
				tmp.setContent(n.getContent());
				tmp.setTitle(n.getTitle());
				tmp.setSessionId(n.getSessionId());
				tmp.setDate(n.getDate());
				tmp.setTags(new ArrayList<String>());
				//On indique que l'ensemble de données a été modifié
				dbo.updateNote(tmp);
				cursorT.requery();
				cursorS.requery();
				cursorD.requery();
				nAdapter.notifyDataSetChanged();
				//TODO Tags
				return true;
			}
		//Element existe pas
		return false;
	}

	/**
	 * Remove Element From List In DB
	 */
	public void removeItem(Note note)
	{
		//TODO TAGS
		listNotes.removeElementAt(listNotes.indexOf(note));
		dbo.deleteNote(note);
		cursorT.requery();
		cursorS.requery();
		cursorD.requery();
		nAdapter.notifyDataSetChanged();
	}



	/**
	 * Classe nécessaire pour le pattern Holder
	 */
	public static class ViewHolder {
		public TextView mTitle;
		public TextView mContent;
		public ImageView mNote;
	}

	/**
	 * Classe pour le traitement d'une crŽation de note
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Si creation de post-it
		if(resultCode==RESULT_OK && requestCode==CODE_CREATE_ACTIVITY){
			Note newNote = (Note)data.getExtras().getSerializable("note");
			addItem(newNote);
			//Si l'element n'est pas déjà dans la liste
			//Ouvre la session ou la note est cree
			Session sess = new Session();
			sess.setId(newNote.getSessionId());
			
			sess = dbo.getSessionWithId(sess);

			Cursor curs = dbo.getAllNotesForSession(sess);
			listNotes = dbo.cursorToNoteList(curs);
			//Mise à jours du titre
			Resources res = getResources();
			String chaine = res.getString(R.string.selectedCategory, res.getString(typeSection), sess.getName());
			TextView vue = (TextView)findViewById(R.id.titleSelection);
			vue.setText(chaine);

			//On indique que l'ensemble de données a été modifié
			nAdapter.notifyDataSetChanged();
		}
		//Si edition de post-it
		if(resultCode==RESULT_OK && requestCode==CODE_EDIT_ACTIVITY){

			Note newNote = (Note)data.getExtras().getSerializable("note");
			ArrayList<String> oldTags = selectedNote.getTags();
			if (!(newNote.getTags().equals(oldTags))){
				diffTags(oldTags, newNote.getTags(), selectedNote);
			}
			//TODO: verifier tags
			updateItem(newNote);
			//Ouvre la session ou la note est cree
			Session sess = new Session();
			sess.setId(newNote.getSessionId());
			
			sess = dbo.getSessionWithId(sess);

			Cursor curs = dbo.getAllNotesForSession(sess);
			listNotes = dbo.cursorToNoteList(curs);
			//Mise à jours du titre
			Resources res = getResources();
			String chaine = res.getString(R.string.selectedCategory, res.getString(typeSection), sess.getName());
			TextView vue = (TextView)findViewById(R.id.titleSelection);
			vue.setText(chaine);

			//Si utilisateur a modifi� les tags, on rafraichit affichage des post-its tagu�s
			//Cursor curs = dbo.getAllNotesForTag(String.valueOf(selectedTagElement.getId()));
			//listNotes = dbo.cursorToNoteList(curs);
			nAdapter.notifyDataSetChanged();
		}
		if(requestCode == CODE_SETTINGS_ACTIVITY) {
			Toast.makeText(this, R.string.editDone, Toast.LENGTH_SHORT).show();
			table_cursor.requery();
			getLogin();
		}

		//Si suppression de post-it
		//TODO change this number
		if(resultCode==1234567890){
			Note newNote = (Note)data.getExtras().getSerializable("note");
			for(Note note : listNotes) {
				if (note.getId()==newNote.getId()) {
					removeItem(note);
					break;
				}
			}
		}
	}

	private void diffTags(ArrayList<String> oldTags, ArrayList<String> newTag, Note note) {
		ArrayList<Tag> allTags = dbo.cursorToTagList(dbo.getAllTags());
		ArrayList<String> tagsToDelete = oldTags;
		Iterator<String> newTagsIterator = newTag.iterator();
		while (newTagsIterator.hasNext()) {
			String tag = newTagsIterator.next().toString();
			if (tag.trim().length() > 0) {
				if (oldTags.contains(tag)) {
					tagsToDelete.remove(tag);
				}
				else {
					// On parcourt all tags pour voir si le tag existe deja
					Iterator<Tag> allTagsIterator = allTags.iterator();
					boolean tagExists = false;
					while (allTagsIterator.hasNext() && !tagExists) {
						Tag currentTag = (Tag)allTagsIterator.next();
						// Le tag ajout?e existe d?j?, on le rajoute donc ? la Note
						if (currentTag.getName().equals(tag.toString())) {
							dbo.addTagToNote(note, currentTag);
							tagExists = true;
							// Tag a ?t? trait?
							tagsToDelete.remove(tag);
						}
					}
					// Il faut cr?er le tag puis le rajouter ? la note
					if (!tagExists) {
						Tag addedTag = dbo.createTag(tag);
						dbo.addTagToNote(note, addedTag);
						//	tagsToDelete.remove(tag);
					}
				}
			}
		}
		// S'il reste des elemens dans tagsToDelete, l'utilisateur a supprim? des tags
		if (tagsToDelete.size() > 0) {
			Iterator<String> it = tagsToDelete.iterator();
			while (it.hasNext()) {
				String sTag = (String)it.next();
				Iterator<Tag> allTagsIterator = allTags.iterator();
				boolean founded = false;
				while (allTagsIterator.hasNext() && !founded) {
					Tag tag = (Tag)allTagsIterator.next();
					if (tag.getName().equals(sTag)) {
						dbo.deleteTagForNote(tag, note);
						founded = true;
					}
				}
				System.out.println("On a supprime des tags !");
			}
		}
	}

	private void createFile(byte[] ba) throws Exception { 
		File myDir=new File(Environment.getExternalStorageDirectory().toString() + "/" + "AndroTat");
		myDir.mkdirs();
		File file;
		String fname;
		int cnt = 1;
		do{
			fname = "tatin-summary"+cnt+".pdf";
			file = new File (myDir, fname);
			cnt++;
		}while(file.exists());
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(ba);
			//  finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			
			String[] listePDF = myDir.list();
			
			 pAdapter = new ArrayAdapter<String>(getApplicationContext(),
					 		android.R.layout.simple_list_item_1,
					 		listePDF);
			      
			listPDF.setAdapter(pAdapter);
			typeSection=R.string.pdf;
			listSessions.setVisibility(View.GONE);
			listTags.setVisibility(View.GONE);
			listDates.setVisibility(View.GONE);
			listPDF.setVisibility(View.VISIBLE);
			readFile(fname);


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readFile(String fname) throws Exception {
		// ##### Read the file back in #####
		File f = new File(Environment.getExternalStorageDirectory().toString() + "/" + "AndroTat" + "/" + fname);
		f.list();
		//		testFile.list();

		Uri path = Uri.fromFile(f); 
		Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
		pdfIntent.setDataAndType(path, "application/pdf");
		pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		try
		{
			startActivity(pdfIntent);
		}
		catch(ActivityNotFoundException e)
		{
			Toast.makeText(this, "No Application available to view pdf", Toast.LENGTH_LONG).show(); 
		}
	}

	private static String convertDateStringFromUSToEu(String usFormatDate) {
		String[] decomposedDate = usFormatDate.split("-");
		return decomposedDate[2] + "/" + decomposedDate[1] + "/" + decomposedDate[0];
	}		

	private class AgentIntentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(INTENT_ACTION_CONNECTED)) {
				contentConnectedSlider.setVisibility(View.VISIBLE);
				connectTable.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
				connectTable.setText(R.string.disconnect);
				connectTable.setEnabled(true);
				spinnerTable.setEnabled(false);
				openConnectTableButton.setImageResource(R.drawable.tableconnected);
				isConnectedTable = true;
				isWorkingOnConnection = false;
			}
			else if (action.equals(INTENT_ACTION_DISCONNECTED)) {
				contentConnectedSlider.setVisibility(View.GONE);
				connectTable.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
				connectTable.setText(R.string.connect);
				connectTable.setEnabled(true);
				spinnerTable.setEnabled(true);
				openConnectTableButton.setImageResource(R.drawable.opentablelogo);
				isConnectedTable = false;
				isWorkingOnConnection = false;
			}
			else if (action.equals(INTENT_ACTION_ERROR)) {
				Toast.makeText(AndroTatActivity.this, 
						intent.getStringExtra(INTENT_EXTRA_ERROR), 
						Toast.LENGTH_LONG).show();
				connectTable.setEnabled(true);
				isWorkingOnConnection = false;

			}
			else if (action.equals(INTENT_ACTION_MODEL_OK)) {

				titleView.setText(R.string.tablePostIts);

				String content = intent.getStringExtra(INTENT_EXTRA_MODEL);
				MessageContentBroker mcb = new MessageContentBroker(content);
				BSJason bsj = (BSJason) mcb.jasonFirstAnswer(BSJason.class);

				listNotes = AgentManager.convertToList(bsj);
				nAdapter.notifyDataSetChanged();

			}
			else if (action.equals(INTENT_ACTION_PDF_OK)) {
				byte[] arr = intent.getByteArrayExtra(INTENT_EXTRA_PDF);
				try {
					createFile(arr);
				} catch (Exception e) {
					Toast.makeText(AndroTatActivity.this, 
							"Error while saving PDF : "+e.getMessage(), 
							Toast.LENGTH_LONG).show();
				}
			}
			else if(action.equals(STRING_ANSWER)){
				String[] t =intent.getStringArrayExtra(INTENT_PARAMETERS);
				String out = "";
				for(String i : t) out += i+", "; 
				Toast.makeText(AndroTatActivity.this, 
						out, 
						Toast.LENGTH_LONG).show();
			}

		}
	}

	/**
	 * Fonction pour la recherche
	 */
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Cursor curs = dbo.searchResult(query);
			if (curs!=null) {
				listNotes = dbo.cursorToNoteList(curs);
				//On indique que l'ensemble de donn�es a �t� modifi�
				nAdapter.notifyDataSetChanged();
			}
			else {
				Toast.makeText(this, "Aucun resultat", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
