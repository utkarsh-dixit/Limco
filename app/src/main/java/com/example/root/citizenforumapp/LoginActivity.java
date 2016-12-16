package com.example.root.citizenforumapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 12/13/2016.
 */
public class LoginActivity extends AppCompatActivity  {

    public static android.content.SharedPreferences SharedPreferences = null;
    private Context appContext;
    private String host = "http://192.168.42.81:80/server_side/";

    private class CheckCredentials extends AsyncTask<Void,Void,Void>{
        private String  user_name;
        private String user_pass;

        public CheckCredentials(String user_name, String user_pass){this.user_name = user_name; this.user_pass= user_pass;}
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            //Toast.makeText(mainView.getContext(),"Downloading Categories List",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Void doInBackground(Void... voids) {final String url = host+"checklogin.php?username="+user_name+"&&password="+user_pass;
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(jsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    final Boolean is_login = jsonObj.getBoolean("is_login");

                    final JSONObject finalJsonObj = jsonObj;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (is_login) {
                                SharedPreferences.edit().putBoolean("is_login",true).apply();
                                try {
                                    SharedPreferences.edit().putString("Username", finalJsonObj.getString("Username")).apply();
                                    SharedPreferences.edit().putString("First_Name", finalJsonObj.getString("First_Name")).apply();
                                    SharedPreferences.edit().putString("Last_Name", finalJsonObj.getString("Last_Name")).apply();
                                    SharedPreferences.edit().putString("user_id", finalJsonObj.getString("user_id")).apply();
                                    SharedPreferences.edit().putString("Address", finalJsonObj.getString("Address")).apply();
                                    SharedPreferences.edit().putString("ContactNo", finalJsonObj.getString("ContactNo")).apply();
                                    SharedPreferences.edit().putString("City", finalJsonObj.getString("City")).apply();
                                    SharedPreferences.edit().putString("State", finalJsonObj.getString("State")).apply();
                                    SharedPreferences.edit().putString("Pincode", finalJsonObj.getString("Pincode")).apply();
                                    SharedPreferences.edit().putString("Email", finalJsonObj.getString("Email")).apply();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(appContext, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(appContext, "Wrong Credentials", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                }catch (final JSONException e) {
                    Log.e("SAMPLE", "Json parsing error: " + e.getMessage());
                }



            }



            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            //Toast.makeText(mainView.getContext(),"Categories Downloaded",Toast.LENGTH_SHORT).show();
            this.cancel(true);

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        SharedPreferences = getSharedPreferences("LIMCO", Context.MODE_PRIVATE);
        boolean check = SharedPreferences.getBoolean("is_login", false);
        if(check){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        appContext = this.getApplicationContext();
        TextInputLayout usr = (TextInputLayout) findViewById(R.id.usr);
        usr.setHintEnabled(false);
        TextInputLayout pswd = (TextInputLayout) findViewById(R.id.pswd);
        pswd.setHintEnabled(false);
        Button sgb_Button  = (Button) findViewById(R.id.signUp);
        sgb_Button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(appContext,"The product is in beta testing. So Sign Up is disabled.",Toast.LENGTH_LONG).show();
            }
        });
        Button lgn_button = (Button) findViewById(R.id.signIn);
        final EditText user = (EditText) findViewById(R.id.username);
        final EditText user_pass = (EditText) findViewById(R.id.password);
        lgn_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //new CheckCredentials(user.getText().toString(),user_pass.getText().toString()).execute();
                new CheckCredentials(user.getText().toString(),user_pass.getText().toString()).execute();
               // DoLogin(user.getText().toString(),user_pass.getText().toString());
            }
        });
    }


}
