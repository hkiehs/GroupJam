package com.noextent.groupjam.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.noextent.groupjam.R;
import com.noextent.groupjam.activity.SimpleSectionedListAdapter;

public class PlaylistFragment extends ListFragment {
    public static final String LOG_TAG = "PlaylistFragment";

    private SimpleSectionedListAdapter mAdapter;
    private MyScheduleAdapter mScheduleAdapter;
    private boolean mScrollToNow;


    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleAdapter = new MyScheduleAdapter(getActivity());
        mAdapter = new SimpleSectionedListAdapter(getActivity(), R.layout.list_item_schedule_header, mScheduleAdapter);
        setListAdapter(mAdapter);

        if (savedInstanceState == null) {
            mScrollToNow = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(
                R.layout.fragment_list_with_empty_container, container, false);
        inflater.inflate(R.layout.empty_waiting_for_sync,
                (ViewGroup) root.findViewById(android.R.id.empty), true);
        root.setBackgroundColor(Color.WHITE);
        ListView listView = (ListView) root.findViewById(android.R.id.list);
        listView.setItemsCanFocus(true);
        listView.setCacheColorHint(Color.WHITE);
        listView.setSelector(android.R.color.transparent);
        return root;
    }

    private class MyScheduleAdapter extends CursorAdapter {
        public MyScheduleAdapter(Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_song_block,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {

        }
    }
}