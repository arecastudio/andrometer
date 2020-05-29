package com.kasuariweb.andrometer.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rail on 6/27/17.
 */

public class ListPelangganDetail {
    //private Context context;
    private String nomor,nolama,nama,cabang,kode_blok,alamat,kondisi_meter,kondisi_pengaliran,keterangan,angka,status,periode,latitude,longitude,tgl_baca;

    public ListPelangganDetail(String nomor, String nolama, String nama, String cabang, String kode_blok, String alamat, String kondisi_meter, String kondisi_pengaliran, String keterangan, String angka, String status, String periode, String latitude, String longitude, String tgl_baca) {
        this.nomor = nomor;
        this.nolama = nolama;
        this.nama = nama;
        this.cabang = cabang;
        this.kode_blok = kode_blok;
        this.alamat = alamat;
        this.kondisi_meter = kondisi_meter;
        this.kondisi_pengaliran = kondisi_pengaliran;
        this.keterangan = keterangan;
        this.angka = angka;
        this.status = status;
        this.periode=periode;
        this.latitude=latitude;
        this.longitude=longitude;
        this.tgl_baca=tgl_baca;
    }

    public JSONObject toJSONObjects(){
        JSONObject json = new JSONObject();
        try {
            json.put("nomor",nomor);
            json.put("nolama",nolama);
            json.put("nama",nama);
            json.put("cabang",cabang);
            json.put("kode_blok",kode_blok);
            json.put("alamat",alamat);
            json.put("kondisi_meter",kondisi_meter);
            json.put("kondisi_pengaliran",kondisi_pengaliran);
            json.put("keterangan",keterangan);
            json.put("angka",angka);
            json.put("status",status);
            json.put("periode",periode);
            json.put("latitude",latitude);
            json.put("longitude",longitude);
            json.put("tgl_baca",tgl_baca);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getNolama() {
        return nolama;
    }

    public void setNolama(String nolama) {
        this.nolama = nolama;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getKode_blok() {
        return kode_blok;
    }

    public void setKode_blok(String kode_blok) {
        this.kode_blok = kode_blok;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKondisi_meter() {
        return kondisi_meter;
    }

    public void setKondisi_meter(String kondisi_meter) {
        this.kondisi_meter = kondisi_meter;
    }

    public String getKondisi_pengaliran() {
        return kondisi_pengaliran;
    }

    public void setKondisi_pengaliran(String kondisi_pengaliran) {
        this.kondisi_pengaliran = kondisi_pengaliran;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getAngka() {
        return angka;
    }

    public void setAngka(String angka) {
        this.angka = angka;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTgl_baca() {
        return tgl_baca;
    }

    public void setTgl_baca(String tgl_baca) {
        this.tgl_baca = tgl_baca;
    }
}
