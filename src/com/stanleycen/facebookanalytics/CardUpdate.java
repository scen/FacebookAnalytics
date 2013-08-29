package com.stanleycen.facebookanalytics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class CardUpdate extends CardItem {
    private int viewType;
    CardUpdateHolder holder;

    public int getViewType() {
        return viewType;
    }
    public CardUpdate(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, int position, final Context context) {
        View v = convertView;

        if (v == null) {
            v = (View)inflater.inflate(R.layout.card_update, null);
            holder = new CardUpdateHolder();
            holder.status = (TextView)v.findViewById(R.id.status);
            holder.update = (Button)v.findViewById(R.id.button_update);
            holder.delete = (Button)v.findViewById(R.id.button_delete);
            holder.bar = (ProgressBar)v.findViewById(R.id.progressBar);
            v.setTag(holder);
        }
        else {
            holder = (CardUpdateHolder)v.getTag();
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("Update data?")
                    .setMessage("This may take a while and use a lot of data.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            holder.bar.setVisibility(View.VISIBLE);
                            holder.bar.setIndeterminate(true);
                            Intent it = new Intent(context, DataDownloaderService.class);
                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Toast
                                        .makeText(context, "Download complete!",
                                                Toast.LENGTH_LONG)
                                        .show();
                                }
                            };
                            it.putExtra(DataDownloaderService.EXTRA_MESSENGER, new Messenger(handler));
                            context.startService(it);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
            }
        });

        return v;
    }

    private class CardUpdateHolder {
        public TextView status;
        public Button update;
        public Button delete;
        public ProgressBar bar;
    }
}