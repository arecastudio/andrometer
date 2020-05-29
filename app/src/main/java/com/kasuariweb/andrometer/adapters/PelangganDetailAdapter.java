package com.kasuariweb.andrometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kasuariweb.andrometer.LihatFoto;
import com.kasuariweb.andrometer.MainActivity;
import com.kasuariweb.andrometer.R;
import com.kasuariweb.andrometer.data.ListPelangganDetail;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by rail on 6/28/17.
 */

public class PelangganDetailAdapter extends BaseAdapter {
    private Context context;
    private List<ListPelangganDetail> list;
    private NumberFormat angka;

    public PelangganDetailAdapter(Context context, List<ListPelangganDetail> list) {
        this.context = context;
        this.list = list;
        angka = NumberFormat.getInstance(Locale.GERMANY);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView=mInflater.inflate(R.layout.layout_pelanggan_detail_list,null);
            holder=new ViewHolder();
            holder.text_nomor=(TextView)convertView.findViewById(R.id.text_nomor);
            holder.text_nama=(TextView)convertView.findViewById(R.id.text_nama);
            holder.text_alamat=(TextView)convertView.findViewById(R.id.text_alamat);
            holder.text_kwm=(TextView)convertView.findViewById(R.id.text_kwm);
            holder.text_pengaliran=(TextView)convertView.findViewById(R.id.text_pengaliran);
            holder.text_angka=(TextView)convertView.findViewById(R.id.text_angka);
            holder.text_status=(TextView)convertView.findViewById(R.id.text_status);
            holder.text_keterangan=(TextView)convertView.findViewById(R.id.text_keterangan);
            holder.foto=(ImageView)convertView.findViewById(R.id.foto);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ListPelangganDetail pel=(ListPelangganDetail) getItem(position);
        holder.text_nomor.setText(pel.getNomor()+" / "+pel.getNolama());
        holder.text_nomor.setTag(pel.getNomor());
        holder.text_nama.setText(pel.getNama());
        holder.text_alamat.setText(pel.getAlamat());
        holder.text_kwm.setText("K. Meter:  "+pel.getKondisi_meter());
        holder.text_pengaliran.setText("Pengaliran:  "+pel.getKondisi_pengaliran());
        holder.text_angka.setText("Angka meter:  "+pel.getAngka());

        holder.foto.setBackgroundColor(Color.LTGRAY);
        holder.foto.setImageResource(R.mipmap.ic_camera);

        String stat="Belum dibaca";
        if (!pel.getStatus().equals("0")) {
            stat="Sudah dibaca";

            holder.foto.setBackgroundColor(Color.parseColor("#303F9F"));//primaryDark

            //File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Andrometer");
            File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Andrometer");
            imagesFolder.mkdirs();
            String nama_foto=pel.getNolama()+"_"+pel.getPeriode()+".jpg";
            File image = new File(imagesFolder, nama_foto);

            if (image.exists()){
                final Uri uri = Uri.fromFile(image);
                holder.foto.setImageURI(uri);
                holder.foto.setTag(position);

                holder.foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(context, "Posisi di-klik: "+v.getTag(), Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent=new Intent(context, LihatFoto.class);
                            intent.putExtra("foto_bitmap",uri.toString());
                            intent.putExtra("foto_nama",holder.text_nama.getText().toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
        holder.text_status.setText("Status:  "+stat);

        holder.text_keterangan.setText("Keterangan:  "+pel.getKeterangan());

        return convertView;
    }

    private class ViewHolder{
        TextView text_nomor, text_nama,text_alamat,text_kwm,text_pengaliran,text_angka,text_status,text_keterangan;
        ImageView foto;
    }
}
