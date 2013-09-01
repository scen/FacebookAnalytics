package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.stanleycen.facebookanalytics.graph.PieSlice;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by scen on 8/30/13.
 */
public class ConversationFragment extends Fragment {
    public FBThread fbThread;
    CardAdapter ca;

    public enum CardItems {
        TOTAL,
        PIE,
        LOADER
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
        Util.addSeparatingHeaderView(getActivity(), inflater, list);


        final List<CardItem> items = new ArrayList<CardItem>();

        items.add(new CardLoader(CardItems.LOADER.ordinal(), "Loading conversation"));

        ca = new CardAdapter(getActivity(), items, CardItems.values().length);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(ca);
        swingBottomInAnimationAdapter.setAbsListView(list);
        list.setAdapter(swingBottomInAnimationAdapter);

        setRetainInstance(true);

        new Worker().execute();

        return root;
    }

    private class Worker extends AsyncTask<Void, Void, List<CardItem>> {
        @Override
        protected List<CardItem> doInBackground(Void... params) {
            Intent i = new Intent("com.stanleycen.facebookanalytics.update");
            i.putExtra("action", "indeterminate");
            getActivity().sendBroadcast(i);

            List<CardItem> ret = new ArrayList<CardItem>();

            Map<FBUser, MutableInt> charCount = new HashMap<FBUser, MutableInt>();
            Map<FBUser, MutableInt> msgCount = new HashMap<FBUser, MutableInt>();

            for (FBMessage fbMessage : fbThread.messages) {
                MutableInt cc = charCount.get(fbMessage.from);
                if (cc == null) charCount.put(fbMessage.from, new MutableInt());
                else cc.add(fbMessage.body.length());

                MutableInt mc = msgCount.get(fbMessage.from);
                if (mc == null) msgCount.put(fbMessage.from, new MutableInt());
                else mc.increment();
            }

            ret.add(new CardTotal(CardItems.TOTAL.ordinal(), fbThread));

            CardPieChart msgCard = new CardPieChart(CardItems.PIE.ordinal(), "Messages sent");
            CardPieChart charCard = new CardPieChart(CardItems.PIE.ordinal(), "Characters sent");

            ArrayList<PieSlice> msgSlices = new ArrayList<PieSlice>(), charSlices = new ArrayList<PieSlice>();

            int idx = 0;

            for (Map.Entry<FBUser, MutableInt> entry : msgCount.entrySet()) {
                String name = entry.getKey() == GlobalApp.get().fb.fbData.me ? "You" : entry.getKey().name;
                PieSlice slice = new PieSlice();
                slice.setColor(Util.colors[idx % Util.colors.length]);
                slice.setTitle(name);
                slice.setValue(entry.getValue().get());
                msgSlices.add(slice);

                slice = new PieSlice();
                slice.setColor(Util.colors[idx % Util.colors.length]);
                slice.setTitle(name);
                slice.setValue(charCount.get(entry.getKey()).get());
                charSlices.add(slice);
                ++idx;
            }

            msgCard.setSlices(msgSlices);
            charCard.setSlices(charSlices);
            ret.add(msgCard);
            ret.add(charCard);

            return ret;
        }

        @Override
        protected void onPostExecute(List<CardItem> result) {
            ca.clear();
            ca.addAll(result);
        }
    }
}
