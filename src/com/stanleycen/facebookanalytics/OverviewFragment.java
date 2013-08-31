package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scen on 8/30/13.
 */
public class OverviewFragment extends Fragment {
    public static Fragment newInstance(Context context) {
        OverviewFragment f = new OverviewFragment();

        return f;
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle("Overview");
        getActivity().getActionBar().setSubtitle(null);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        FBData fbData = GlobalApp.get().fb.fbData;
            return (ViewGroup)inflater.inflate(R.layout.fragment_error_data, null);



//        return root;
    }
}