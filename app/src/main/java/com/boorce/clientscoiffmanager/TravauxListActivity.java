package com.boorce.clientscoiffmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class TravauxListActivity extends ActionBarActivity {

    private TravauxDataSource Tds;
    private ListView travauxList;
    private Boolean selectionTrv;

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travaux_list);

        travauxList=(ListView) findViewById(R.id.TL_travauxList);

        Tds=new TravauxDataSource(this);
        Tds.open();
        ctx=this;
        refreshTravauxList();

        registerForContextMenu(travauxList);

        Bundle requestData;


        selectionTrv=false;
        if (savedInstanceState == null) {
            requestData = getIntent().getExtras();
            selectionTrv=(requestData!=null);
            if(selectionTrv) {
                this.setTitle(getString(R.string.ajouterTravail));
            } else {
                this.setTitle(getString(R.string.title_activity_travaux));
            }
        } else {
            selectionTrv=(Boolean) savedInstanceState.getSerializable("selectionTrv");
            this.setTitle((String) savedInstanceState.getSerializable("appTitle"));
        }

        // Cas du la page lancée à partir de rendezvous :


        if(selectionTrv) {
            ((ListView) findViewById(R.id.TL_travauxList)).
                    setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> AV, View v, int position, long id) {
                            Intent returnIntent=new Intent();
                            returnIntent.putExtra("selection",((TextView) v.findViewById(R.id.uidText)).getText());
                            returnIntent.putExtra("description",((TextView) v.findViewById(R.id.textText)).getText());
                            setResult(RESULT_OK,returnIntent);
                            finish();
                        }
                    });
        }

        // Bouton "Add"

        (findViewById(R.id.TL_addButton)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent CW = new Intent(ctx, TravailActivity.class);
                        startActivity(CW);
                    }
                });



    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("selectionTrv", selectionTrv);
        state.putSerializable("appTitle",this.getTitle().toString());
    }

    private void refreshTravauxList() {
        List<Travaux> values;
        values = Tds.getAllTravaux();
        TravauxAdapter adapter = new TravauxAdapter(this, values);
        travauxList.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.TL_travauxList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_std, menu);
        }
    }

    // Ajout du bouton d'ajout.

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final String uid=((TextView) info.targetView.findViewById(R.id.uidText)).getText().toString();
        final String travailName=((TextView) info.targetView.findViewById(R.id.textText)).getText().toString();
        Intent CW = new Intent(ctx, TravailActivity.class);
        switch(item.getItemId()) {
            case R.id.add:
                startActivity(CW);
                refreshTravauxList();
                return true;
            case R.id.edit:
                CW.putExtra("travailID",uid);
                startActivity(CW);
                refreshTravauxList();
                return true;
            case R.id.delete:
                RdvTrvDataSource RTds=new RdvTrvDataSource(ctx);
                RTds.open();
                List<RdvTrv> listRdvTrv;
                listRdvTrv=RTds.getRdvTrvFromTid(Long.parseLong(uid,10));
                if(listRdvTrv.size()==0) {
                    AlertDialog.Builder deleteDial = new AlertDialog.Builder(this);
                    deleteDial.setMessage(getString(R.string.DeleteTrv) + " " + travailName);
                    deleteDial.setCancelable(true);
                    final Context ctx = this;
                    deleteDial.setPositiveButton(getString(R.string.Oui),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Travaux trv = new Travaux();
                                    trv.setId(Long.parseLong(uid, 10));
                                    Tds.deleteTravail(trv);
                                    dialog.cancel();
                                    refreshTravauxList();
                                    Toast.makeText(ctx, getString(R.string.TrvDeleted), Toast.LENGTH_SHORT).show();
                                }
                            });
                    deleteDial.setNegativeButton(getString(R.string.Non),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = deleteDial.create();
                    alert11.show();
                }else{
                    Toast.makeText(ctx, getString(R.string.unableTrvDelete), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        Tds.open();
        super.onResume();
        refreshTravauxList();
    }
    @Override
    protected void onPause() {
        Tds.close();
        super.onPause();
    }
}
