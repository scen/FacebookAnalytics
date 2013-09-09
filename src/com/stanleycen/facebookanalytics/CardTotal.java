package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by scen on 8/30/13.
 */
public class CardTotal implements CardItem {
    public final FBThread fbThread;
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public CardTotal(int viewType, FBThread fbThread) {
        this.viewType = viewType;
        this.fbThread = fbThread;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardTotalHolder holder = new CardTotalHolder();

        if (v == null) {
            v = (View) inflater.inflate(R.layout.card_total, null);


            holder.name = (TextView) v.findViewById(R.id.name);
            holder.profilePic = (ImageView) v.findViewById(R.id.profilePicture);
            holder.messages = (TextView) v.findViewById(R.id.messages);
            holder.chars = (TextView) v.findViewById(R.id.chars);
            holder.since = (TextView) v.findViewById(R.id.since);

            v.setTag(holder);
        } else {
            holder = (CardTotalHolder) v.getTag();
        }

        int w = Math.max(holder.profilePic.getWidth(), 500);
        UrlImageViewHelper.setUrlDrawable(holder.profilePic, "http://graph.facebook.com/" + fbThread.other.id + "/picture?width=" + w + "&height=" + w, R.drawable.default_profile);
        holder.name.setText((fbThread.other == null) ? ("") : ((fbThread.other.name == null || fbThread.other.name == "") ? "" : fbThread.other.name));
        holder.messages.setText(Util.getFormattedInt(fbThread.messageCount) + " messages &");
        holder.chars.setText(Util.getFormattedInt(fbThread.charCount) + " characters");
        if (!fbThread.messages.isEmpty())
            holder.since.setText("sent and received since " + Util.getDate(fbThread.messages.get(0).timestamp));

        return v;
    }

    private class CardTotalHolder {
        public ImageView profilePic;
        public TextView name;
        public TextView messages;
        public TextView chars;
        public TextView since;
    }
}
