package com.boorce.clientscoiffmanager;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class BoorceCCMBackupAgent extends BackupAgent {


    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
// Get the oldState input stream
        FileInputStream instream = new FileInputStream(oldState.getFileDescriptor());
        DataInputStream in = new DataInputStream(instream);

        RdvTrvDataSource RTds=new RdvTrvDataSource(this.getApplicationContext());
        RendezVousDataSource Rds=new RendezVousDataSource(this.getApplicationContext());
        TravauxDataSource Tds=new TravauxDataSource(this.getApplicationContext());

        try {
            // Get the last modified timestamp from the state file and database
            long stateModified;
            if(in.available()>0) {
                stateModified = in.readLong();
            } else {
                stateModified = (long) 0;
            }
            RTds.open();
            long dataModified = RTds.getLastModified();
            RTds.close();
            if (stateModified != dataModified) {
                // The file has been modified, so do a backup
                // Or the time on the device changed, so be safe and do a backup
                // Initialisation : backup timestamp
                ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
                DataOutputStream bufWriter = new DataOutputStream(bufferStream);
                bufWriter.writeLong(dataModified);
                byte[] bufferTrv=bufferStream.toByteArray();
                int length=bufferTrv.length;
                data.writeEntityHeader("com.boorce.ccm:lastmodified", length);
                data.writeEntityData(bufferTrv,length);
                // Phase 1 : Backup travaux
                Tds.open();
                List<Travaux> Trv=Tds.getAllTravaux();
                bufWriter.flush();
                bufferStream.reset();
                String workString="";
                for(Travaux travail:Trv) {
                    workString=workString+travail.getId()+"#"
                            +travail.getDescription().replace("#",";").replace("|", ",")+"|";
                }
                bufWriter.writeUTF(workString);
                bufferTrv = bufferStream.toByteArray();
                length=bufferTrv.length;
                data.writeEntityHeader(CCMSQLiteHelper.TABLE_TRAVAUX, length);
                data.writeEntityData(bufferTrv,length);
                Tds.close();
                // Phase 2 : Backup rendezVous
                Rds.open();
                List<RendezVous> Rdv=Rds.getAllRendezVous();
                bufWriter.flush();
                bufferStream.reset();
                workString="";
                for(RendezVous rendezVous:Rdv) {
                    workString=workString+rendezVous.getUid()+"#"
                            +rendezVous.getCName().replace("#",";").replace("|", ",")+"#"
                            +rendezVous.getDate()+"#"
                            +rendezVous.getDescription().replace("#",";").replace("|",",")
                            +"|";
                }
                bufWriter.writeUTF(workString);
                bufferTrv = bufferStream.toByteArray();
                length=bufferTrv.length;
                data.writeEntityHeader(CCMSQLiteHelper.TABLE_RENDEZVOUS, length);
                data.writeEntityData(bufferTrv, length);
                Rds.close();
                // Phase 3 : Sauvegarde du lien Rendez vous et travaux
                RTds.open();
                List<RdvTrv> RT=RTds.getAllRdvTrv();
                bufWriter.flush();
                bufferStream.reset();
                workString="";
                for(RdvTrv RdvT:RT) {
                    workString=workString+RdvT.getUid()+"#"
                            +RdvT.getRid()+"#"
                            +RdvT.getTid()
                            +"|";
                }
                bufWriter.writeUTF(workString);
                bufferTrv = bufferStream.toByteArray();
                length=bufferTrv.length;
                data.writeEntityHeader(CCMSQLiteHelper.TABLE_RDV_TRV,length);
                data.writeEntityData(bufferTrv,length);
                RTds.close();
            }
            FileOutputStream outstream = new FileOutputStream(newState.getFileDescriptor());
            DataOutputStream out = new DataOutputStream(outstream);

            out.writeLong(dataModified);
        } catch (IOException e) {
            // Unable to read state file... be safe and do a backup

        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        // There should be only one entity, but the safest
        // way to consume it is using a while loop
        Long dataModified=System.currentTimeMillis();
        while (data.readNextHeader()) {
            String key = data.getKey();
            int dataSize = data.getDataSize();

            // If the key is ours (for saving top score). Note this key was used when
            // we wrote the backup entity header
            if (CCMSQLiteHelper.TABLE_TRAVAUX.equals(key)) {
                TravauxDataSource Tds = new TravauxDataSource(this.getApplicationContext());
                // Create an input stream for the BackupDataInput
                byte[] dataBuf = new byte[dataSize];
                data.readEntityData(dataBuf, 0, dataSize);
                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
                DataInputStream in = new DataInputStream(baStream);
                if (in.available() == 0) {
                    Log.d("BoorceCCM", "Travaux : Rien à faire");
                } else {
                    Tds.open();
                    Tds.deleteAllTravaux();
                    String inputString = in.readUTF();
                    String[] records = inputString.split("\\|");
                    for (String row : records) {
                        String col[] = row.split("#");
                        Travaux trv = new Travaux();
                        trv.setId(Long.parseLong(col[0], 10));
                        trv.setDescription(col[1]);
                        Tds.createTravaux(trv);
                    }
                    Tds.close();
                }
            } else if (CCMSQLiteHelper.TABLE_RENDEZVOUS.equals(key)) {
                RendezVousDataSource Rds=new RendezVousDataSource(this.getApplicationContext());
                // Create an input stream for the BackupDataInput
                byte[] dataBuf = new byte[dataSize];
                data.readEntityData(dataBuf, 0, dataSize);
                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
                DataInputStream in = new DataInputStream(baStream);
                if (in.available() == 0) {
                    Log.d("BoorceCCM", "RendezVous : Rien à faire");
                } else {
                    Rds.open();
                    Rds.deleteAllRendezVous();
                    String inputString = in.readUTF();
                    String[] records = inputString.split("\\|");
                    for (String row : records) {
                        String col[] = row.split("#");
                        RendezVous rdv = new RendezVous();
                        rdv.setUid(Long.parseLong(col[0], 10));
                        rdv.setCName(col[1]);
                        rdv.setDate(col[2]);
                        rdv.setDescription(col[3]);
                        Rds.createRendezVous(rdv);
                    }
                    Rds.close();
                }
            } else if (CCMSQLiteHelper.TABLE_RDV_TRV.equals(key)) {
                RdvTrvDataSource RTds=new RdvTrvDataSource(this.getApplicationContext());
                // Create an input stream for the BackupDataInput
                byte[] dataBuf = new byte[dataSize];
                data.readEntityData(dataBuf, 0, dataSize);
                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
                DataInputStream in = new DataInputStream(baStream);
                if (in.available() == 0) {
                    Log.d("BoorceCCM", "RdvTrv : Rien à faire");
                } else {
                    RTds.open();
                    RTds.deleteAllRdvTrv();
                    String inputString = in.readUTF();
                    String[] records = inputString.split("\\|");
                    for (String row : records) {
                        String col[] = row.split("#");
                        RdvTrv rdv = new RdvTrv();
                        rdv.setUid(Long.parseLong(col[0], 10));
                        rdv.setRid(Long.parseLong(col[1], 10));
                        rdv.setTid(Long.parseLong(col[2], 10));
                        RTds.createRdvTrv(rdv);
                    }
                    RTds.close();
                }
            } else if (key.equals("com.boorce.ccm:lastmodified")) {
                byte[] dataBuf = new byte[dataSize];
                data.readEntityData(dataBuf, 0, dataSize);
                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
                DataInputStream in = new DataInputStream(baStream);
                if (in.available() == 0) {
                    Log.d("BoorceCCM", "lastmodified : Rien à faire");
                } else {
                    dataModified = in.readLong();
                }
            } else {
                // We don't know this entity key. Skip it. (Shouldn't happen.)
                data.skipEntityData();
            }
        }

        // Finally, write to the state blob (newState) that describes the restored data
        FileOutputStream outstream = new FileOutputStream(newState.getFileDescriptor());
        DataOutputStream out = new DataOutputStream(outstream);
        out.writeLong(dataModified);
    }

}
