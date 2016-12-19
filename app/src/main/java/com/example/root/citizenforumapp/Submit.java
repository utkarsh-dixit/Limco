package com.example.root.citizenforumapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import br.com.simplepass.loading_button_lib.CircularProgressButton;

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
        private int category;
        private String location;
        private CircularProgressButton t;
        public SubmitProblem(Context appContext,String problem_title,String problem,String encodedImage,int user_id,int category, String location, CircularProgressButton p){
           this.AppContext = appContext;
            this.problem_title = problem_title;
            this.encodedImage = encodedImage;
            this.user_id = user_id;
            this.category = category;
            this.problem = problem;
            this.location = location;
            this.t = p;
        }
        public String postData(String url) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("Problem",
                        problem));
                nameValuePairs.add(new BasicNameValuePair("problem_title",
                        problem_title));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        user_id+""));
                nameValuePairs.add(new BasicNameValuePair("encoded_image",
                        encodedImage));
                nameValuePairs.add(new BasicNameValuePair("category",
                        category+""));
                nameValuePairs.add(new BasicNameValuePair("location",
                        location));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                String str = "";

                while ((str = rd.readLine()) != null) {
                    builder.append(str);
                }

                String text = builder.toString();
                return text;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return "No";
        }


        @Override
        protected  void onPreExecute(){
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... voids) {

            //Toast.makeText(AppContext,category,Toast.LENGTH_SHORT).show();
          final String response = postData(prefix+"submit_problem.php");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(response.trim().equals("Yes")){
                        Toast.makeText(getContext(),"Submitted",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getContext(),"Not Submitted",Toast.LENGTH_LONG).show();
                    }

                }
                });

            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    t.revertAnimation();
                }
            });
            //Toast.makeText(mainView.getContext(),"Categories Downloaded",Toast.LENGTH_SHORT).show();


        }
    }
    private ImageView coverPhoto;
    private Uri imageURI;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View l = inflater.inflate(R.layout.submit, container, false);
        final CircularProgressButton sbm = (CircularProgressButton) l.findViewById(R.id.sbm);
        final SharedPreferences SharedPreferences = l.getContext().getSharedPreferences("LIMCO", Context.MODE_PRIVATE);
        coverPhoto = (ImageView) l.findViewById(R.id.coverPhoto);
        final Spinner spinner = (Spinner) l.findViewById(R.id.Category);
        String cat[] = {"Road/FootPaths","Street Lights/ Traffic Signal","Traffic Violations",  "Garbage Issues","Illegal Constructions / Encroachments" };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(l.getContext(),   android.R.layout.simple_spinner_item, cat);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

        spinner.setAdapter(spinnerArrayAdapter);
        final EditText location  = (EditText) l.findViewById(R.id.loc);
        sbm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText title = (EditText) l.findViewById(R.id.tle);
                EditText comment = (EditText) l.findViewById(R.id.cmt) ;
                sbm.startAnimation();
                ImageView img = (ImageView) l.findViewById(R.id.coverPhoto);

                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bmap = drawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.JPEG, 45, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
               // Toast.makeText(l.getContext(),"Submitted",Toast.LENGTH_LONG).show();
                int catId = spinner.getSelectedItemPosition()+1;
               new SubmitProblem(l.getContext(),title.getText().toString(),comment.getText().toString(),encodedImage,Integer.parseInt(SharedPreferences.getString("user_id","1").trim()),catId,location.getText().toString(),sbm).execute();
            }


        });
        FloatingActionButton btn = (FloatingActionButton) l.findViewById(R.id.camera);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), "CheckingPost.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                Uri imageUri = Uri.fromFile(photo);
                imageURI = imageUri;
                startActivityForResult(intent, 1888);
            }
        });

        return l;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1888) {
            if (resultCode == Activity.RESULT_OK) {


               coverPhoto.setImageURI(imageURI);



            }
        }


    }
}
