package com.noextent.groupjam.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.callbacks.GroupInterface;
import com.noextent.groupjam.utility.GifDecoder;
import com.noextent.groupjam.utility.GifDecoderView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

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
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeParentLayout);
        ViewGif viewGif = new ViewGif(relativeLayout);
        viewGif.execute();

        return view;
    }

    private class ViewGif extends AsyncTask<Void, Void, Boolean> {
        private GifDecoderView gifDecoderView;
        private RelativeLayout relativeLayout;

        public ViewGif(RelativeLayout relativeLayout) {
            this.relativeLayout = relativeLayout;
        }

        protected void onPreExecute() {
            gifDecoderView = new GifDecoderView(getActivity());
        }

        protected Boolean doInBackground(Void... params) {
            final int low = 1; // inclusive
            final int high = 11; // exclusive
            Random r = new Random();
            final int number = r.nextInt(high - low) + low;

            InputStream inputStream = null;
            try {
                inputStream = getActivity().getAssets().open("gif/Preloader_" + number + ".gif");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (inputStream != null) {
                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (200 * scale + 0.5f);

                GifDecoder mGifDecoder = new GifDecoder();
                mGifDecoder.read(inputStream);
                gifDecoderView.playGif(mGifDecoder);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                gifDecoderView.setPadding(0, 0, 0, dpAsPixels);
                gifDecoderView.setLayoutParams(layoutParams);
                return true;
            }
            return false;
        }

        protected void onPostExecute(Boolean status) {
            if (status)
                relativeLayout.addView(gifDecoderView);
        }
    }
}