package com.boorce.clientscoiffmanager;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.List;


public class RendezVousActivity extends ActionBarActivity {

    private TravauxDataSource Tds;
    private RdvTrvDataSource RTds;
    private RendezVousDataSource Rds;

    private ListView travauxList;
    private TextView dateRdv;
    private EditText descText;

    Context ctx;
    private Long rid;
    private String clientName;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendezvous);

        ctx=this;
        travauxList=(ListView) (findViewById(R.id.RV_travauxList));
        dateRdv=(TextView) (findViewById(R.id.rdvDate));
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
                rid= extras.getLong("rendezVousID");
                clientId= extras.getString("clientID");
                clientName= extras.getString("clientName");
                this.setTitle(getString(R.string.editRendezVous)+"-"+clientName);
                ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(true);
                RendezVous rdv=Rds.getRendezVousFromId(rid);
                dateRdv.setText(rdv.getDate());
                descText.setText(rdv.getDescription());
                refreshTravauxList();
            } else {
                if(!(extras==null)) {
                    clientId = extras.getString("clientID");
                    clientName = extras.getString("clientName");
                    this.setTitle(getString(R.string.ajoutRendezVous) + "-" + clientName);
                    rid = (long) 0;
                    Calendar date=Calendar.getInstance();
                    dateRdv.setText(date.get(Calendar.DAY_OF_MONTH)
                            +"/"+(date.get(Calendar.MONTH)+1)
                            +"/"+date.get(Calendar.YEAR));
                    ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(false);
                } else {
                    end_activity();
                }
            }
        } else {
            rid= Long.parseLong((String) savedInstanceState.getSerializable("rid"),10);
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
                            updateRendezVous();
                            Toast.makeText(ctx, getString(R.string.editSaved), Toast.LENGTH_SHORT).show();
                            end_activity();
                        }else{
                            saveRendezVous();
                            Toast.makeText(ctx, getString(R.string.addSaved), Toast.LENGTH_SHORT).show();
                            end_activity();
                        }
                    }
                });

        (findViewById(R.id.RV_datePicker)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] sDate=dateRdv.getText().toString().split("/");
                        int jour= Integer.parseInt(sDate[0],10);
                        int mois= Integer.parseInt(sDate[1],10);
                        int annee= Integer.parseInt(sDate[2],10);

                        // Launch Date Picker Dialog
                        DatePickerDialog dpd = new DatePickerDialog(ctx,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        dateRdv.setText(dayOfMonth + "/"
                                                + (monthOfYear + 1) + "/" + year);

                                    }
                                }, annee, mois-1, jour);
                        dpd.show();
                    }
                });


        // Ajout d'un travail

        (findViewById(R.id.RV_addTrvButton)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((CheckBox) (findViewById(R.id.RV_editMode))).isChecked()) {
                            updateRendezVous();
                            Intent TL = new Intent(ctx, TravauxListActivity.class);
                            TL.putExtra("requestMode","getTravail");
                            startActivityForResult(TL,1);
                        }else{
                            saveRendezVous();
                            Intent TL = new Intent(ctx, TravauxListActivity.class);
                            TL.putExtra("requestMode","getTravail");
                            // Bug: Création multiples d'un rendez vous lors de la première édition.
                            // Correction => passer en mode édition l'activity !
                            ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(true);
                            startActivityForResult(TL, 1);
                        }
                    }
                });
        refreshTravauxList();
    }

    // Evolution : on sort la mise à jour et la sauvegarde sous formes de méthodes privées
    private void saveRendezVous() {
        // Correction : inversion format date
        RendezVous rdv=Rds.createRendezVous(clientId,
                dateRdv.getText().toString(),
                descText.getText().toString());
        rid=rdv.getUid();
    }

    private void updateRendezVous() {
        RendezVous rdv=new RendezVous();
        rdv.setUid(rid);
        rdv.setCid(clientId);
        // Correction : inversion format date
        rdv.setDate(dateRdv.getText().toString());
        rdv.setDescription(descText.getText().toString());
        Rds.updateRendezVous(rdv);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RTds.open();
        Tds.open();
        Rds.open();
        if(data!=null) {
            if (requestCode == 1) {
                Long tid = Long.parseLong(data.getStringExtra("selection"), 10);
                if (RTds.RdvTrvExists(rid, tid)) {
                    Toast.makeText(ctx, getString(R.string.travailExist) + ":" + data.getStringExtra("description"), Toast.LENGTH_SHORT).show();
                } else {
                    RTds.createRdvTrv(rid, tid);
                    Toast.makeText(ctx, getString(R.string.travailAjoute) + ":" + data.getStringExtra("description"), Toast.LENGTH_SHORT).show();
                    refreshTravauxList();
                }
            }
            // Bug: Création multiples d'un rendez vous lors de la première édition.
            // Correction => passer en mode édition l'activity !
            ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("rid", String.valueOf(rid));
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
        if(rid!=0) {
            intermediate = RTds.getRdvTrvFromRid(rid);
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
                RTds.deleteRdvTrv(rid, Long.parseLong(tid, 10));
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
