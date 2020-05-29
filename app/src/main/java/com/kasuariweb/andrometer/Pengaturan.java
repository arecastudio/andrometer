package com.kasuariweb.andrometer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pengaturan extends AppCompatActivity implements View.OnClickListener {

    private TextView text_judul;
    private Button button_batal,button_simpan,button_bekap;
    private EditText edit_petugas, edit_ip;
    private Spinner spin_upp,spinner_petugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pengaturan);

        text_judul=(TextView)this.findViewById(R.id.text_judul);
        text_judul.setText("Andrometer - Pengaturan");

        button_batal=(Button)this.findViewById(R.id.button_batal);
        button_batal.setOnClickListener(this);

        button_simpan=(Button)this.findViewById(R.id.button_simpan);
        button_simpan.setOnClickListener(this);

        button_bekap=(Button)this.findViewById(R.id.button_backup);
        button_bekap.setOnClickListener(this);

        edit_ip=(EditText)this.findViewById(R.id.text_ip);
        edit_petugas=(EditText)this.findViewById(R.id.text_petugas);
        spin_upp=(Spinner)this.findViewById(R.id.spinner_upp);
        spinner_petugas=(Spinner)this.findViewById(R.id.spinner_petugas);

        Tampilkan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_batal:
                finish();
                break;
            case R.id.button_backup:
                BackupDB();
                break;
            case R.id.button_simpan:
                //String upp=spin_upp.getSelectedItem().toString().substring(0,2);
                String upp=spin_upp.getSelectedItem().toString();
                String ip=edit_ip.getText().toString().trim();
                String petugas=edit_petugas.getText().toString().trim();
                String level_petugas=spinner_petugas.getSelectedItem().toString();

                if (ip.length()>1 && petugas.length()>1){
                    DBHelper helper=new DBHelper(getApplicationContext());
                    System.out.println(upp+"\n"+ip+"\n"+petugas);
                    Boolean hasil=helper.simpanPengaturan(ip,petugas,upp,level_petugas);
                    if(hasil.equals(true)){
                        Toast.makeText(getApplicationContext(), "Pengaturan berhasil disimpan", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Gagal menyimpan pengaturan", Toast.LENGTH_SHORT).show();
                    }
                    helper=null;
                }

                break;
            default:
                break;
        }
    }

    private void Tampilkan(){
        DBHelper helper=new DBHelper(getApplicationContext());
        Boolean hasil=helper.ambilPengaturan();
        edit_ip.setText(helper.getIP());
        edit_petugas.setText(helper.getNAMA_PETUGAS());
        String upp=helper.getUPP();
        //System.out.println("HASIL :  "+upp);
        spin_upp.setSelection(getIndex(spin_upp, upp));
        String lvl=helper.getLEVEL_PETUGAS();
        spinner_petugas.setSelection(getIndex(spinner_petugas,lvl));
    }

    //private method of your class
    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    private void BackupDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String tanggal=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.kasuariweb.andrometer//databases//AndroMeter.db";
                String backupDBPath = "andrometer-backup-db-"+tanggal+".db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.d("backupDB path", "" + backupDB.getAbsolutePath());

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), "Berhasil backup database ke\n"+backupDB.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
