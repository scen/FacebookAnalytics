package com.stanleycen.facebookanalytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

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

        CardConversationHolder holder = new CardConversationHolder();

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_conversation, null);


            holder.name = (TextView)v.findViewById(R.id.name);
            holder.profilePic = (SmartImageView)v.findViewById(R.id.profilePicture);

            v.setTag(holder);
        }
        else {
            holder = (CardConversationHolder)v.getTag();
        }

        FBUser other = null;
        for (FBUser person : fbThread.participants) {
            if (!person.id.equals(GlobalApp.get().fb.me.getId())) {
                other = person;
                break;
            }
        }

//        holder.profilePic.setProfileId(other == null ? "" : other.id);
//        holder.profilePic.setPresetSize(ProfilePictureView.LARGE);
//        holder.profilePic.setCropped(true);
        holder.profilePic.setImageUrl("http://graph.facebook.com/" + other.id + "/picture?width=1000&height=1000",R.drawable.default_profile);
        holder.name.setText((other == null) ? ("") : ((other.name == null || other.name == "") ? "" : other.name));

        return v;
    }

    private class CardConversationHolder {
        public SmartImageView profilePic;
        public TextView name;
    }
}
