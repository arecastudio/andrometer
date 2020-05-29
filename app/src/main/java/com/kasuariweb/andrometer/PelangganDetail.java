package com.kasuariweb.andrometer;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kasuariweb.andrometer.adapters.PelangganDetailAdapter;
import com.kasuariweb.andrometer.data.ListPelangganDetail;

import java.util.ArrayList;

public class PelangganDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private String kode_blok;
    private TextView text_kop;
    private Spinner spinner;
    private ListView listView;
    private DBHelper helper;
    private ArrayList<ListPelangganDetail> pelDetail;
    private Intent intent;
    private String sqlJns,keyword;
    private PelangganDetailAdapter adapter;
    private Boolean SearchMode;
    private TextView text_judul;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pelanggan_detail);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        helper=new DBHelper(getApplicationContext());
        helper.ambilPengaturan();

        init();

        SearchMode=false;
        keyword="";

        text_judul.setText("Andrometer - Pelanggan Detail");

        Bundle extras = getIntent().getExtras();
        kode_blok=extras.getString("key_kode_blok");

        if (kode_blok.equals("pencarian")){
            text_kop.setText("Hasil Pencarian");
            spinner.setVisibility(View.GONE);
            keyword=extras.getString("keyword");
            SearchMode=true;
            pelDetail=helper.getCariPelanggan(keyword);
        }else{
            text_kop.setText("Blok "+kode_blok);
            pelDetail=helper.getPelangganDetail(kode_blok,helper.SQL_Semua);
        }



        //pelDetail=helper.getPelangganDetail(kode_blok,helper.SQL_Semua);
        adapter=new PelangganDetailAdapter(getApplicationContext(),pelDetail);

        listView.setAdapter(adapter);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                mAdView.setVisibility(View.GONE);
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });
    }

    private void init() {
        text_judul=(TextView)this.findViewById(R.id.text_judul);
        text_kop=(TextView)this.findViewById(R.id.text_kop);
        spinner=(Spinner)this.findViewById(R.id.spinner_status);
        spinner.setOnItemSelectedListener(this);

        listView=(ListView)this.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (SearchMode.equals(false)){
            Toast.makeText(parent.getContext(),
                    "Menampilkan Pelanggan : " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();

            switch (parent.getItemAtPosition(position).toString()){
                case "Tersisa":
                    pelDetail=helper.getPelangganDetail(kode_blok,helper.SQL_Tersisa);
                    adapter=new PelangganDetailAdapter(getApplicationContext(),pelDetail);
                    listView.setAdapter(adapter);
                    break;
                case "Dibaca":
                    pelDetail=helper.getPelangganDetail(kode_blok,helper.SQL_Dibaca);
                    adapter=new PelangganDetailAdapter(getApplicationContext(),pelDetail);
                    listView.setAdapter(adapter);
                    break;
                default:
                    pelDetail=helper.getPelangganDetail(kode_blok,helper.SQL_Semua);
                    adapter=new PelangganDetailAdapter(getApplicationContext(),pelDetail);
                    listView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView text_nomor=(TextView)view.findViewById(R.id.text_nomor);
        String nomor=text_nomor.getTag()+"";
        try {
            //Toast.makeText(this, ""+nomor, Toast.LENGTH_SHORT).show();
            intent=new Intent(getApplicationContext(),EntryPelanggan.class);
            intent.putExtra("nomor",nomor);
            startActivityForResult(intent,1);
        }catch (Exception e){
            Log.i("Peringatan",e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            intent=new Intent(getApplicationContext(),PelangganDetail.class);
            intent.putExtra("key_kode_blok",kode_blok);
            if (SearchMode.equals(true))intent.putExtra("keyword",keyword);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            //return false;
            //setResult(RESULT_OK,null);
            intent=new Intent(getApplicationContext(),Pelanggan.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
