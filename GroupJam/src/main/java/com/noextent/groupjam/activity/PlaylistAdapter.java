package com.noextent.groupjam.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noextent.groupjam.R;
import com.noextent.groupjam.model.ParseMedia;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Locale;

public class PlaylistAdapter extends ParseQueryAdapter<ParseMedia> {
	private static final String LOG_TAG = "PlaylistAdapter";
	private Context context;

	public PlaylistAdapter(Context context) {
		super(context, new QueryFactory<ParseMedia>() {
			public ParseQuery<ParseMedia> create() {
				ParseQuery<ParseMedia> query = ParseQuery.getQuery(ParseMedia.TABLE);
				query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
				query.orderByDescending(ParseMedia.CREATED_AT);
				return query;
			}
		});
		this.context = context;
	}

	@Override
	public View getItemView(final ParseMedia mParseMedia, View view, ViewGroup parent) {
		super.getItemView(mParseMedia, view, parent);
        ViewHolder viewHolder;
		if (view == null) {
			view = View.inflate(getContext(), R.layout.list_item_song_block, null);

            viewHolder = new ViewHolder();
            viewHolder.songName = (TextView) view.findViewById(R.id.block_title);
            viewHolder.artistName = (TextView) view.findViewById(R.id.block_subtitle);

            view.setTag(viewHolder);
		} else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.songName.setText(mParseMedia.getSongName());
        viewHolder.artistName.setText(mParseMedia.getArtistName().toUpperCase(Locale.ENGLISH));
		return view;
	}

    static class ViewHolder {
        TextView songName;
        TextView artistName;
    }
}