package com.kasuariweb.andrometer.transmit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kasuariweb.andrometer.DBHelper;
import com.kasuariweb.andrometer.MainActivity;
import com.kasuariweb.andrometer.data.ListPelanggan;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rail on 6/21/17.
 */

public class TransmitPelanggan extends AsyncTask<JSONObject, JSONObject,JSONObject> {
    private Context context;
    private ProgressDialog dialog;
    private List<ListPelanggan> pelangganList;
    private JSONArray array=null;

    private String retVal;
    private final String KUNCI_KIRIM="kirim_json";
    private String url="http://10.0.3.2/billing/json/";


    //private downloadAdapter adapter


    public TransmitPelanggan(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... params) {

        DBHelper helper=new DBHelper(context);
        helper.ambilPengaturan();
        url="http://"+helper.getIP()+"/billing/json/";

        JSONObject json=params[0];
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = new HttpPost(url);

        pelangganList=new ArrayList<ListPelanggan>();

        try {
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
                String sql;

                array=jsonResponse.getJSONArray("hasil");
                for (int i=0;i<array.length();i++){
                    json=array.getJSONObject(i);
                    //ListPelanggan list=new ListPelanggan(json.getString("nomor"),json.getString("nolama"),json.getString("nama"),json.getString("cabang"),json.getString("kode_blok"),json.getString("alamat"));
                    //pelangganList.add(list);

                    //Log.i("nama",json.getString("nama"));
                    sql="INSERT OR IGNORE INTO pelanggan(nomor,nolama,nama,cabang,kode_blok,alamat)VALUES";
                    sql+="('"+json.getString("nomor")+"'";
                    sql+=",'"+json.getString("nolama")+"'";
                    sql+=",'"+json.getString("nama")+"'";
                    sql+=",'"+json.getString("cabang")+"'";
                    sql+=",'"+json.getString("kode_blok")+"'";
                    sql+=",'"+json.getString("alamat")+"')";
                    //if (i<array.length())sql+=",";
                    //Log.i("BATAS","----------------------------------------------------");
                    //Log.i("SQL",sql);

                    helper.eksekusiSQL(sql);
                }
                helper=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog=new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Download data Pelanggan...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        dialog.dismiss();
    }
}
