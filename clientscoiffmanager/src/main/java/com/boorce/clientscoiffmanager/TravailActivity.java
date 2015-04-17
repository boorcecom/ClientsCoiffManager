package com.boorce.clientscoiffmanager;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class TravailActivity extends ActionBarActivity {

    private TravauxDataSource Tds;
    private String tid;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travail);
        ctx=this;
        Tds=new TravauxDataSource(this);
        Tds.open();

        Bundle extras;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if(!(extras==null)&&(extras.containsKey("travailID"))) {
                this.setTitle(getString(R.string.editTravail));
                tid= extras.getString("travailID");
                ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(true);
                Travaux trv=Tds.getTravailFromId(Long.parseLong(tid,10));
                ((EditText) (findViewById(R.id.T_descriptionText))).setText(trv.getDescription());
            } else {
                this.setTitle(getString(R.string.ajoutTravail));
                tid= null;
                ((CheckBox) (findViewById(R.id.RV_editMode))).setChecked(false);
            }
        } else {
            tid= (String) savedInstanceState.getSerializable("tid");
            this.setTitle((String) savedInstanceState.getSerializable("appTitle"));
        }


        (findViewById(R.id.T_saveButton)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((CheckBox) (findViewById(R.id.RV_editMode))).isChecked()) {
                            Travaux trv=new Travaux();
                            trv.setId(Long.parseLong(tid,10));
                            trv.setDescription(((EditText)(findViewById(R.id.T_descriptionText))).getText().toString());
                            Tds.updateTravaux(trv);
                            Toast.makeText(ctx, getString(R.string.editSaved), Toast.LENGTH_SHORT).show();
                            end_activity();
                        }else{
                            Travaux trv=Tds.createTravaux(((EditText)(findViewById(R.id.T_descriptionText))).getText().toString());
                            tid=String.valueOf(trv.getId());
                            Toast.makeText(ctx, getString(R.string.addSaved), Toast.LENGTH_SHORT).show();
                            end_activity();
                        }
                    }
                });

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("tid", tid);
        state.putSerializable("appTitle",this.getTitle().toString());
    }

    private void end_activity()
    {
        finish();
    }

    @Override
    protected void onResume() {
        Tds.open();
        super.onResume();
    }
    @Override
    protected void onPause() {
        Tds.close();
        super.onPause();
    }

}
