package com.kasuariweb.andrometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kasuariweb.andrometer.R;
import com.kasuariweb.andrometer.data.ListBlok;
import com.kasuariweb.andrometer.data.ListPelanggan;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by rail on 6/23/17.
 */

public class PelangganAdapter extends BaseAdapter {
    private Context context;
    private List<ListBlok> list;
    private NumberFormat angka;

    public PelangganAdapter(Context context, List<ListBlok> list) {
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
        ViewHolder holder;
        LayoutInflater mInflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView=mInflater.inflate(R.layout.layout_pelanggan_list,null);
            holder=new ViewHolder();
            holder.text_kode_blok=(TextView)convertView.findViewById(R.id.text_kode_blok);
            holder.text_total=(TextView)convertView.findViewById(R.id.text_total);
            holder.text_terbaca=(TextView)convertView.findViewById(R.id.text_terbaca);
            holder.text_sisa=(TextView)convertView.findViewById(R.id.text_sisa);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ListBlok blok=(ListBlok) getItem(position);
        holder.text_kode_blok.setText(blok.getKode_blok());
        holder.text_total.setText(angka.format(Double.parseDouble(blok.getTotal())));
        holder.text_terbaca.setText(angka.format(Double.parseDouble(blok.getTerbaca())));
        holder.text_sisa.setText(angka.format(Double.parseDouble(blok.getSisa())));
        return convertView;
    }

    private class ViewHolder{
        TextView text_kode_blok, text_total,text_terbaca,text_sisa;
    }
}
