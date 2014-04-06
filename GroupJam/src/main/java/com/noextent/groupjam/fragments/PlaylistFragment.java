package com.noextent.groupjam.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.noextent.groupjam.R;
import com.noextent.groupjam.activity.PlaylistAdapter;
import com.noextent.groupjam.activity.SimpleSectionedListAdapter;

public class PlaylistFragment extends ListFragment {
    public static final String LOG_TAG = "PlaylistFragment";

    private SimpleSectionedListAdapter mAdapter;
    private PlaylistAdapter playlistAdapter;

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistAdapter = new PlaylistAdapter(getActivity());
        mAdapter = new SimpleSectionedListAdapter(getActivity(), R.layout.list_item_schedule_header, playlistAdapter);
        setListAdapter(mAdapter);

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
}