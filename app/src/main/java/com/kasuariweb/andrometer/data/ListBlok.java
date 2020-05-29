package com.kasuariweb.andrometer.data;

/**
 * Created by rail on 6/23/17.
 */

public class ListBlok {
    private String kode_blok,total,terbaca,sisa;

    public ListBlok(String kode_blok, String total, String terbaca, String sisa) {
        this.kode_blok = kode_blok;
        this.total = total;
        this.terbaca = terbaca;
        this.sisa = sisa;
    }

    public String getKode_blok() {
        return kode_blok;
    }

    public void setKode_blok(String kode_blok) {
        this.kode_blok = kode_blok;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTerbaca() {
        return terbaca;
    }

    public void setTerbaca(String terbaca) {
        this.terbaca = terbaca;
    }

    public String getSisa() {
        return sisa;
    }

    public void setSisa(String sisa) {
        this.sisa = sisa;
    }
}
