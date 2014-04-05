package com.noextent.groupjam.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.noextent.groupjam.MediaModel;
import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.Utility;
import com.noextent.groupjam.callbacks.GroupInterface;

public class MediaPlayerFragment extends Fragment {
    public static final String LOG_TAG = "MediaPlayerFragment";

    private MusicPlayerApplication mChatApplication;
    private GroupInterface groupInterface;

    private Button mJoinButton;
    private Button mLeaveButton;
    private Button mActionButton;

    public MediaPlayerFragment(MusicPlayerApplication mChatApplication, GroupInterface groupInterface) {
        this.mChatApplication = mChatApplication;
        this.groupInterface = groupInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);
        //TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        //dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

        mJoinButton = (Button) rootView.findViewById(R.id.useJoin);
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(LOG_TAG, "Join Button pressed");
                ChannelDialogFragment channelDialogFragment = new ChannelDialogFragment(mChatApplication, groupInterface);
                channelDialogFragment.show(getFragmentManager(), "dialog");

            }
        });

        mLeaveButton = (Button) rootView.findViewById(R.id.useLeave);
        mLeaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().showDialog(DIALOG_LEAVE_ID);
            }
        });

        mActionButton = (Button) rootView.findViewById(R.id.btAction);

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mediaState = mActionButton.getText().toString();
                int position = mChatApplication.mMediaPlayer.getCurrentPosition();

                MediaModel mMediaModel = new MediaModel();
                mMediaModel.playBackPosition = position;

                if (mediaState.equalsIgnoreCase(Utility.STATUS_PLAY)) {
                    mMediaModel.action = Utility.STATUS_PLAY;
                    mChatApplication.newLocalUserMessage(mMediaModel.toJson());
                    mActionButton.setText("Pause");
                } else {
                    mMediaModel.action = Utility.STATUS_PAUSE;
                    mChatApplication.newLocalUserMessage(mMediaModel.toJson());
                    mActionButton.setText("Play");
                }

                final MediaModel mediaModel = mMediaModel;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaModel.action.equalsIgnoreCase(Utility.STATUS_PLAY)) {
                            Utility.playMedia(mChatApplication.mMediaPlayer, mediaModel.playBackPosition);
                        } else if (mediaModel.action.equalsIgnoreCase(Utility.STATUS_PAUSE)) {
                            Utility.pauseMedia(mChatApplication.mMediaPlayer, mediaModel.playBackPosition);
                        }
                    }
                }, 300);
            }
        });


        return rootView;
    }

    public static final int DIALOG_JOIN_ID = 0;
    public static final int DIALOG_LEAVE_ID = 1;
    public static final int DIALOG_ALLJOYN_ERROR_ID = 2;

//    protected Dialog onCreateDialog(int id) {
//        Log.i(LOG_TAG, "onCreateDialog()");
//        Dialog result = null;
//        switch (id) {
//            case DIALOG_JOIN_ID : {
//                DialogBuilder builder = new DialogBuilder();
//                result = builder.createUseJoinDialog(getActivity(), mChatApplication);
//            }
//            break;
//            case DIALOG_LEAVE_ID : {
//                DialogBuilder builder = new DialogBuilder();
//                result = builder.createUseLeaveDialog(getActivity(), mChatApplication);
//            }
//            break;
//            case DIALOG_ALLJOYN_ERROR_ID : {
//                DialogBuilder builder = new DialogBuilder();
//                result = builder.createAllJoynErrorDialog(getActivity(), mChatApplication);
//            }
//            break;
//        }
//        return result;
//    }
}