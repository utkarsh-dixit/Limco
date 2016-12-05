package com.example.root.citizenforumapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by root on 11/26/2016. app_bar_main
 */
public class Home extends Fragment {
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View main  = inflater.inflate(R.layout.app_bar_main, container, false);
        ImageButton cmp = (ImageButton) main.findViewById(R.id.Complain);
        cmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create fragment and give it an argument specifying the article it should show
                Complain newFragment = new Complain();
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


        return main;
    }
}
