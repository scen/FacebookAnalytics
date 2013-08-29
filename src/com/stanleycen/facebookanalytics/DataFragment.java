package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {
    public enum CardItems {
        CardDateTimeItem,
        CardUpdateItem,
    };

    public static Fragment newInstance(Context context) {
        DataFragment f = new DataFragment();

        // put any parameters here in a bundle

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_data, null);

        ListView list = (ListView)root.findViewById(R.id.listView);

        List<CardItem> items = new ArrayList<CardItem>();
        items.add(new CardDateTime(CardItems.CardDateTimeItem.ordinal(), "Latest data update", "September 11, 2013", "12:12:12 pm"));
        items.add(new CardDateTime(CardItems.CardDateTimeItem.ordinal(), "Amount of data",
                (new File(FBAccount.db.getReadableDatabase().getPath()).length() / 1024.0) + "", " kilobytes"));
        items.add(new CardUpdate(CardItems.CardUpdateItem.ordinal()));

        CardAdapter ca = new CardAdapter(getActivity(), items, CardItems.values().length);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(ca);
        swingBottomInAnimationAdapter.setAbsListView(list);
        list.setAdapter(swingBottomInAnimationAdapter);

        return root;
    }


}
