package com.example.root.citizenforumapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created to Handle Complain Fragment
 * Ask the server to send JSON for the query specified and then set the recycler view
 * to show the complaints
 */
public class Complain extends Fragment {
    ArrayList<HashMap<String, String>> jsonData = new ArrayList<HashMap<String, String>>();
    private class StartMain extends AsyncTask<Void, Void, Void> {
        public View lV = null;

        public StartMain(View l) {
            this.lV = l;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(lV.getContext(),"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://192.168.1.103:8080/server_side/interactor.php?page=0"; // Specify the url
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONArray jsonObj = null;
                    try {
                        jsonObj = new JSONArray(jsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Getting JSON Array node

                    Log.e("Check it",jsonObj.length() + "");
                    // looping through All Contacts
                    for (int i = 0; i < jsonObj.length(); i++) {
                        JSONObject c = jsonObj.getJSONObject(i);

                        String unique_id = c.getString("unique_id");
                        String problem_details = c.getString("problem_details");
                        String location_photo = c.getString("location_photo");



                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("unique_id", unique_id);
                        contact.put("problem_details", problem_details);
                        contact.put("location_photo", location_photo);


                        // adding contact to contact list
                        jsonData.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e("SAMPLE", "Json parsing error: " + e.getMessage());


                }

            } else {
                Log.e("Sorry", "Couldn't get json from server.");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ItemData t[];
            List<ItemData> dat = new ArrayList<>();
            for(int i =0;i<jsonData.size();i++){
                    String problem_details = jsonData.get(i).get("problem_details");
                    String photo = jsonData.get(i).get("location_photo");
                    String unique_id = jsonData.get(i).get("unique_id");
                    dat.add(new ItemData("Test",photo,problem_details,unique_id));
            }
            // 3. create an adapter
            MyAdapter mAdapter = new MyAdapter(dat);
            // 4. set adapter
            RecyclerView  rv = (RecyclerView) lV.findViewById(R.id.rv);
            rv.setAdapter(mAdapter);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View l = inflater.inflate(R.layout.complain, container, false);

       RecyclerView  rv = (RecyclerView) l.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        // 2. set layoutManger
        rv.setLayoutManager(new LinearLayoutManager(l.getContext()));
        FloatingActionButton fab = (FloatingActionButton) l.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                // Create fragment and give it an argument specifying the article it should show
                Submit newFragment = new Submit();
                Bundle args = new Bundle();

                newFragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();
            }
        });
        // this is data fro recycler view

        new StartMain(l).execute();

        return l;
    }



}
