package com.example.root.citizenforumapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Adapter for RecyclerView
 * Handles all the cardViews that are shown in the complaint section.
 * Remember to assign unique_id to the posts for click handling.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<ItemData> itemsData;
    public FragmentManager mang;
    public MyAdapter(List<ItemData> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder( ViewGroup parent,
                                                   int viewType) {

        // create a new view
        final View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forumcard, null,true);
        final Context pContext = parent.getContext();

        itemLayoutView.findViewById(R.id.mainCard).setOnClickListener(new View.OnClickListener() {
                                                                          @Override
                                                                          public void onClick(View view) {
                                                                              String unq_id = view.getTag().toString();

                                                                              Toast.makeText(pContext,unq_id,Toast.LENGTH_LONG).show();
                                                                              /*
                                                                              // Click action
                                                                              // Create fragment and give it an argument specifying the article it should show
                                                                              ComplaintDetails newFragment = new ComplaintDetails();

                                                                              Bundle args = new Bundle();

                                                                              newFragment.setArguments(args);

                                                                              FragmentTransaction transaction = mang.beginTransaction();
                                                                              transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
                                                                              transaction.replace(R.id.fragment_container, newFragment);
                                                                              transaction.addToBackStack(null);

// Commit the transaction
                                                                              transaction.commit();
                                                                              */
                                                                          }
                                                                      });
        WindowManager windowManager = (WindowManager)itemLayoutView.getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        itemLayoutView.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar pb = null;

        public DownloadImageTask(ImageView imgViewIcon, ProgressBar pb) {
            this.pb = pb;
            this.bmImage = imgViewIcon;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(pb!=null)
            pb.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
        }
    }
    // Replace the contents of a view (invoked by the layout manager)
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.d("NOTICE", "getBitmapFromURL: thIS IS NOTWORK");
            return null;
        }
    }
    private  String ResizeString(String main){
        int offSet = 200;
        if(main.length()>200){
            main = main.substring(0,offSet)  + "...";
        }
        return main;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
        String problemDescription = itemsData.get(position).getProblemDescription();
        viewHolder.problem_desc.setText(ResizeString(itemsData.get(position).getProblemDescription()));
        try{
             viewHolder.itemView.setTag(itemsData.get(position).returnUniqueId());

        }
        catch(NullPointerException ignored){

        }
       // viewHolder.imgViewIcon.setImageBitmap(getBitmapFromURL("http://patrickcoombe.com/wp-content/uploads/2015/09/new-google-logo-2015.png"));
        new DownloadImageTask((ImageView) viewHolder.imgViewIcon,(ProgressBar) viewHolder.pb)
             .execute(itemsData.get(position).getImageUrl());


    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
       public ImageView imgViewIcon;
        public ProgressBar pb;
        public TextView problem_desc;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.category);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.locationImage);
            pb = (ProgressBar)  itemLayoutView.findViewById(R.id.ImageLoading);
            problem_desc = (TextView) itemLayoutView.findViewById(R.id.problem_desc);

        }
    }
    public void clearData() {
        int size = this.itemsData.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.itemsData.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

}
