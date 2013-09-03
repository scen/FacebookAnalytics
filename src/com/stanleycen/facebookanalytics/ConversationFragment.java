package com.stanleycen.facebookanalytics;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.stanleycen.facebookanalytics.graph.Bar;
import com.stanleycen.facebookanalytics.graph.Line;
import com.stanleycen.facebookanalytics.graph.LineGraph;
import com.stanleycen.facebookanalytics.graph.LinePoint;
import com.stanleycen.facebookanalytics.graph.PieSlice;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by scen on 8/30/13.
 */
public class ConversationFragment extends Fragment {
    public FBThread fbThread;
    SpinnerClickReceiver receiver;
    CardAdapter ca;
    ListView list;

    AggregateCounter msgCounter = new AggregateCounter() {
        @Override
        public int count(FBMessage message) {
            return 1;
        }
    };

    AggregateCounter charCounter = new AggregateCounter() {
        @Override
        public int count(FBMessage message) {
            return message.body.length();
        }
    };

    public enum CardItems {
        TOTAL,
        PIE_MSG,
        PIE_CHAR,
        PIE_SENTFROM,
        LOADER,
        BAR_DOW,
        BAR_CPM,
        LINE_DAY,
        LINE_NIGHT,
        HISTORY_MSG,
        HISTORY_CHAR
    };

    public ConversationFragment() {
        Log.w("Created", "receiver");
        receiver = new SpinnerClickReceiver();
    }

    public static Fragment newInstance(Context context, FBThread fbThread) {
        ConversationFragment f = new ConversationFragment();
        f.fbThread = fbThread;
        return f;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w("s", "onPause");
            getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(fbThread.title);
        getActivity().getActionBar().setSubtitle("" + DateUtils.getRelativeTimeSpanString(fbThread.lastUpdate.getMillis(),
                DateTime.now().getMillis(), DateUtils.MINUTE_IN_MILLIS, 0));
        ((MainActivity)getActivity()).unselectAllFromNav();

        IntentFilter filter = new IntentFilter("com.stanleycen.facebookanalytics.spinner.group");
        filter.addCategory("android.intent.category.DEFAULT");
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        FBData fbData = GlobalApp.get().fb.fbData;
        if (fbData == null || fbData.lastUpdate == null) {
            return (ViewGroup)inflater.inflate(R.layout.fragment_error_data, null);
        }


        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_conversation, null);

        list = (ListView)root.findViewById(R.id.listView);
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

            fbThread.msgCount = (HashMap)msgCount;

            Map<FBUser, int[]> userMsgsPerHour = new HashMap<FBUser, int[]>();

            int[] messagesPerDow = new int[8];
            int[] messagesPerHour = new int[24];

            int mobileCount = 0;
            int webCount = 0;
            int otherCount = 0;

            for (FBMessage fbMessage : fbThread.messages) {
                MutableInt cc = charCount.get(fbMessage.from);
                if (cc == null) charCount.put(fbMessage.from, new MutableInt(fbMessage.body.length()));
                else cc.add(fbMessage.body.length());

                MutableInt mc = msgCount.get(fbMessage.from);
                if (mc == null) msgCount.put(fbMessage.from, new MutableInt());
                else mc.increment();
                messagesPerDow[fbMessage.timestamp.getDayOfWeek()]++;
                messagesPerHour[fbMessage.timestamp.getHourOfDay()]++;
                int hour = fbMessage.timestamp.getHourOfDay();
                int[] userMPH = userMsgsPerHour.get(fbMessage.from);
                if (userMPH == null) {
                    userMsgsPerHour.put(fbMessage.from, new int[24]);
                }
                userMsgsPerHour.get(fbMessage.from)[hour]++;

                switch (fbMessage.source) {
                    case MOBILE:
                        mobileCount++;
                        break;
                    case WEB:
                    case OTHER:
                        webCount++;
                        break;
                    default:
                }
            }

            ret.add(new CardTotal(CardItems.TOTAL.ordinal(), fbThread));

