package com.noextent.groupjam.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.utility.GifDecoderView;
import com.noextent.groupjam.callbacks.GroupInterface;

import java.io.IOException;
import java.io.InputStream;

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
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);

        InputStream stream = null;
        try {
            stream = getActivity().getAssets().open("preloader.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GifDecoderView gifDecoderView = new GifDecoderView(getActivity(), stream);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeParentLayout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (200 * scale + 0.5f);
        gifDecoderView.setPadding(0, 0, 0, dpAsPixels);
        gifDecoderView.setLayoutParams(layoutParams);
        relativeLayout.addView(gifDecoderView);

        return view;
    }
}