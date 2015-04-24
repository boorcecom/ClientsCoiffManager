package com.boorce.clientscoiffmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;


public class RendezVousListActivity extends ActionBarActivity {

    private RendezVousDataSource CTds;

    private ListView rendezVousList;
    private Context ctx;


    // passage de reférence cid à cname
//    private String clientID;
    private String clientName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx=this;

        Bundle extras;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if(extras == null) {
// passage de reférence cid à cname
//                clientID= null;
                clientName=null;
            } else {
// passage de reférence cid à cname
//                clientID= extras.getString("clientID");
                clientName= extras.getString("clientName");
            }
        } else {
// passage de reférence cid à cname
//            clientID= (String) savedInstanceState.getSerializable("clientID");
            clientName= (String) savedInstanceState.getSerializable("clientName");
        }
        if(clientName==null) {
            this.setTitle("Ensemble des rendez-vous");
        } else {
            this.setTitle(clientName);
        }
        setContentView(R.layout.activity_rendezvous_list);

        rendezVousList=(ListView) findViewById(R.id.RVL_rendezVousList);

        CTds=new RendezVousDataSource(this);
        CTds.open();

        // Ajout de l'édition en "one-click"
        rendezVousList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                RendezVous rdv = (RendezVous) rendezVousList.getItemAtPosition(position);
                editRendezVous(rdv.getUid());
            }
        });

        refreshRendezVousList();
        registerForContextMenu(rendezVousList);

        // Bouton "Add"
        // Sortie du code de lancement de l'Activity dans une méthode privée.
        (findViewById(R.id.RVL_addButton)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addRendezVous();
                    }
                });

    }

    private void refreshRendezVousList() {

        List<RendezVous> values;
        if(clientName==null) {
            values = CTds.getAllRendezVous();
        } else {
            values = CTds.getRendezVousFromClientName(clientName);
        }
        RendezVousAdapter adapter = new RendezVousAdapter(this, values);
        rendezVousList.setAdapter(adapter);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.RVL_rendezVousList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_std, menu);
        }
    }

    // Sortie du lancement de l'édition et de l'ajout
    private void editRendezVous(Long uid) {
        Intent CW = new Intent(ctx, RendezVousActivity.class);
        CW.putExtra("rendezVousID",uid);
// passage de reférence cid à cname
//        CW.putExtra("clientID",clientID);
        CW.putExtra("clientName",clientName);
        startActivity(CW);
    }

    private void addRendezVous() {
        Intent CW = new Intent(ctx, RendezVousActivity.class);
// passage de reférence cid à cname
//        CW.putExtra("clientID",clientID);
        CW.putExtra("clientName",clientName);
        startActivity(CW);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final String uid=((TextView) info.targetView.findViewById(R.id.uidText)).getText().toString();
        final String rdvDate=((TextView) info.targetView.findViewById(R.id.textText)).getText().toString();
        switch(item.getItemId()) {
            case R.id.add:
                addRendezVous();
                refreshRendezVousList();
                return true;
            case R.id.edit:
                editRendezVous(Long.parseLong(uid,10));
                refreshRendezVousList();
                return true;
            case R.id.delete:
                AlertDialog.Builder deleteDial = new AlertDialog.Builder(this);
                deleteDial.setMessage(getString(R.string.DeleteRDV)+" "+rdvDate+"-"+clientName);
                deleteDial.setCancelable(true);
                final Context ctx=this;
                deleteDial.setPositiveButton(getString(R.string.Oui),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RendezVous rdv=new RendezVous();
                                rdv.setUid(Long.parseLong(uid,10));
                                CTds.deleteRendezVous(rdv);
                                RdvTrvDataSource RTds=new RdvTrvDataSource(ctx);
                                RTds.open();
                                List<RdvTrv> listRdvTrv=RTds.getRdvTrvFromRid(Long.parseLong(uid,10));
                                for(RdvTrv rt : listRdvTrv) {
                                    RTds.deleteRdvTrv(rt);
                                }
                                RTds.close();
                                PhotoDataSource Pds=new PhotoDataSource(ctx);
                                Pds.open();
                                List<Photo> photos=Pds.getPhotosFromRid(Long.parseLong(uid,10));
                                for(Photo photo:photos) {
                                    File file = new File(photo.getFilename());
                                    file.delete();
                                }
                                Pds.deletePhotosFromRdv(Long.parseLong(uid,10));
                                Pds.close();
                                dialog.cancel();
                                refreshRendezVousList();
                                Toast.makeText(ctx, getString(R.string.RDVDeleted), Toast.LENGTH_SHORT).show();
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

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        CTds.open();
        super.onResume();
        refreshRendezVousList();
    }
    @Override
    protected void onPause() {
        CTds.close();
        super.onPause();
    }

}
