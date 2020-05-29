package com.kasuariweb.andrometer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kasuariweb.andrometer.adapters.PelangganAdapter;
import com.kasuariweb.andrometer.data.ListBlok;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Pelanggan extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private TextView text_judul,text_kop;
    private TextView footer_total,footer_baca,footer_sisa;
    private ListView listView;
    private DBHelper helper;
    private ArrayList<ListBlok> blok;
    private Intent intent;

    private NumberFormat angka;
    private String kode_cabang;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pelanggan);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        angka = NumberFormat.getInstance(Locale.GERMANY);

        helper=new DBHelper(getApplicationContext());
        helper.ambilPengaturan();

        kode_cabang=helper.getKODE_CABANG();

        text_judul=(TextView)this.findViewById(R.id.text_judul);
        text_judul.setText("Andrometer - Pelanggan");

        text_kop=(TextView)this.findViewById(R.id.text_kop);
        listView=(ListView)this.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);




        Tampilkan();

        //View header = (View)getLayoutInflater().inflate(R.layout.layout_pelanggan_list_header,null);
        //listView.addHeaderView(header);

        View footer =(View)getLayoutInflater().inflate(R.layout.layout_pelanggan_list_footer,null);
        listView.addFooterView(footer);

        footer_total=(TextView)this.findViewById(R.id.text_total_footer);
        footer_baca=(TextView)this.findViewById(R.id.text_terbaca_footer);
        footer_sisa=(TextView)this.findViewById(R.id.text_sisa_footer);

        footer_total.setText(angka.format(Double.parseDouble(helper.getTotalSR(kode_cabang))));
        footer_baca.setText(angka.format(Double.parseDouble(helper.getBacaSR(kode_cabang))));
        footer_sisa.setText(angka.format(Double.parseDouble(helper.getSisaSR(kode_cabang))));



        blok=helper.getBlok(kode_cabang);
        PelangganAdapter adapter=new PelangganAdapter(getApplicationContext(),blok);
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
                Log.e("Ads", "onAdFailedToLoad\n"+errorCode);
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

    private void Tampilkan(){
        Boolean hasil=helper.ambilPengaturan();
        String upp=helper.getUPP();
        String jml=helper.getTotalSR(kode_cabang)+" SR";
        text_kop.setText(""+upp);
        Log.i("UPP",upp+"");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView kode_blok=(TextView)view.findViewById(R.id.text_kode_blok);
        //Toast.makeText(this, ""+kode_blok.getText(), Toast.LENGTH_SHORT).show();
        try {
            intent = new Intent(getApplicationContext(), PelangganDetail.class);
            intent.putExtra("key_kode_blok", kode_blok.getText().toString());
            startActivity(intent);
            this.finish();
        }catch (Exception e){
            Log.i("Peringatan",e+"");
        }
    }


}
