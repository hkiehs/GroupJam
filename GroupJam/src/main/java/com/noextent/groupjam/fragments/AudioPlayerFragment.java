package com.noextent.groupjam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.callbacks.GroupInterface;

public class AudioPlayerFragment extends Fragment {
    public static final String LOG_TAG = "MediaPlayerFragment";

    private MusicPlayerApplication mChatApplication;
    private GroupInterface groupInterface;

    public AudioPlayerFragment(MusicPlayerApplication mChatApplication, GroupInterface groupInterface) {
        this.mChatApplication = mChatApplication;
        this.groupInterface = groupInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio_player, container,  false);
        return rootView;
    }
}