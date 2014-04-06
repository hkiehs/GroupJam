package com.noextent.groupjam.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.model.ParseMedia;
import com.noextent.groupjam.utility.Utility;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;
import java.util.Locale;

public class PlaylistAdapter extends ParseQueryAdapter<ParseMedia> {
	private static final String LOG_TAG = "PlaylistAdapter";
    private MusicPlayerApplication application;
	private Context context;

	public PlaylistAdapter(Context context, MusicPlayerApplication application) {
		super(context, new QueryFactory<ParseMedia>() {
			public ParseQuery<ParseMedia> create() {
				ParseQuery<ParseMedia> query = ParseQuery.getQuery(ParseMedia.TABLE);
				query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
				query.orderByDescending(ParseMedia.CREATED_AT);
				return query;
			}
		});
		this.context = context;
        this.application = application;
	}

	@Override
	public View getItemView(final ParseMedia mParseMedia, View view, ViewGroup parent) {
		super.getItemView(mParseMedia, view, parent);
        final ViewHolder viewHolder;
		if (view == null) {
			view = View.inflate(getContext(), R.layout.list_item_song_block, null);

            viewHolder = new ViewHolder();
            viewHolder.songName = (TextView) view.findViewById(R.id.block_title);
            viewHolder.artistName = (TextView) view.findViewById(R.id.block_subtitle);
            viewHolder.playSong = (ImageButton) view.findViewById(R.id.extra_button);

            view.setTag(viewHolder);
		} else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.songName.setText(mParseMedia.getSongName());
        viewHolder.artistName.setText(mParseMedia.getArtistName().toUpperCase(Locale.ENGLISH));

        viewHolder.playSong.setTag(mParseMedia);
        viewHolder.playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseMedia parseMedia = (ParseMedia) viewHolder.playSong.getTag();
                Toast.makeText(context, "requesting " + parseMedia.getSongName() + " download", Toast.LENGTH_SHORT).show();
                new DownloadSong(parseMedia, viewHolder.playSong).execute();
            }
        });

		return view;
	}

    static class ViewHolder {
        TextView songName;
        TextView artistName;
        ImageButton playSong;
    }

    private byte[] download(ParseMedia parseMedia) {
        byte[] data = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseMedia.TABLE);
        query.whereEqualTo(Utility.OBJECT_ID, parseMedia.getObjectId());
        try {
            List<ParseObject> parseObjects = query.find();
            if (parseObjects != null && parseObjects.size() > 0) {
                ParseFile media = (ParseFile) parseObjects.get(0).get(ParseMedia.MEDIA_FILE);
                data = media.getData();
                if (data != null) {
                    return data;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class DownloadSong extends AsyncTask<Void, Void, byte[]> {
        private ParseMedia parseMedia;
        private ImageButton imageButton;
        public DownloadSong(ParseMedia parseMedia, ImageButton imageButton) {
            this.parseMedia = parseMedia;
            this.imageButton = imageButton;
        }
        protected byte[] doInBackground(Void... params) {
            byte[] data = null;
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseMedia.TABLE);
            query.whereEqualTo(Utility.OBJECT_ID, parseMedia.getObjectId());
            try {
                List<ParseObject> parseObjects = query.find();
                if (parseObjects != null && parseObjects.size() > 0) {
                    ParseFile media = (ParseFile) parseObjects.get(0).get(ParseMedia.MEDIA_FILE);
                    data = media.getData();
                    if (data != null) {
                        return data;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {}

        protected void onPostExecute(byte[] data) {
            Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();
            imageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_add_to_queue));
            application.mMediaPlayer = Utility.prepareMediaPlayer(context, application.mMediaPlayer, data);
        }
    }

}