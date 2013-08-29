package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {
    public enum CardItems {
        CardValueItem,
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
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "September 11, 2013", "12:12:12 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "March 8, 2013", "1:02:03 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "April 1, 2011", "7:17:40 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "September 11, 2013", "12:12:12 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "March 8, 2013", "1:02:03 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "April 1, 2011", "7:17:40 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "September 11, 2013", "12:12:12 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "March 8, 2013", "1:02:03 pm"));
        items.add(new CardDateTime(CardItems.CardValueItem.ordinal(), "Last update", "April 1, 2011", "7:17:40 pm"));
        CardAdapter ca = new CardAdapter(getActivity(), items, CardItems.values().length);
        list.setAdapter(ca);

        return root;
    }


}
