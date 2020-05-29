package com.kasuariweb.andrometer;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class LihatFoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lihat_foto);

        TextView text_judul=(TextView)this.findViewById(R.id.text_judul);


        ImageView full_photo=(ImageView)this.findViewById(R.id.full_photo);

        Bundle extras = getIntent().getExtras();
        Uri uri=Uri.parse(extras.getString("foto_bitmap"));

        text_judul.setText(extras.getString("foto_nama"));

        full_photo.setImageURI(uri);
    }
}
