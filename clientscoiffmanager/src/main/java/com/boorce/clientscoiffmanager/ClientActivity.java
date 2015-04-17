package com.boorce.clientscoiffmanager;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ClientActivity extends ActionBarActivity {

    private ListView clientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        clientList=(ListView) findViewById(R.id.AC_clientsList);

        clientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Cursor cData = (Cursor) clientList.getItemAtPosition(position);
// passage de reférence cid à cname
//                String CID=cData.getString(0);
                String cName=cData.getString(1);
// passage de reférence cid à cname
                launchClientWork(cName);
            }
        });

        populateContactList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_standard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_travaux) {
            Intent CW = new Intent(this, TravauxListActivity.class);
            startActivity(CW);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Populate the contact list based on account currently selected in the account spinner.
     */
    private void populateContactList() {
        // Build adapter with contact entries
        Cursor cursor = getContacts();
        String[] fields = new String[] {
                ContactsContract.Data.DISPLAY_NAME
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contact_client, cursor,
                fields, new int[] {R.id.contactEntryText},SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        clientList.setAdapter(adapter);
    }

    /**
     * Obtains the contact list for the currently selected account.
     *
     * @return A cursor for for accessing the contact list.
     */
    private Cursor getContacts()
    {
        // Run query
        CursorLoader clientLDR;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        clientLDR=new CursorLoader(this,uri,projection,selection,null,sortOrder);


        return clientLDR.loadInBackground();
    }

    // passage de reférence cid à cname
    private void launchClientWork(String cName) {
        Intent CW = new Intent(this, RendezVousListActivity.class);
//        CW.putExtra("clientID",CID);
        CW.putExtra("clientName",cName);
        startActivity(CW);
    }

}
