package com.kasuariweb.andrometer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EntryPelanggan extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private DBHelper helper;
    private TextView text_judul,text_kop;
    private TextView text_informasi;
    private EditText text_angka,text_keterangan;
    private Spinner spinner_kwm,spinner_pengaliran;
    private String nomor,informasi,nama_foto,kode_blok,tanggal;

    private static final int CAMERA_REQUEST = 1888;
    private ImageView foto;
    private Button button_foto,button_save;
    private File imagesFolder,image;
    private Boolean stat_foto;

    private static final String TAG="ENTRY PELANGGAN";
    private GetLokasi lokasi;
    private Uri uriSavedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_entry_pelanggan);

        helper=new DBHelper(getApplicationContext());
        helper.ambilPengaturan();
        tanggal=helper.getTanggalJam();

        init();
        text_judul.setText("Andrometer - Entry Pelanggan");
        //text_kop.setText("");

        Bundle extras = getIntent().getExtras();
        nomor=extras.getString("nomor");
        //kode_blok=extras.getString("kode_blok");

        helper.getPelanggan(nomor);

        informasi="Nomor : "+helper.getNOMOR()+" / "+helper.getNOLAMA();
        informasi+="\nNama   : "+helper.getNAMA();
        informasi+="\nAlamat : "+helper.getALAMAT();
        informasi+="\nStatus  : "+getStatusBaca(helper.getSTATUS());

        text_kop.setText(informasi);
    }

    private void init(){
        stat_foto=false;
        text_judul=(TextView)this.findViewById(R.id.text_judul);
        text_kop=(TextView)this.findViewById(R.id.text_kop);
        foto=(ImageView)this.findViewById(R.id.foto);
        button_foto=(Button)this.findViewById(R.id.button_foto);
        button_foto.setOnClickListener(this);
        button_save=(Button)this.findViewById(R.id.button_save);
        button_save.setOnClickListener(this);
        text_angka=(EditText)this.findViewById(R.id.text_angka);
        text_keterangan=(EditText)this.findViewById(R.id.text_keterangan);
        spinner_kwm=(Spinner)this.findViewById(R.id.spinner_kwm);
        spinner_kwm.setOnItemSelectedListener(this);
        spinner_pengaliran=(Spinner)this.findViewById(R.id.spinner_pengaliran);
    }

    private String getStatusBaca(String status){
        String stat="Sudah dibaca";
        if (status.equals("0")) stat="Belum dibaca";
        return stat;
    }

    @Override
    public void onClick(View v) {
        String periode=helper.getPeriode();
        String lat="0",lon="0";
        try {
            lokasi=new GetLokasi(getApplicationContext());
            lat=lokasi.getLatitude();
            lon=lokasi.getLongitude();
        }catch (Exception e){
            Log.e("Ambil lokasi",e.toString());
        }

        switch (v.getId()){
            case R.id.button_foto:
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);

                //camera stuff
                //Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                //folder stuff
                //imagesFolder = new File(Environment.getExternalStorageDirectory(), "Andrometer");
                imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Andrometer");
                imagesFolder.mkdirs();

                //nama_foto=helper.getNOLAMA()+"_" + timeStamp + ".jpg";
                nama_foto=helper.getNOLAMA()+"_" + periode + ".jpg";

                image = new File(imagesFolder, nama_foto);
                uriSavedImage = Uri.fromFile(image);

                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, CAMERA_REQUEST);

                break;
            case R.id.button_save:
                String angka,keterangan,kwm,pengaliran;
                Boolean simpan=false;
                try {
                    angka=text_angka.getText().toString().trim();
                    keterangan=text_keterangan.getText().toString().trim();
                    kwm=spinner_kwm.getSelectedItem().toString();
                    pengaliran=spinner_pengaliran.getSelectedItem().toString();

                    if (angka.equals("") || angka.isEmpty() || keterangan.equals("") || keterangan.isEmpty() || stat_foto.equals(false)){
                        //Log.i("Simpan Error","Data belum siap disimpan");
                        AlertDialog.Builder builder1=new AlertDialog.Builder(this);
                        builder1.setTitle(getString(R.string.app_name));
                        builder1.setIcon(R.mipmap.ic_cancel);
                        builder1.setMessage("Masukkan Angka Meter, Keterangan dan ambil Foto terlebih dahulu !");
                        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        });
                        AlertDialog dialog1=builder1.create();
                        dialog1.show();
                    }else {
                        Log.i("Simpan Sukses",nomor+"\n"+"Angka: "+angka+"\n"+"Keterangan: "+keterangan+"\n"+kwm+"\n"+pengaliran);
                        simpan=helper.entryPelanggan(nomor,angka,keterangan,kwm,pengaliran,periode,lat,lon,tanggal);
                        if (simpan.equals(true)){
                            Toast.makeText(this, "Data berhasil disimpan !", Toast.LENGTH_SHORT).show();

                            //Intent resultIntent = new Intent();
                            //resultIntent.putExtra("kode_blok", kode_blok);
                            setResult(RESULT_OK,null);
                            finish();
                        }else {
                            Toast.makeText(this, "Gagal menyimpan data !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
            /*Bitmap photo = null;
            try {
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(image));

                Log.i("INFO FOTO","Berhasil...berhasil...");
                stat_foto=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            foto.setImageBitmap(photo);*/
                foto.setImageURI(uriSavedImage);
                stat_foto=true;
            }
        }catch (Exception ex){
            Log.e("Error set Foto",ex.getMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getItemAtPosition(position).toString()){
            case "1 - Meter Baik":
                text_angka.setText("");
                text_angka.setEnabled(true);
                break;
            default:
                text_angka.setText("0");
                text_angka.setEnabled(false);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
