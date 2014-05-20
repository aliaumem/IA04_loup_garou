package utc.androTat.main;

import java.util.Vector;

import utc.androTat.model.DbOperations;
import utc.androTat.model.Table;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ParameterActivity extends PreferenceActivity {
	DbOperations dbo;
	Vector<Table> tables = new Vector<Table>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		dbo = new DbOperations(getApplicationContext());
		// open db access
		dbo.open();
		Preference button = (Preference)findPreference("button");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		                public boolean onPreferenceClick(Preference arg0) { 
		                	ListPreference listDelete = (ListPreference)findPreference("listdelete");
		                	showalert(listDelete);
		                    return true;
		                }
		            });
		ListPreference listDelete = (ListPreference)findPreference("listdelete");
		updateList(listDelete);
		listDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	        public boolean onPreferenceClick(Preference preference) {
	            if((preference instanceof ListPreference) && (preference.getKey().equals("listdelete"))){
	            	ListPreference listDelete = (ListPreference)preference;
	            	updateList(listDelete);
	                return true;
	            }
	            return false;
	        }
	    });
		listDelete.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				ListPreference listDelete = (ListPreference)arg0;
				long id = Long.parseLong((arg1.toString()));
				deleteAlert(id, listDelete);
		    	updateList(listDelete);
				return false;
			}
		});
	}
	
	public void showalert(final ListPreference listDelete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View alertDialogView = factory.inflate(R.layout.add_table_dialog, null);
		builder.setView(alertDialogView)
			   .setMessage(R.string.tableParameters)
		       .setCancelable(false)
		       .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   EditText champName = (EditText)alertDialogView.findViewById(R.id.NomTable);
		        	   String name = champName.getText().toString();
		        	   EditText champIp = (EditText)alertDialogView.findViewById(R.id.IP);
		        	   String ip = champIp.getText().toString();
		        	   //Table tab = 
		        	   addTable(name,ip);
		        	   updateList(listDelete);
		           }
		       })
		       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private Table addTable(String name, String ip) {
		Table tab = new Table();
		tab = dbo.createTable(name, ip);
		return tab;
	}
	
	public boolean updateList(ListPreference listDelete) {
		Cursor cursor = dbo.getAllTables();
		tables = dbo.cursorToTableList(cursor);
		CharSequence[] entries = new CharSequence[tables.size()]; 
		CharSequence[] entryValues = new CharSequence[tables.size()];
		int i = 0;
		for(Table t : tables) {
			System.out.println(i + " : " + t.getName() + " " + t.getIp());
			entries[i] = "Nom : " + t.getName() + ", ip : " + t.getIp();
			entryValues[i] = String.valueOf(t.getId()) ;
			i++;
		}
		listDelete.setEntries(entries);
		listDelete.setEntryValues(entryValues);
		return true;
	}
	
	public void deleteAlert(final long idDelete, final ListPreference listDelete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirmTableDelete)
		.setCancelable(false)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
		    	dbo.deleteTable(idDelete);
		    	updateList(listDelete);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
