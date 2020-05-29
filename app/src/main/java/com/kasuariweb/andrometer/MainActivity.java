package com.kasuariweb.andrometer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kasuariweb.andrometer.data.ListPelangganDetail;
import com.kasuariweb.andrometer.transmit.TransmitHasilBaca;
import com.kasuariweb.andrometer.transmit.TransmitPelanggan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_pelanggan, btn_pengaturan, btn_download, btn_upload, btn_delete, btn_pencarian;
    private Intent intent;
    private DBHelper helper;
    private String kode_cabang, sql;
    private ContentResolver cr;
    private Context context;
    private ArrayList<ListPelangganDetail> pel;
    private  String keyword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DBHelper(this);
        helper.ambilPengaturan();
        kode_cabang = helper.getKODE_CABANG();
        //Log.i("Cabang",kode_cabang);

        btn_pelanggan = (Button) this.findViewById(R.id.btn_pelanggan);
        btn_pelanggan.setOnClickListener(this);

        btn_pengaturan = (Button) this.findViewById(R.id.btn_pengaturan);
        btn_pengaturan.setOnClickListener(this);

        btn_download = (Button) this.findViewById(R.id.btn_download);
        btn_download.setOnClickListener(this);

        btn_upload = (Button) this.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);

        btn_delete = (Button) this.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

        btn_pencarian = (Button) this.findViewById(R.id.btn_pencarian);
        btn_pencarian.setOnClickListener(this);
    }

    public MainActivity() {
    }

    @Override
    public void onClick(View v) {
        helper.ambilPengaturan();
        kode_cabang = helper.getKODE_CABANG();
        int lvl=Integer.parseInt(helper.getKODE_LEVEL());

        switch (v.getId()) {
            case R.id.btn_pelanggan:
                intent = new Intent(getApplicationContext(), Pelanggan.class);
                startActivity(intent);
                break;
            case R.id.btn_pengaturan:
                intent = new Intent(getApplicationContext(), Pengaturan.class);
                startActivity(intent);
                break;
            case R.id.btn_download:
                if (cekKoneksi() == false) break;
                JSONObject object = new JSONObject();
                try {
                    object.put("cabang", kode_cabang);
                    object.put("level_petugas",lvl);
                    TransmitPelanggan transmit = new TransmitPelanggan(this);
                    transmit.execute(new JSONObject[]{object});
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_upload:
                if (cekKoneksi() == false) break;
                //Toast.makeText(getApplicationContext(), "Mulai proses kirim data !!!", Toast.LENGTH_SHORT).show();
                //helper.cekPelanggan();

                //JSONObject obj=new JSONObject();

                /*pel=helper.getListPelanggan(kode_cabang);




                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //int i=0;

                        sql="INSERT INTO dsmp_new(periode,nomor,angka,status,kondisi_meter,kondisi_pengaliran,keterangan)VALUES";
                        //for (ListPelangganDetail s:pel){
                        for (int i=0;i<pel.size();i++){
                            //i++;
                            sql+="("+pel.get(i).getNomor()+",'"+pel.get(i).getNomor()+"',"+pel.get(i).getAngka()+","+pel.get(i).getStatus()+",'"+pel.get(i).getKondisi_meter()+"','"+pel.get(i).getKondisi_pengaliran()+"','"+pel.get(i).getKeterangan()+"')";
                            if (i<pel.size())sql+=",";
                        }
                        sql+=" ON DUPLICATE KEY UPDATE angka=VALUES(angka),kondisi_meter=VALUES(kondisi_meter),kondisi_pengaliran=VALUES(kondisi_pengaliran),keterangan=VALUES(keterangan);";

                    }


                });

                t.start();
                try {
                    t.join();//tunggu proses thread selesai
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.e("SQL",sql+"");*/

                TransmitHasilBaca transmit = new TransmitHasilBaca(this);
                transmit.execute();

                //if (i==pel.size()) Toast.makeText(getApplicationContext(), "Proses kirim DSMP selesai !", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Konfirmasi");
                builder.setIcon(R.mipmap.ic_warning_black_24dp);
                builder.setMessage("Yakin untuk hapus data Pembacaan Meter ?");
                builder.setCancelable(true);

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("HAPUS", "ya");
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                helper.eksekusiSQL("DELETE FROM pelanggan");
                            }
                        });
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        HapusData hapus = new HapusData();
                        hapus.execute();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("HAPUS", "tidak");
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.btn_pencarian:
                //if (cekKoneksi() == false) break;
                //getLocation();
                //GetLokasi lokasi=new GetLokasi(this);

                LayoutInflater li=LayoutInflater.from(this);
                View promptView=li.inflate(R.layout.prompt_cari,null);
                AlertDialog.Builder build= new AlertDialog.Builder(this);
                build.setView(promptView);

                final EditText text_keyword=(EditText)promptView.findViewById(R.id.text_keyword);

                build
                        .setCancelable(false)
                        .setTitle("Pelanggan...")
                        .setIcon(R.mipmap.ic_search_black_24dp)

                        .setPositiveButton("Cari", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                keyword=text_keyword.getText().toString().trim();
                                if (keyword.length()>0){
                                    Log.e("Cari",keyword);
                                    //Toast.makeText(MainActivity.this, "Fungsi Pencarian akan segera diaktifkan pada update aplikasi selanjutnya !", Toast.LENGTH_SHORT).show();
                                    try {
                                        intent = new Intent(getApplicationContext(), PelangganDetail.class);
                                        intent.putExtra("key_kode_blok","pencarian");
                                        intent.putExtra("keyword",keyword);
                                        startActivity(intent);
                                    }catch (Exception ex){
                                        Log.e("Error Cari",ex.getMessage());
                                    }
                                }
                            }
                        })

                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("Cari","Batal");
                            }
                        });

                AlertDialog dialog=build.create();
                dialog.show();

                //Toast.makeText(MainActivity.this, "Fungsi Pencarian akan segera diaktifkan pada update aplikasi melalui Google Playstore !", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

    private boolean cekKoneksi() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private class HapusData extends AsyncTask<String,Integer,Integer>{
        private ProgressDialog dialog;

        private void recursiveDelete(File fileOrDirectory) {
            try {
                if (fileOrDirectory.isDirectory())
                    for (File child : fileOrDirectory.listFiles())
                        recursiveDelete(child);

                fileOrDirectory.delete();
            }catch (Exception ex){
                Log.e("Error Hapus Foler",ex.getMessage());
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            String nama_foto;
            String dir_foto= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/Andrometer/";
            //String ip=helper.getIP();
            //File file_foto;

            File dirs=new File(dir_foto);
            if (dirs.exists() && dirs.isDirectory()) {
                recursiveDelete(dirs);
                Log.e("Hapus Folder",dir_foto);
            }

            /*kode_cabang=helper.getKODE_CABANG();

            //ArrayList<ListPelangganDetail> listDetail=new ArrayList<ListPelangganDetail>();
            ArrayList<ListPelangganDetail> pel=helper.getListPelanggan(kode_cabang);

            for (ListPelangganDetail s:pel){
                nama_foto=s.getNolama()+"_"+s.getPeriode()+".jpg";
                file_foto=new File(dir_foto+nama_foto);
                if (file_foto.isFile() && file_foto.exists()){
                    Log.e("Hapus Foto",dir_foto+nama_foto);
                    file_foto.delete();
                }
            }*/


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Hapus data...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            dialog.dismiss();
        }
    }
}
