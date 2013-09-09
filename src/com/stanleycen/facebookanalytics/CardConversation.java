package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.joda.time.DateTime;

/**
 * Created by scen on 8/30/13.
 */
public class CardConversation implements CardItem {
    public final FBThread fbThread;
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public CardConversation(int viewType, FBThread fbThread) {
        this.viewType = viewType;
        this.fbThread = fbThread;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardConversationHolder holder = new CardConversationHolder();

        if (v == null) {
            v = (View) inflater.inflate(R.layout.card_conversation, null);


            holder.name = (TextView) v.findViewById(R.id.name);
            holder.profilePic = (ImageView) v.findViewById(R.id.profilePicture);
            holder.messages = (TextView) v.findViewById(R.id.messages);
            holder.chars = (TextView) v.findViewById(R.id.chars);
            holder.last = (TextView) v.findViewById(R.id.last);

            v.setTag(holder);
        } else {
            holder = (CardConversationHolder) v.getTag();
        }

        int w = Math.max(holder.profilePic.getWidth(), 500);
        UrlImageViewHelper.setUrlDrawable(holder.profilePic, "http://graph.facebook.com/" + fbThread.other.id + "/picture?width=" + w + "&height=" + w, R.drawable.default_profile);
//        holder.profilePic.setImageUrl("http://graph.facebook.com/" + fbThread.other.id + "/picture?width=800&height=800",R.drawable.default_profile);
        holder.name.setText((fbThread.other == null) ? ("") : ((fbThread.other.name == null || fbThread.other.name == "") ? "" : fbThread.other.name));
        holder.messages.setText(Util.getFormattedInt(fbThread.messageCount) + " messages sent & received");
        holder.chars.setText(Util.getFormattedInt(fbThread.charCount) + " characters sent & received");
        holder.last.setText("Last action " + DateUtils.getRelativeTimeSpanString(fbThread.lastUpdate.getMillis(),
                DateTime.now().getMillis(), DateUtils.MINUTE_IN_MILLIS, 0));

        return v;
    }

    private class CardConversationHolder {
        public ImageView profilePic;
        public TextView name;
        public TextView messages;
        public TextView chars;
        public TextView last;
    }
}
