package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scen on 8/30/13.
 */
public class ConversationFragment extends Fragment {
    public FBThread fbThread;

    public enum CardItems {
        TOTAL,
        PIE
    };

    public static Fragment newInstance(Context context, FBThread fbThread) {
        ConversationFragment f = new ConversationFragment();
        f.fbThread = fbThread;
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(fbThread.title);
        getActivity().getActionBar().setSubtitle("" + DateUtils.getRelativeTimeSpanString(fbThread.lastUpdate.getMillis(),
                DateTime.now().getMillis(), DateUtils.MINUTE_IN_MILLIS, 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        FBData fbData = GlobalApp.get().fb.fbData;
        if (fbData == null || fbData.lastUpdate == null) {
            return (ViewGroup)inflater.inflate(R.layout.fragment_error_data, null);
        }


        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_conversation, null);

        ListView list = (ListView)root.findViewById(R.id.listView);

        List<CardItem> items = new ArrayList<CardItem>();

        items.add(new CardTotal(CardItems.TOTAL.ordinal(), fbThread));
        items.add(new CardPieChart(CardItems.PIE.ordinal(), "Messages per day"));


        final CardAdapter ca = new CardAdapter(getActivity(), items, CardItems.values().length);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(ca);
        swingBottomInAnimationAdapter.setAbsListView(list);
        list.setAdapter(swingBottomInAnimationAdapter);

        setRetainInstance(true);

        return root;
    }
}
