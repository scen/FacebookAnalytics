package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

/**
 * Created by scen on 8/30/13.
 */
public class CardConversation implements CardItem {
    private final FBThread fbThread;
    private int viewType;

    public int getViewType() {
        return viewType;
    }
    public CardConversation(int viewType, FBThread fbThread) {
        this.viewType = viewType;
        this.fbThread = fbThread;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, Context context) {
        View v = convertView;

        CardDateTimeHolder holder = new CardDateTimeHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_conversation, null);


            holder.name = (TextView)v.findViewById(R.id.name);
            holder.profilePic = (ProfilePictureView)v.findViewById(R.id.profilepic);

            v.setTag(holder);
        }
        else {
            holder = (CardDateTimeHolder)v.getTag();
        }

        FBUser other;
        for (String id : fbThread.participants) {

        }

        holder.profilePic.setProfileId();
        holder.profilePic.setPresetSize(ProfilePictureView.CUSTOM);
        holder.profilePic.setCropped(true);
        holder.name.setText(title);

        return v;
    }

    private class CardDateTimeHolder {
        public ProfilePictureView profilePic;
        public TextView name;
    }
}