            CardPieChart msgCard = new CardPieChart(CardItems.PIE_MSG.ordinal(), "Message distribution");
            CardPieChart charCard = new CardPieChart(CardItems.PIE_CHAR.ordinal(), "Character distribution");
            CardBarChart charsPerMessage = new CardBarChart(CardItems.BAR_CPM.ordinal(), "Characters per message");
            ArrayList<Bar> cpmBars = new ArrayList<Bar>();
            ArrayList<PieSlice> msgSlices = new ArrayList<PieSlice>(), charSlices = new ArrayList<PieSlice>();

            int idx = 0;

            msgCard.setSlices(msgSlices);
            charCard.setSlices(charSlices);
            ret.add(msgCard);
            ret.add(charCard);

            for (FBUser person : fbThread.participants) {
                String name = person == GlobalApp.get().fb.fbData.me ? "You" : person.name;
                name = name.split(" ")[0];
                Bar b = new Bar();
                b.setColor(Util.colors[idx % Util.colors.length]);
                if (msgCount.get(person) != null) b.setValue(msgCount.get(person).get() == 0 ? 0 : (float)charCount.get(person).get() / (float)msgCount.get(person).get());
                else b.setValue(0);
                b.setName(name);
                cpmBars.add(b);

                PieSlice slice = new PieSlice();
                slice.setColor(Util.colors[idx % Util.colors.length]);
                slice.setTitle(name);
                if (msgCount.get(person) != null) slice.setValue(msgCount.get(person).get());
                else slice.setValue(0);
                msgSlices.add(slice);

                slice = new PieSlice();
                slice.setColor(Util.colors[idx % Util.colors.length]);
                slice.setTitle(name);
                if (charCount.get(person) != null) slice.setValue(charCount.get(person).get());
                else slice.setValue(0);
                charSlices.add(slice);
                ++idx;
            }
            charsPerMessage.setBars(cpmBars);
            ret.add(charsPerMessage);

            CardBarChart mostActiveDow = new CardBarChart(CardItems.BAR_DOW.ordinal(), "Most active day of week");
            int firstDow = Util.getJodaFirstDayOfWeek();
            ArrayList<Bar> dowBars = new ArrayList<Bar>();
            final DateTime tmp = new DateTime();
            for (int offset = 0; offset < 7; offset++) {
                Bar b = new Bar();
                b.setName(tmp.withDayOfWeek((firstDow - 1 + offset) % 7 + 1).dayOfWeek().getAsShortText());
                b.setColor(Util.colors[offset % Util.colors.length]);
                b.setValue(messagesPerDow[(firstDow - 1 + offset) % 7 + 1]);
                dowBars.add(b);
            }
            mostActiveDow.setBars(dowBars);
            ret.add(mostActiveDow);

            CardLineChart daytimeActivity = new CardLineChart(CardItems.LINE_DAY.ordinal(), "Daytime activity");
            CardLineChart nighttimeActivity = new CardLineChart(CardItems.LINE_NIGHT.ordinal(), "Nighttime activity");

            final DateTimeFormatter hourFormatter = DateTimeFormat.forPattern("h a");


            Map<FBUser, Line> userDaytimeLines = new HashMap<FBUser, Line>();
            Map<FBUser, Line> userNighttimeLines = new HashMap<FBUser, Line>();
            idx = 0;
            for (FBUser user : fbThread.participants) {
                String name = user == GlobalApp.get().fb.fbData.me ? "You" : user.name;
                name = name.split(" ")[0];
                if (msgCount.get(user) != null) {
                    Line line = new Line();
                    line.setName(name);
                    line.setShowingPoints(true);
                    line.setColor(Util.colors[idx % Util.colors.length]);
                    userDaytimeLines.put(user, line);
                    Line line2 = new Line();
                    line2.setName(name);
                    line2.setShowingPoints(true);
                    line2.setColor(Util.colors[idx % Util.colors.length]);
                    userNighttimeLines.put(user, line2);
                    idx++;
                }
            }
            Line daytimeLine = new Line();
            daytimeLine.setName("Total");
            daytimeLine.setShowingPoints(true);
            daytimeLine.setColor(Util.colors[idx]);

            Line nighttimeLine = new Line();
            nighttimeLine.setName("Total");
            nighttimeLine.setShowingPoints(true);
            nighttimeLine.setColor(Util.colors[idx]);

