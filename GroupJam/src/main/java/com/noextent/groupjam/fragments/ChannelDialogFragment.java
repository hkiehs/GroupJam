package com.noextent.groupjam.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.callbacks.GroupInterface;

import java.util.List;

public class ChannelDialogFragment extends DialogFragment {
    private static final String LOG_TAG = "ChannelDialogFragment";

    private MusicPlayerApplication application;
    private GroupInterface groupInterface;

    public ChannelDialogFragment(MusicPlayerApplication musicPlayerApplication, GroupInterface groupInterface) {
        this.application = musicPlayerApplication;
        this.groupInterface = groupInterface;
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
                // leave any already joined channel
                application.useLeaveChannel();
                application.useSetChannelName("No group selected");

                Log.i(LOG_TAG, "Selected group [" + channels.get(which) + "]");
                String channelName = channels.get(which);
                application.useSetChannelName(channelName);
                application.useJoinChannel();

                int lastDot = channelName.lastIndexOf('.');
                if (lastDot >= 0) {
                    groupInterface.onGroupSelected(channelName.substring(lastDot + 1));
                    Toast.makeText(getActivity(), channelName + " joined", Toast.LENGTH_SHORT).show();
                } else {
                    application.useLeaveChannel();
                    groupInterface.onGroupSelected("No group selected");
                }
            }
        });
        return builder.create();
    }
}