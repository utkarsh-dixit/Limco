package com.example.root.citizenforumapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hudixt on 11/29/2016.
 */
public class Submit extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View l = inflater.inflate(R.layout.submit, container, false);

        return l;
    }
}
