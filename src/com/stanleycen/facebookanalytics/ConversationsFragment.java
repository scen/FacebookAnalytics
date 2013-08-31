package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by scen on 8/30/13.
 */
public class ConversationsFragment extends Fragment {
    public static Fragment newInstance(Context context) {
        ConversationsFragment f = new ConversationsFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        FBData fbData = GlobalApp.get().fb.fbData;
        if (fbData == null || fbData.lastUpdate == null) {
            return (ViewGroup)inflater.inflate(R.layout.fragment_error_data, null);
        }
        //TODO
        // IF FBDATA is BAD/NULL, inflate a different layout with "please collect data"

        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_conversation, null);

        ListView list = (ListView)root.findViewById(R.id.listView);

        List<CardItem> items = new ArrayList<CardItem>();
        for (FBThread fbThread : fbData.threads) {
            if (!fbThread.isGroupConversation) {
                items.add(new CardConversation(0, fbThread));
            }
        }

        CardAdapter ca = new CardAdapter(getActivity(), items, 1);
//        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(ca);
//        swingBottomInAnimationAdapter.setAbsListView(list);
        list.setAdapter(ca);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "" + i, Toast.LENGTH_SHORT).show();
            }
        });

        setRetainInstance(true);

        return root;
    }
}
