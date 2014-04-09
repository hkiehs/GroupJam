package com.noextent.groupjam.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.callbacks.GroupInterface;
import com.noextent.groupjam.callbacks.MediaReceiver;
import com.noextent.groupjam.model.MediaModel;
import com.noextent.groupjam.model.ParseMedia;
import com.noextent.groupjam.utility.GifDecoder;
import com.noextent.groupjam.utility.GifDecoderView;
import com.noextent.groupjam.utility.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AudioPlayerFragment extends Fragment implements MediaReceiver {
    public static final String LOG_TAG = "AudioPlayerFragment";

    private MusicPlayerApplication mChatApplication = null;
    private GroupInterface groupInterface = null;

    public AudioPlayerFragment(MusicPlayerApplication mChatApplication, GroupInterface groupInterface) {
        this.mChatApplication = mChatApplication;
        this.groupInterface = groupInterface;
    }

    private ImageButton imgBtnPlay = null;
    private TextView tvSongName = null;
    private TextView tvArtistAlbumName = null;

    @Override
    public void onMediaReceived(ParseMedia parseMedia) {
        refreshView(parseMedia);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeParentLayout);
        ViewGif viewGif = new ViewGif(relativeLayout);
        viewGif.execute();

        mChatApplication.mMediaReceiver = this;

        imgBtnPlay = (ImageButton) view.findViewById(R.id.imageButtonPlay);
        imgBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamMedia();
            }
        });

        tvSongName = (TextView) view.findViewById(R.id.textViewSongName);
        tvArtistAlbumName = (TextView) view.findViewById(R.id.textViewAlbumName);

        return view;
    }

    private void refreshView(ParseMedia parseMedia) {
        if (parseMedia != null) {
            tvSongName.setText(parseMedia.getSongName());
            tvArtistAlbumName.setText(parseMedia.getArtistName() + " - " + parseMedia.getAlbumName());
        }
    }

    private void streamMedia() {
        if (mChatApplication.mMediaPlayer != null) {
            int position = mChatApplication.mMediaPlayer.getCurrentPosition();

            MediaModel mMediaModel = new MediaModel();
            mMediaModel.playBackPosition = position;

            if (mChatApplication.mMediaPlayer.isPlaying()) {
                mMediaModel.action = Utility.STATUS_PAUSE;
                mChatApplication.newLocalUserMessage(mMediaModel.toJson());
                imgBtnPlay.setBackgroundResource(R.drawable.ic_action_play);
            } else {
                mMediaModel.action = Utility.STATUS_PLAY;
                mChatApplication.newLocalUserMessage(mMediaModel.toJson());
                imgBtnPlay.setBackgroundResource(R.drawable.ic_action_pause);
            }

            final MediaModel mediaModel = mMediaModel;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mChatApplication.mMediaPlayer.isPlaying()) {
                        Utility.pauseMedia(mChatApplication.mMediaPlayer, mediaModel.playBackPosition);
                    } else {
                        Utility.playMedia(mChatApplication.mMediaPlayer, mediaModel.playBackPosition);
                    }
                }
            }, 300);
        } else {
            Toast.makeText(getActivity(), "Please select a song from the playlist", Toast.LENGTH_SHORT).show();
        }
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