            int daytimemx = 0;
            int nighttimemx = 0;
            for (int h = 6; h <= 17; h++) {
                //6am to 5pm
                daytimeLine.addPoint(new LinePoint(h, messagesPerHour[h]));
                for (Map.Entry<FBUser, Line> entry : userDaytimeLines.entrySet()) {
                    int[] da = userMsgsPerHour.get(entry.getKey());
                    entry.getValue().addPoint(new LinePoint(h, da == null ? 0 : da[h]));
                }
                daytimemx = Math.max(daytimemx, messagesPerHour[h]);
            }
            int iidx = 0;
            for (int h = 18; h <= 23; h++) {
                nighttimeLine.addPoint(new LinePoint(iidx, messagesPerHour[h]));
                for (Map.Entry<FBUser, Line> entry : userNighttimeLines.entrySet()) {
                    int[] da = userMsgsPerHour.get(entry.getKey());
                    entry.getValue().addPoint(new LinePoint(iidx, da == null ? 0 : da[h]));
                }
                iidx++;
                nighttimemx = Math.max(nighttimemx, messagesPerHour[h]);
            }
            for (int h = 0; h <= 5; h++) {
                nighttimeLine.addPoint(new LinePoint(iidx, messagesPerHour[h]));
                for (Map.Entry<FBUser, Line> entry : userNighttimeLines.entrySet()) {
                    int[] da = userMsgsPerHour.get(entry.getKey());
                    entry.getValue().addPoint(new LinePoint(iidx, da == null ? 0 : da[h]));
                }
                iidx++;
                nighttimemx = Math.max(nighttimemx, messagesPerHour[h]);
            }
            daytimeActivity.setxFormatter(new LineGraph.LabelFormatter() {
                @Override
                public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                    return hourFormatter.print(tmp.withHourOfDay((idx*ptsPerDelta) + 6));
                }
            });

            daytimeActivity.setyFormatter(new LineGraph.LabelFormatter() {
                @Override
                public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                    return (int)((max - min)*((float)idx/(float)(tot - 1))+min) + (idx==tot-1?" messages" : "");
                }
            });

            nighttimeActivity.setxFormatter(new LineGraph.LabelFormatter() {
                @Override
                public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                    return hourFormatter.print(tmp.withHourOfDay((idx*ptsPerDelta) + 6).plusHours(12));
                }
            });

            nighttimeActivity.setyFormatter(new LineGraph.LabelFormatter() {
                @Override
                public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                    return (int)((max - min)*((float)idx/(float)(tot - 1))+min) + (idx==tot-1?" messages" : "");
                }
            });

            ArrayList<Line> daytimeLines = new ArrayList<Line>(), nighttimeLines = new ArrayList<Line>();
            for (Map.Entry<FBUser, Line> entry : userDaytimeLines.entrySet()) {
                daytimeLines.add(entry.getValue());
            }
            for (Map.Entry<FBUser, Line> entry : userNighttimeLines.entrySet()) {
                nighttimeLines.add(entry.getValue());
            }
            daytimeLines.add(daytimeLine);
            nighttimeLines.add(nighttimeLine);
            daytimeActivity.setLines(daytimeLines);
            nighttimeActivity.setLines(nighttimeLines);
            daytimeActivity.setRangeY(0, Util.roundUpNiceDiv4(daytimemx));
            nighttimeActivity.setRangeY(0, Util.roundUpNiceDiv4(nighttimemx));


            ret.add(daytimeActivity);
            ret.add(nighttimeActivity);

            CardLineChartSpinner msghistory = new CardLineChartSpinner(CardItems.HISTORY_MSG.ordinal(), "Message history over time");
            loadHistory(msghistory, 1, msgCounter, " messages");
            ret.add(msghistory);

            CardLineChartSpinner charhistory = new CardLineChartSpinner(CardItems.HISTORY_CHAR.ordinal(), "Character history over time");
            loadHistory(charhistory, 1, charCounter, " characters");
            ret.add(charhistory);


            CardPieChart sentFromCard = new CardPieChart(CardItems.PIE_SENTFROM.ordinal(), "Devices sent from");
            PieSlice webSlice = new PieSlice();
            webSlice.setColor(Util.colors[0]);
            webSlice.setTitle("Web");
            webSlice.setValue(webCount);
            PieSlice mobileSlice = new PieSlice();
            mobileSlice.setColor(Util.colors[1]);
            mobileSlice.setTitle("Mobile");
            mobileSlice.setValue(mobileCount);
