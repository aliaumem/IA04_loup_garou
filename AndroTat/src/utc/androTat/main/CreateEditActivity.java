package utc.androTat.main;

import java.util.ArrayList;
import java.util.Iterator;

import utc.androTat.model.DbHelper;
import utc.androTat.model.DbOperations;
import utc.androTat.model.Note;
import utc.androTat.model.Session;
import utc.androTat.model.Tag;
import utc.androTat.utils.SpaceTokenizer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;


public class CreateEditActivity extends Activity{

	public static final String EXTRA_NOTE = "note";
	public static final String EXTRA_ALL_TAGS = "listAllTags";
	public static final String EXTRA_NOTE_TAGS = "listTagsForNote";

	public static final String RESULT_NOTE_DELETED = "listTagsForNote";

	DbOperations dbo;
	Note note = new Note();
	Cursor sess = null;
	SimpleCursorAdapter  adapter;
	Intent i=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		dbo = new DbOperations(getApplicationContext());
		// open db access
		dbo.open();
		sess=dbo.getAllSessions();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_edit_layout);
		Spinner s = (Spinner) findViewById(R.id.spinner);
		i = getIntent();

		// On charge tous les tags pour avoir lesp ropositions lors de l'autocomplete

		ArrayList<Tag> allTags = dbo.cursorToTagList(dbo.getAllTags());
		String[] allTagsString = new String[allTags.size()];
		Iterator<Tag> it = allTags.iterator();
		int index = 0;
		while (it.hasNext()) {
			String sTag = ((Tag) it.next()).getName();
			allTagsString[index] = sTag;
			index++;
		}
		ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, allTagsString);


		final EditText title = (EditText) findViewById(R.id.editTitle);
		final EditText content = (EditText) findViewById(R.id.editContent);
		final MultiAutoCompleteTextView tagEdit = (MultiAutoCompleteTextView) findViewById(R.id.tagMultiTextView);
		tagEdit.setAdapter(tagAdapter);
		tagEdit.setTokenizer(new SpaceTokenizer());

		//Autocomplete from the first letter
		tagEdit.setThreshold(1);
		
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item,
				sess,
				new String[]{DbHelper.SESSION_NAME}, 
				new int[]{android.R.id.text1});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
		//On rŽcup�re la note
		try{
			note = (Note)i.getExtras().getSerializable("note");
			//On rŽcup�re les champs et on leur affecte le texte

			title.setText(note.getTitle());
			content.setText(note.getContent());

			String tagText ="";

			@SuppressWarnings("unchecked")
			ArrayList<Tag> tags = (ArrayList<Tag>) i.getExtras().get("listTagsForNote");
			Iterator<Tag> tagIterator = tags.iterator();
			while (tagIterator.hasNext()) {
				Tag tag = tagIterator.next();
				tagText = tagText + tag.getName() + " ";
				tagEdit.setText(tagText);
			}
			
			for (int i = 0; i < s.getCount(); i++) {
		        Cursor sessTemp = (Cursor)s.getItemAtPosition(i);
		        long id = sessTemp.getLong(sessTemp.getColumnIndex(DbHelper.SESSION_ID));
		        if (id==note.getSessionId()) {
		            s.setSelection(i);
		        }
		    }

		}
		//Si nouvelle note
		catch(Exception e){
		}
		Button saveBut = (Button) findViewById(R.id.save);
		saveBut.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//Defini la nouvelle note
				note.setContent(content.getText().toString());
				note.setTitle(title.getText().toString());

				// On r�cup�re les tags
				String[] tags = tagEdit.getText().toString().split(" ");
				ArrayList<String>tagsList = new ArrayList<String>();
				for (int i = 0; i < tags.length; i++)
					tagsList.add(tags[i]);
				note.setTags(tagsList);
				Session session = dbo.cursorToSession(sess);
				note.setSessionId(session.getId());
				//Renvoie la nouvelle note
				i.putExtra("note", note);
				setResult(RESULT_OK, i);
				finish();
			}
		});
		Button addSession = (Button) findViewById(R.id.addSession);
		addSession.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				addSession();
			}
		});

		Button deleteBut = (Button) findViewById(R.id.delete);
		deleteBut.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				deleteAlert();
			}
		});





	}

	public void addSession() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View alertDialogView = factory.inflate(R.layout.add_session_dialog, null);
		builder.setView(alertDialogView)
		.setMessage("Ajouter une session")
		.setCancelable(false)
		.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				EditText champName = (EditText)alertDialogView.findViewById(R.id.NomSession);
				String name = champName.getText().toString();
				//Table tab = 
				dbo.createSession(name);
				adapter.notifyDataSetChanged();
				sess.requery();
			}
		})
		.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void deleteAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		Resources res = getResources();
		builder.setMessage(res.getString(R.string.confirmNoteDelete))
		.setCancelable(false)
		.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				i.putExtra(EXTRA_NOTE, note);
				setResult(1234567890, i);
				finish();
			}
		})
		.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}