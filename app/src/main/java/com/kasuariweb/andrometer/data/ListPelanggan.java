package com.kasuariweb.andrometer.data;

/**
 * Created by rail on 6/21/17.
 */

public class ListPelanggan {
    private String nomor,nolama,nama,cabang,kode_blok,alamat;

    public ListPelanggan(String nomor, String nolama, String nama, String cabang, String kode_blok, String alamat) {
        this.nomor=nomor;
        this.nolama=nolama;
        this.nama=nama;
        this.cabang=cabang;
        this.kode_blok=kode_blok;
        this.alamat=alamat;
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
}
