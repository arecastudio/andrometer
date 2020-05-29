package com.kasuariweb.andrometer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kasuariweb.andrometer.data.ListBlok;
import com.kasuariweb.andrometer.data.ListPelangganDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by android on 5/24/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private String IP,NAMA_PETUGAS,UPP,KODE_CABANG;
    private String NOMOR,NOLAMA,NAMA,ALAMAT,KONDISI_METER,KONDISI_PENGALIRAN,KETERANGAN,ANGKA,STATUS,LEVEL_PETUGAS,KODE_LEVEL;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AndroMeter.db";
    private static String SQL_CREATE_PENGATURAN = "CREATE TABLE pengaturan (idx INT PRIMARY KEY, nama TEXT, ip TEXT, cabang TEXT,level_petugas TEXT)";
    private static String SQL_CREATE_PELANGGAN = "CREATE TABLE pelanggan (nomor TEXT PRIMARY KEY, nolama TEXT, nama TEXT, cabang TEXT, kode_blok TEXT, alamat TEXT, kondisi_meter TEXT, kondisi_pengaliran TEXT, keterangan TEXT, angka TEXT NOT NULL DEFAULT '0', status INTEGER NOT NULL DEFAULT 0, periode TEXT, latitude  TEXT NOT NULL DEFAULT '0', longitude  TEXT NOT NULL DEFAULT '0',tgl_baca VARCHAR(100) NOT NULL DEFAULT '')";

    public static final String SQL_Tersisa=" AND status='0' ";
    public static final String SQL_Dibaca=" AND status<>'0' ";
    public static final String SQL_Semua="";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public String getIP() {
        return IP;
    }

    public String getNAMA_PETUGAS() {
        return NAMA_PETUGAS;
    }

    public String getUPP() {
        return UPP;
    }

    public String getKODE_CABANG() {
        return KODE_CABANG;
    }

    public String getLEVEL_PETUGAS() {
        return LEVEL_PETUGAS;
    }

    public String getKODE_LEVEL() {
        return KODE_LEVEL;
    }

    //=========================================================
    public String getNOMOR() {
        return NOMOR;
    }

    public String getNOLAMA() {
        return NOLAMA;
    }

    public String getNAMA() {
        return NAMA;
    }

    public String getALAMAT() {
        return ALAMAT;
    }

    public String getKONDISI_METER() {
        return KONDISI_METER;
    }

    public String getKONDISI_PENGALIRAN() {
        return KONDISI_PENGALIRAN;
    }

    public String getKETERANGAN() {
        return KETERANGAN;
    }

    public String getANGKA() {
        return ANGKA;
    }

    public String getSTATUS() {
        return STATUS;
    }
