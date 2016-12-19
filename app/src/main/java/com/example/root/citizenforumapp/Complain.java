package com.example.root.citizenforumapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
    String prefix = "https://himanshudixit.me/server_side/";
    private class GetCategories extends AsyncTask<Void,Void,Void>{
        private TabLayout rootTab;
        private View mainView;
        private Complain given;
        public GetCategories(View l,TabLayout root,Complain cmp){this.mainView = l; this.rootTab = root;this.given = cmp;}
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            //Toast.makeText(mainView.getContext(),"Downloading Categories List",Toast.LENGTH_SHORT).show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = prefix+"categories.php"; // Specify the url
            String jsonStr = sh.makeServiceCall(url);
            Log.e("my",jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray jsonObj = null;
                    try {
                        jsonObj = new JSONArray(jsonStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < jsonObj.length(); i++) {
                        JSONObject c = jsonObj.getJSONObject(i);
                        String category_id = c.getString("category_id");
                        String category_name = c.getString("category_name");
                      given.AddaTab(rootTab,category_name,category_id);
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
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            //Toast.makeText(mainView.getContext(),"Categories Downloaded",Toast.LENGTH_SHORT).show();


        }
    }
    public void AddaTab(final TabLayout m, final String category_name, final String category_id){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TabLayout.Tab t = m.newTab();
                t.setText(category_name);
                t.setTag(category_id);
              //  Toast.makeText(m.getContext(),category_name,Toast.LENGTH_SHORT);
                m.addTab(t);
            }
        });

        return;
    }
    private class StartMain extends AsyncTask<Void, Void, Void> {
        public View lV = null;
        public String url = "";
        public StartMain(View l,String url) {
            this.lV = l; this.url = url;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jsonData.clear();

          //  Toast.makeText(lV.getContext(),"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
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
                        String problemTitle = c.getString("Problem_Title");


                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("unique_id", unique_id);
                        contact.put("problem_details", problem_details);
                        contact.put("location_photo", location_photo);
                        contact.put("problemTitle",problemTitle);

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
                    String tle = jsonData.get(i).get("problemTitle");
                    dat.add(new ItemData(tle,photo,problem_details,unique_id));
            }
            // 3. create an adapter
            MyAdapter mAdapter = new MyAdapter(dat);

            mAdapter.mang = getFragmentManager();
            // 4. set adapter
            RecyclerView  rv = (RecyclerView) lV.findViewById(R.id.rv);
            rv.setAdapter(mAdapter);

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View l = inflater.inflate(R.layout.complain, container, false);
        final TabLayout ctrl = (TabLayout)  l.findViewById(R.id.TabCtrl);
        final Complain m = this;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                new GetCategories(l,ctrl,m).execute();
            }
        });
        ctrl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(final TabLayout.Tab tab)
            {

                        new StartMain(l, prefix + "interactor.php?page=0&cat=" + tab.getTag().toString()).execute();

                // Toast.makeText(l.getContext(),tab.getTag()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
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

        new StartMain(l,prefix+"interactor.php?page=0").execute();

        return l;
    }



}
