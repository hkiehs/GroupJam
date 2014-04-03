package com.noextent.groupjam.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;

import java.util.List;

public class ChannelDialogFragment extends DialogFragment {
    private static final String LOG_TAG = "ChannelDialogFragment";

    private MusicPlayerApplication application;

    public ChannelDialogFragment(MusicPlayerApplication musicPlayerApplication) {
        this.application = musicPlayerApplication;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.test_list_item);
        final List<String> channels = application.getFoundChannels();
        for (String channel : channels) {
            int lastDot = channel.lastIndexOf('.');
            if (lastDot < 0) {
                continue;
            }
            channelListAdapter.add(channel.substring(lastDot + 1));
        }
        channelListAdapter.notifyDataSetChanged();

        builder.setTitle(R.string.select_group).setAdapter(channelListAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(LOG_TAG, "Selected group [" + channels.get(which) + "]");
            }
        });
//                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                    }
//                });
        return builder.create();
    }
}