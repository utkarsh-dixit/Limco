package com.example.root.citizenforumapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 11/26/2016.
 */
public class Complain extends Fragment {
    ArrayList<HashMap<String, String>> jsonData;
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
            String url = "http://192.168.1.100:8080/server_side/interactor.php?page=0";
            String jsonStr = sh.makeServiceCall(url);

            Log.e("THIS IS RESPONSE", "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(jsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Getting JSON Array node


                    // looping through All Contacts
                    for (int i = 0; i < jsonObj.length(); i++) {
                        JSONObject c = jsonObj.getJSONObject(i+"");

                        String unique_id = c.getString("unqiue_id");
                        String problem_details = c.getString("problem_details");
                        String location_photo = c.getString("location_photo");



                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("unqiue_id", unique_id);
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
                    dat.add(new ItemData("Test",photo,problem_details));
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
        ItemData itemsData[] = {
                new ItemData("Illegal Construction","http://www.udaipurtimes.com/wp-content/uploads/2013/08/illegal-construction.jpg","Lorem ipsum dolor sit amet, eu sumo liber est, mentitum voluptaria mea ut. Officiis invidunt menandri nam eu, duo euismod mentitum an. Ne brute habemus nusquam sit. His justo dicunt omnium cu, usu etiam velit ea, ...."),
                new ItemData("Lease Issue","http://www.udaipurtimes.com/wp-content/uploads/2013/08/illegal-construction.jpg","Lorem ipsum dolor sit amet, eu sumo liber est, mentitum voluptaria mea ut. Officiis invidunt menandri nam eu, duo euismod mentitum an. Ne brute habemus nusquam sit. His justo dicunt omnium cu, usu etiam velit ea, ...."),
                new ItemData("Some Problem","http://www.udaipurtimes.com/wp-content/uploads/2013/08/illegal-construction.jpg","Lorem ipsum dolor sit amet, eu sumo liber est, mentitum voluptaria mea ut. Officiis invidunt menandri nam eu, duo euismod mentitum an. Ne brute habemus nusquam sit. His justo dicunt omnium cu, usu etiam velit ea, ...."),
                new ItemData("Bla Bla Bla","http://www.udaipurtimes.com/wp-content/uploads/2013/08/illegal-construction.jpg","Lorem ipsum dolor sit amet, eu sumo liber est, mentitum voluptaria mea ut. Officiis invidunt menandri nam eu, duo euismod mentitum an. Ne brute habemus nusquam sit. His justo dicunt omnium cu, usu etiam velit ea, ...."),
                new ItemData("Illegal Construction","http://www.udaipurtimes.com/wp-content/uploads/2013/08/illegal-construction.jpg","Lorem ipsum dolor sit amet, eu sumo liber est, mentitum voluptaria mea ut. Officiis invidunt menandri nam eu, duo euismod mentitum an. Ne brute habemus nusquam sit. His justo dicunt omnium cu, usu etiam velit ea, ...."),
        };
        new StartMain(l).execute();

        return l;
    }



}