//            PieSlice otherSlice = new PieSlice();
//            otherSlice.setColor(Util.colors[2]);
//            otherSlice.setTitle("Other");
//            otherSlice.setValue(otherCount);
            sentFromCard.setSlices(new ArrayList<PieSlice>(Arrays.asList(webSlice, mobileSlice)));
            ret.add(sentFromCard);


            return ret;
        }

        @Override
        protected void onPostExecute(List<CardItem> result) {
            ca.clear();
            ca.addAll(result);
        }
    }

    DateTime getBucketEndpoint(DateTime start, int bucketSize) {
        switch (bucketSize) {
            case 0:
                return start.plusDays(1);
            case 1:
                return start.plusWeeks(1);
            case 2:
                return start.plusMonths(1);
            case 3:
                return start.plusYears(1);
            default:
                Log.w("loadHistory", "unknown bucketSize");
        }
        return start;
    }


    void loadHistory(CardLineChartSpinner card, int bucketSize, AggregateCounter counter, final String suffix) {
        /*
            0=day
            1=week
            2=month
            3=year
         */
        if (fbThread.messages.isEmpty()) return;
        DateTime startDate = fbThread.messages.get(0).timestamp.withTimeAtStartOfDay();
        DateTime bucketEndpoint = getBucketEndpoint(startDate, bucketSize);
        Interval curBucket = new Interval(startDate, bucketEndpoint);
        DateTime endDate = DateTime.now().withTimeAtStartOfDay();

        Line totalLine = new Line();
        Map<FBUser, Line> userLines = new HashMap<FBUser, Line>();
        for (FBUser user : fbThread.participants) {
            if (fbThread.msgCount.get(user) != null) userLines.put(user, new Line());
        }

        int msgIndex = 0;
        int size = fbThread.messages.size();
        Map<FBUser, MutableInt> curBucketUserCount = new HashMap<FBUser, MutableInt>();

        int idx = 0;
        int accum = 0;
        int maxval = 0;

        while (true) {
            if (msgIndex >= size) break;

            int curBucketTotal = 0;
            curBucketUserCount.clear();

            for (; msgIndex < size;) {
                FBMessage msg = fbThread.messages.get(msgIndex);
                DateTime normalized = msg.timestamp.withTimeAtStartOfDay();
                if (curBucket.contains(normalized)) {
                    int cnt = counter.count(msg);
                    curBucketTotal += cnt;
                    MutableInt i = curBucketUserCount.get(msg.from);
                    if (i == null) {
                        curBucketUserCount.put(msg.from, new MutableInt(cnt));
                    }
                    else {
                        i.add(cnt);
                    }

                    msgIndex++;
                }
                else {
                    break;
                }
            }

            totalLine.addPoint(new LinePoint(idx, curBucketTotal));
            for (Map.Entry<FBUser, Line> entry : userLines.entrySet()) {
                MutableInt val = curBucketUserCount.get(entry.getKey());
                entry.getValue().addPoint(new LinePoint(idx, val == null ? 0 : val.get()));
            }
            accum += curBucketTotal;
            maxval = Math.max(maxval, curBucketTotal);
//            Log.v("dbg", "idx = " + idx + " tot = " + curBucketTotal + " accum = " + accum);
            idx++;
            startDate = bucketEndpoint;
            bucketEndpoint = getBucketEndpoint(startDate, bucketSize);
            curBucket = new Interval(startDate, bucketEndpoint);
            if (startDate.isAfter(endDate)) break;
        }

        boolean showPoints = totalLine.getPoints().size() <= 30;

        idx = 0;
        ArrayList<Line> lines = new ArrayList<Line>();
        for (Map.Entry<FBUser, Line> entry : userLines.entrySet()) {
            Line l = entry.getValue();
            String name = entry.getKey() == GlobalApp.get().fb.fbData.me ? "You" : entry.getKey().name;
            name = name.split(" ")[0];
            l.setColor(Util.colors[idx % Util.colors.length]);
            l.setName(name);
            l.setShowingPoints(showPoints);
            lines.add(l);
            idx++;
        }
        totalLine.setColor(Util.colors[idx % Util.colors.length]);
        totalLine.setName("Total");
        totalLine.setShowingPoints(showPoints);
        lines.add(totalLine);
        card.setLines(lines);
        card.setShouldCacheToBitmap(true);
        card.setRangeY(0, Util.roundUpNiceDiv4((float)maxval));
        card.setyFormatter(new LineGraph.LabelFormatter() {
            @Override
            public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                return (int)((max - min)*((float)idx/(float)(tot - 1))+min) + (idx==tot-1? suffix : "");
            }
        });

        final DateTime s = fbThread.messages.get(0).timestamp.withTimeAtStartOfDay();
        switch(bucketSize) {
            case 0:
                card.setxFormatter(new LineGraph.LabelFormatter() {
                    @Override
                    public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                        if (idx == 0) return initialDayFormatter.print(s.plusDays(idx*ptsPerDelta));
                        else
                            return dayFormatter.print(s.plusDays(idx*ptsPerDelta));
                    }
                });
                break;
            case 1:
                card.setxFormatter(new LineGraph.LabelFormatter() {
                    @Override
                    public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                        if (idx == 0) return initialDayFormatter.print(s.plusWeeks(idx * ptsPerDelta));
                        else
                            return dayFormatter.print(s.plusWeeks(idx * ptsPerDelta));
                    }
                });
                break;
            case 2:
                card.setxFormatter(new LineGraph.LabelFormatter() {
                    @Override
                    public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                        if (idx == 0) return initialMonthFormatter.print(s.plusMonths(idx*ptsPerDelta));
                        else return monthFormatter.print(s.plusMonths(idx*ptsPerDelta));
                    }
                });
                break;
            case 3:
                card.setxFormatter(new LineGraph.LabelFormatter() {
                    @Override
                    public String format(int idx, int tot, float min, float max, int ptsPerDelta) {
                        return yearFormatter.print(s.plusYears(idx*ptsPerDelta));
                    }
                });
                break;
        }
    }

    DateTimeFormatter initialDayFormatter = DateTimeFormat.forPattern("MMM d ''yy");
    DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("MMM d");
    DateTimeFormatter initialMonthFormatter = DateTimeFormat.forPattern("MMM ''yy");
    DateTimeFormatter monthFormatter = DateTimeFormat.forPattern("MMM");
    DateTimeFormatter yearFormatter = DateTimeFormat.forPattern("yyyy");

    class SpinnerClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            int value = intent.getIntExtra("value", 0);
//            ca.remove(5);
            if (title.equals("Message history over time")) {
                int size = ca.getCount();
                int idx = 0;
                for (idx = 0; idx < size; idx++) {
                    if (ca.getItem(idx) instanceof CardLineChartSpinner && ((CardLineChartSpinner) ca.getItem(idx)).title.equals("Message history over time")) {
                        CardLineChartSpinner card = ((CardLineChartSpinner) ca.getItem(idx));
                        loadHistory(card, value, msgCounter, " messages");
                        card.refreshLineChart();
                        card.invalidateChart();
                        break;
                    }
                }

            }
            if (title.equals("Character history over time")) {
                int size = ca.getCount();
                int idx = 0;
                for (idx = 0; idx < size; idx++) {
                    if (ca.getItem(idx) instanceof CardLineChartSpinner && ((CardLineChartSpinner) ca.getItem(idx)).title.equals("Character history over time")) {
                        CardLineChartSpinner card = ((CardLineChartSpinner) ca.getItem(idx));
                        loadHistory(card, value, charCounter, " characters");
                        card.refreshLineChart();
                        card.invalidateChart();
                        break;
                    }
                }

            }
        }
    }

    private abstract interface AggregateCounter {
        public abstract int count(FBMessage message);
    }
}
