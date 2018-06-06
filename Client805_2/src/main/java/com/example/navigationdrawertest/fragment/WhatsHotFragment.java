package com.example.navigationdrawertest.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.navigationdrawertest.R;

public class WhatsHotFragment extends Fragment {
	
	public WhatsHotFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_whats_hot, container, false);
         
        return rootView;
    }
}
