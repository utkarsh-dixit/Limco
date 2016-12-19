package com.example.root.citizenforumapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hudixt on 11/29/2016.
 */
public class Submit extends Fragment {
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
    private class SubmitProblem extends AsyncTask<Void,Void,Void> {
        private String prefix =  "https://himanshudixit.me/server_side/";
        private Context AppContext;
        private String problem_title;
        private String problem;
        private String encodedImage;
        private int user_id;
        public SubmitProblem(Context appContext,String problem_title,String problem,String encodedImage,int user_id){
           this.AppContext = appContext;
            this.problem_title = problem;
            this.encodedImage = encodedImage;
            this.user_id = user_id;
        }
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("title",problem);
            params.put("problem_title",problem_title);
            params.put("user_id",user_id+"");
            String response = performPostCall(prefix+"submit_problem.php",params);
            /*
            if(response.trim().equals("Yes")){
                Toast.makeText(AppContext,"Submitted",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(AppContext,"Not Submitted",Toast.LENGTH_LONG).show();
            }
            */
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            //Toast.makeText(mainView.getContext(),"Categories Downloaded",Toast.LENGTH_SHORT).show();


        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View l = inflater.inflate(R.layout.submit, container, false);
        Button sbm = (Button) l.findViewById(R.id.sbm);
        final SharedPreferences SharedPreferences = l.getContext().getSharedPreferences("LIMCO", Context.MODE_PRIVATE);

        sbm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText title = (EditText) l.findViewById(R.id.tle);
                EditText comment = (EditText) l.findViewById(R.id.cmt) ;
                ImageView img = (ImageView) l.findViewById(R.id.coverPhoto);
                img.buildDrawingCache();
                Bitmap bmap = img.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Toast.makeText(l.getContext(),"Submitted",Toast.LENGTH_LONG).show();
                //new SubmitProblem(l.getContext(),title.getText().toString(),comment.getText().toString(),encodedImage,Integer.parseInt(SharedPreferences.getString("user_id","1").trim())).execute();
            }


        });

        return l;
    }
}
