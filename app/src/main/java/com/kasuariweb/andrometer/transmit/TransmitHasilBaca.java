package com.kasuariweb.andrometer.transmit;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.kasuariweb.andrometer.DBHelper;
import com.kasuariweb.andrometer.MainActivity;
import com.kasuariweb.andrometer.R;
import com.kasuariweb.andrometer.data.ListPelangganDetail;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rail on 7/1/17.
 */

public class TransmitHasilBaca extends AsyncTask<JSONObject,JSONObject,JSONObject> {
    private Context context;
    private ProgressDialog dialog;
    private JSONArray array=null;
    private List<ListPelangganDetail> listDetail;

    private String retVal;
    private final String KUNCI_KIRIM="hasil_baca";
    private final String LOG="Log Hasil Baca";
    private String url,kode_cabang;
    private boolean isBeres=false;
    private ArrayList<ListPelangganDetail> pel=null;
    private String sql,petugas,periode;

    public TransmitHasilBaca(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... params) {
        final DBHelper helper=new DBHelper(context);
        helper.ambilPengaturan();
        url="http://"+helper.getIP()+"/billing/json/";
        kode_cabang=helper.getKODE_CABANG();
        petugas=helper.getNAMA_PETUGAS();


        JSONObject json=new JSONObject();


        pel=helper.getListPelanggan(kode_cabang);


        sql="INSERT INTO dsmp_new(periode,nomor,angka,status,kondisi_meter,kondisi_pengaliran,keterangan,latitude,longitude,petugas,tgl_baca)VALUES";



        //JSONObject json=params[0];
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = new HttpPost(url);

        listDetail=new ArrayList<ListPelangganDetail>();

        try {

            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    String nama_foto;
                    String dir_foto=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/Andrometer/";
                    String ip=helper.getIP();
                    KirimFOTO kirim=new KirimFOTO();
                    int i=0;
                    for (ListPelangganDetail s:pel){
                        i++;
                        sql+="("+s.getPeriode()+",'"+s.getNomor()+"',"+s.getAngka()+","+s.getStatus()+",'"+s.getKondisi_meter()+"','"+s.getKondisi_pengaliran()+"','"+s.getKeterangan()+"','"+s.getLatitude()+"','"+s.getLongitude()+"','"+petugas+"','"+s.getTgl_baca()+"')";
                        if (i<pel.size())sql+=",";

                        nama_foto=s.getNolama()+"_"+s.getPeriode()+".jpg";
                        periode=s.getPeriode();

                        kirim.uploadFile(dir_foto+nama_foto,ip);
                        //Log.e(LOG,nama_foto);
                    }
                    sql+=" ON DUPLICATE KEY UPDATE status=VALUES(status),angka=VALUES(angka),kondisi_meter=VALUES(kondisi_meter),kondisi_pengaliran=VALUES(kondisi_pengaliran),keterangan=VALUES(keterangan),latitude=VALUES(latitude),longitude=VALUES(longitude),petugas=VALUES(petugas),tgl_baca=VALUES(tgl_baca);";
                    Log.d("SQL",sql);
                }
            });
            t.start();
            t.join();//tunggu sampai thread selesai

            json.put("sql_simpan",sql);
            json.put("petugas",petugas);
            json.put("periode",periode);
            StringEntity se = new StringEntity(KUNCI_KIRIM+"="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);

            HttpResponse response;
            response = client.execute(post);
            String resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());

            jsonResponse=new JSONObject(resFromServer);
            Log.i("Response from server", jsonResponse.getString("retVal"));

            retVal=jsonResponse.getString("retVal");

            if (retVal.equals("SUKSES")){
                array=jsonResponse.getJSONArray("hasil");
                Log.i(LOG,"mantap"+ array);
                for (int i=0;i<array.length();i++){
                    json=array.getJSONObject(i);
                    sql="UPDATE pelanggan set status=2 WHERE nomor=";
                    sql+="'"+json.getString("nomor")+"'";
                    //if (i<array.length())sql+=",";
                    //Log.i("BATAS","----------------------------------------------------");
                    //Log.i("SQL",sql);

                    helper.eksekusiSQL(sql);
                }
                isBeres=true;
                //Toast.makeText(context, "Proses kirim DSMP selesai", Toast.LENGTH_SHORT).show();
            }else {
                Log.i(LOG,retVal.toString());
            }
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            Log.e("Error Transmit",e.toString());
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e("Error Transmit",e.toString());
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.e("Error Transmit",e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return jsonResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog=new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Kirim hasil Pembacaan...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        dialog.dismiss();

        if (isBeres==true)Toast.makeText(context, "Proses kirim selesai !", Toast.LENGTH_SHORT).show();
    }

    private class KirimFOTO{

        private int uploadFile(String sourceFileUri,String ipServer) {
            final String LOGS="Kirim FOTO";
            String fileName = sourceFileUri;
            String link="http://"+ipServer+"/billing/upload_foto.php";

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);
            int serverResponseCode=0;

            if (!sourceFile.isFile()) {
                return 0;
            }else{
                try {
                    Log.e(LOGS,sourceFileUri.toString());
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(link);//upload_foto.php

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());
                    //rail=fileName;

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.e(LOGS, "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){
                        String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                +" D:/xamp/httdocs/upload/upload";
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {
                    //dialog.dismiss();
                    ex.printStackTrace();
                    Log.e(LOGS, "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    //dialog.dismiss();
                    //e.printStackTrace();
                    Log.e(LOGS, e.getMessage().toString());
                }
                return serverResponseCode;
            }
        }
    }
}
