package com.boorce.clientscoiffmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class RendezVousActivity extends ActionBarActivity {

    private TravauxDataSource Tds;
    private RdvTrvDataSource RTds;
    private RendezVousDataSource Rds;

    private ListView travauxList;
    private DatePicker dateRdv;
    private EditText descText;

    Context ctx;
    private String rid;
    private String clientName;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendezvous);

        ctx=this;
        travauxList=(ListView) (findViewById(R.id.RV_travauxList));
        dateRdv=(DatePicker) (findViewById(R.id.rdvDate));
        descText=(EditText) (findViewById(R.id.RV_descText));

        Tds=new TravauxDataSource(ctx);
        RTds=new RdvTrvDataSource(ctx);
        Rds=new RendezVousDataSource(ctx);

        Tds.open();
        RTds.open();
        Rds.open();

        Bundle extras;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if(!(extras==null)&&(extras.containsKey("rendezVousID"))) {
                rid= extras.getString("rendezVousID");
                clientId= extras.getString("clientID");
                clientName= extras.getString("clientName");
                this.setTitle(getString(R.string.editRendezVous)+"-"+clientName);
                ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(true);
                RendezVous rdv=Rds.getRendezVousFromId(Long.parseLong(rid, 10));
                descText.setText(rdv.getDescription());
                String[] splittedDate=rdv.getDate().split("/");
                dateRdv.updateDate(Integer.parseInt(splittedDate[2]),Integer.parseInt(splittedDate[1])-1,Integer.parseInt(splittedDate[0]));
                refreshTravauxList();
            } else {
                if(!(extras==null)) {
                    clientId = extras.getString("clientID");
                    clientName = extras.getString("clientName");
                    this.setTitle(getString(R.string.ajoutRendezVous) + "-" + clientName);
                    rid = null;
                    ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(false);
                }
            }
        } else {
            rid= (String) savedInstanceState.getSerializable("rid");
            clientId= (String) savedInstanceState.getSerializable("clientId");
            clientName= (String) savedInstanceState.getSerializable("clientName");
            this.setTitle((String) savedInstanceState.getSerializable("appTitle"));
            refreshTravauxList();
        }

        // Sauvegarde des modification sur le Rendez Vous

        (findViewById(R.id.RV_validateButton)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((CheckBox) (findViewById(R.id.RV_editMode))).isChecked()) {
                            RendezVous rdv=new RendezVous();
                            rdv.setUid(Long.parseLong(rid,10));
                            rdv.setCid(clientId);
                            rdv.setDate(dateRdv.getYear()+"/"+dateRdv.getMonth()+"/"+dateRdv.getDayOfMonth());
                            rdv.setDescription(descText.getText().toString());
                            Rds.updateRendezVous(rdv);
                            Toast.makeText(ctx, getString(R.string.editSaved), Toast.LENGTH_SHORT).show();
                            end_activity();
                        }else{
                            RendezVous rdv=Rds.createRendezVous(clientId,dateRdv.getYear()+
                                    "/"+dateRdv.getMonth()+"/"+dateRdv.getDayOfMonth(),descText.getText().toString());
                            rid=String.valueOf(rdv.getUid());
                            Toast.makeText(ctx, getString(R.string.addSaved), Toast.LENGTH_SHORT).show();
                            end_activity();
                        }
                    }
                });

        // Ajout d'un travail

        (findViewById(R.id.RV_addTrvButton)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((CheckBox) (findViewById(R.id.RV_editMode))).isChecked()) {
                            RendezVous rdv=new RendezVous();
                            rdv.setUid(Long.parseLong(rid,10));
                            rdv.setCid(clientId);
                            rdv.setDate(dateRdv.getYear()+"/"+dateRdv.getMonth()+"/"+dateRdv.getDayOfMonth());
                            rdv.setDescription(descText.getText().toString());
                            Rds.updateRendezVous(rdv);
                            Intent TL = new Intent(ctx, TravauxListActivity.class);
                            TL.putExtra("requestMode","getTravail");
                            startActivityForResult(TL,1);
                        }else{
                            RendezVous rdv=Rds.createRendezVous(clientId,dateRdv.getYear()+
                                    "/"+dateRdv.getMonth()+"/"+dateRdv.getDayOfMonth(),descText.getText().toString());
                            rid=String.valueOf(rdv.getUid());
                            Intent TL = new Intent(ctx, TravauxListActivity.class);
                            TL.putExtra("requestMode","getTravail");
                            startActivityForResult(TL, 1);
                        }
                    }
                });
        refreshTravauxList();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1) {
            Long tid=Long.parseLong(data.getStringExtra("selection"),10);
            if(RTds.RdvTrvExists(Long.parseLong(rid,10),tid)) {
                Toast.makeText(ctx, getString(R.string.travailExist) + ":" + data.getStringExtra("description"), Toast.LENGTH_SHORT).show();
            } else {
                RTds.createRdvTrv(Long.parseLong(rid, 10), tid);
                Toast.makeText(ctx, getString(R.string.travailAjoute) + ":" + data.getStringExtra("description"), Toast.LENGTH_SHORT).show();
                refreshTravauxList();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("rid", rid);
        state.putSerializable("clientName", clientName);
        state.putSerializable("clientId", clientId);
        state.putSerializable("appTitle",this.getTitle().toString());
    }

    private void end_activity()
    {
        finish();
    }

    private void refreshTravauxList() {
        List<Travaux> values=new ArrayList<Travaux>();
        List<RdvTrv> intermediate;
        if(rid!=null) {
            intermediate = RTds.getRdvTrvFromRid(Long.parseLong(rid, 10));
            for(RdvTrv worker : intermediate) {
                Travaux trv;
                trv=Tds.getTravailFromId(worker.getTid());
                values.add(trv);
            }
            TravauxAdapter adapter = new TravauxAdapter(this, values);
            travauxList.setAdapter(adapter);
        }
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
        if (v.getId()==R.id.RV_travauxList) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final String tid=((TextView) info.targetView.findViewById(R.id.uidText)).getText().toString();
        switch(item.getItemId()) {
            case R.id.delete:
                RTds.deleteRdvTrv(Long.parseLong(rid, 10), Long.parseLong(tid, 10));
                refreshTravauxList();
                Toast.makeText(ctx, getString(R.string.TrvDeleted), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        Tds.open();
        RTds.open();
        Rds.open();
        super.onResume();
        refreshTravauxList();
    }
    @Override
    protected void onPause() {
        Tds.close();
        RTds.close();
        Rds.close();
        super.onPause();
    }



}