//==========================================
    public boolean ambilPengaturan(){
        SQLiteDatabase db=this.getReadableDatabase();
        String sql="SELECT nama,ip,cabang,level_petugas FROM pengaturan WHERE idx=1";
        Cursor rs=db.rawQuery(sql,null);
        //rs.moveToFirst();
        if(rs.moveToNext()){
            this.NAMA_PETUGAS=rs.getString(0);
            this.IP=rs.getString(1);
            this.UPP=rs.getString(2);
            this.KODE_CABANG=rs.getString(2).substring(0,2);
            this.LEVEL_PETUGAS=rs.getString(3);
            this.KODE_LEVEL=rs.getString(3).substring(0,1);
        }
        rs.close();
        db.close();
        return true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PENGATURAN);
        db.execSQL(SQL_CREATE_PELANGGAN);
        db.execSQL("INSERT OR REPLACE INTO pengaturan (idx,ip,cabang,level_petugas)VALUES(1,'10.0.3.2','01 - UPP Jayapura Selatan','1 - Petugas Koperasi')");
        Log.d("Info","Tabel telah dibuat");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE pengaturan;");
        db.execSQL("DROP TABLE pelanggan;");
        onCreate(db);
    }

    public boolean simpanPengaturan(String ip, String nama, String upp,String level_petugas){
        SQLiteDatabase db=this.getWritableDatabase();
        //String sql="UPDATE pengaturan SET nama='"+nama+"',ip='"+ip+"',upp='"+upp+"' WHERE idx=1";
        String sql="INSERT OR REPLACE INTO pengaturan(idx,nama,ip,cabang,level_petugas)VALUES(1,'"+nama+"','"+ip+"','"+upp+"','"+level_petugas+"')";
        db.execSQL(sql);
        return true;
    }

    public boolean eksekusiSQL(String sql){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql);
        return true;
    }

    public boolean entryPelanggan(String nomor, String angka, String ket, String kwm, String alir, String periode,String lat, String lon, String tgl){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="UPDATE pelanggan SET angka='"+angka+"',keterangan='"+ket+"',kondisi_meter='"+kwm+"',kondisi_pengaliran='"+alir+"',status=1, periode='"+periode+"',latitude='"+lat+"',longitude='"+lon+"',tgl_baca='"+tgl+"'  WHERE nomor='"+nomor+"' ";
        db.execSQL(sql);
        return true;
    }

    public boolean getPelanggan(String NOMOR){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM pelanggan WHERE nomor='"+NOMOR+"'", null );
        if (res.moveToNext()){
            this.NOMOR=NOMOR;
            this.NOLAMA=res.getString(res.getColumnIndex("nolama"));
            this.NAMA=res.getString(res.getColumnIndex("nama"));
            this.ALAMAT=res.getString(res.getColumnIndex("alamat"));
            this.KONDISI_METER=res.getString(res.getColumnIndex("kondisi_meter"));
            this.KONDISI_PENGALIRAN=res.getString(res.getColumnIndex("kondisi_pengaliran"));
            this.KETERANGAN=res.getString(res.getColumnIndex("keterangan"));
            this.ANGKA=res.getString(res.getColumnIndex("angka"));
            this.STATUS=res.getString(res.getColumnIndex("status"));
        }
        return true;
    }

    public boolean cekPelanggan(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM pelanggan WHERE status<>0", null );
        res.moveToFirst();
        int i=1;
        while(res.isAfterLast() == false){
            Log.i("HASIL "+i,res.getString(res.getColumnIndex("nomor"))+","+res.getString(res.getColumnIndex("nama"))+","+res.getString(res.getColumnIndex("angka"))+","+res.getString(res.getColumnIndex("status"))+","+res.getString(res.getColumnIndex("periode"))+","+res.getString(res.getColumnIndex("kondisi_meter"))+","+res.getString(res.getColumnIndex("kondisi_pengaliran"))+","+res.getString(res.getColumnIndex("keterangan")));
            res.moveToNext();
            i++;
        }
        return true;
    }

    public ArrayList<ListBlok> getBlok(String kode_cabang){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListBlok> blok=new ArrayList<ListBlok>();
        Cursor rs = db.rawQuery("SELECT p.kode_blok,COUNT(p.nomor) AS jml,COUNT(t.status) AS terbaca,(COUNT(p.nomor)-COUNT(t.status)) AS sisa FROM pelanggan AS p LEFT OUTER JOIN pelanggan AS t ON t.nomor=p.nomor AND t.status<>0 AND p.cabang=t.cabang WHERE p.cabang='"+kode_cabang+"' GROUP BY p.kode_blok ORDER BY p.kode_blok ASC", null);
        while (rs.moveToNext()){
            blok.add(new ListBlok(rs.getString(0),rs.getString(1),rs.getString(2),rs.getString(3)));
        }
        return blok;
    }

    public ArrayList<ListPelangganDetail> cariPelanggan(String keyword){
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<ListPelangganDetail> pel=new ArrayList<ListPelangganDetail>();
        Cursor rs=db.rawQuery("SELECT * FROM pelanggan WHERE nama like '%"+keyword+"%' OR alamat like '%"+keyword+"%' ",null);
        while (rs.moveToNext()){
            pel.add(new ListPelangganDetail(rs.getString(0),rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)));
        }
        return pel;
    }

    public ArrayList<ListPelangganDetail>getPelangganDetail(String kode_blok, String sqlJenis){
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<ListPelangganDetail> pel=new ArrayList<ListPelangganDetail>();
        Cursor rs=db.rawQuery("SELECT * FROM pelanggan WHERE kode_blok='"+kode_blok+"' "+sqlJenis+" ORDER BY nolama ASC, nama ASC",null);
        while (rs.moveToNext()){
            pel.add(new ListPelangganDetail(rs.getString(0),rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)));
        }
        return pel;
    }

    public ArrayList<ListPelangganDetail>getCariPelanggan(String keyword){
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<ListPelangganDetail> pel=new ArrayList<ListPelangganDetail>();
        Cursor rs=db.rawQuery("SELECT * FROM pelanggan WHERE ((nama like '%"+keyword+"%')OR(alamat  like '%"+keyword+"%')OR(nomor  like '%"+keyword+"%')OR(nolama  like '%"+keyword+"%')) ORDER BY nolama ASC, nama ASC",null);
        while (rs.moveToNext()){
            pel.add(new ListPelangganDetail(rs.getString(0),rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)));
        }
        return pel;
    }

    //fungsi untuk mengirim data terbaca dengan status=1 saja.
    public ArrayList<ListPelangganDetail>getListPelanggan(String cabang){
        SQLiteDatabase db=this.getReadableDatabase();
        //ListPelangganDetail listPel;//=new ListPelangganDetail("000000","","","","","","","","","","","");
        ArrayList<ListPelangganDetail> pel=new ArrayList<ListPelangganDetail>();
        //JSONArray array=new JSONArray();
        Cursor rs=db.rawQuery("SELECT * FROM pelanggan WHERE cabang='"+cabang+"' AND status=1",null);
        while (rs.moveToNext()){
            pel.add(new ListPelangganDetail(rs.getString(0),rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)));
            //listPel=new ListPelangganDetail(rs.getString(0),rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11));
            //pel.add(listPel);
            //array.put(listPel.toJSONObjects());
        }
        return pel;
    }

    public String getTotalSR(String kode_cabang){
        String hasil="0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs=db.rawQuery("SELECT COUNT(nomor) FROM pelanggan WHERE cabang='"+kode_cabang+"'",null);
        if (rs.moveToNext()){
            hasil=rs.getString(0);
        }
        return hasil;
    }

    public String getBacaSR(String kode_cabang){
        String hasil="0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs=db.rawQuery("SELECT COUNT(nomor) FROM pelanggan WHERE status<>0 AND cabang='"+kode_cabang+"'",null);
        if (rs.moveToNext()){
            hasil=rs.getString(0);
        }
        return hasil;
    }

    public String getSisaSR(String kode_cabang){
        String hasil="0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs=db.rawQuery("SELECT COUNT(nomor) FROM pelanggan WHERE status=0 AND  cabang='"+kode_cabang+"'",null);
        if (rs.moveToNext()){
            hasil=rs.getString(0);
        }
        return hasil;
    }

    public String getPeriode(){
        /*Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int tahun=calendar.get(Calendar.YEAR);
        int bulan=calendar.get(Calendar.MONTH);
        bulan++;
        String bln=""+bulan;
        if (bulan<11)bln="0"+bulan;
        return ""+tahun+bln;*/
        String hasil=new SimpleDateFormat("yyyyMM").format(new Date());
        return hasil;
    }

    public String getTanggalJam(){
        String hasil=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return hasil;
    }

}
