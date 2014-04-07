package com.noextent.groupjam.activity;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.fragments.AudioPlayerFragment;
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
	private FragmentActivity fragmentActivity;

	public PlaylistAdapter(FragmentActivity fragmentActivity, MusicPlayerApplication application) {
		super(fragmentActivity, new QueryFactory<ParseMedia>() {
			public ParseQuery<ParseMedia> create() {
				ParseQuery<ParseMedia> query = ParseQuery.getQuery(ParseMedia.TABLE);
				query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
				query.orderByDescending(ParseMedia.CREATED_AT);
				return query;
			}
		});
		this.fragmentActivity = fragmentActivity;
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
                Toast.makeText(fragmentActivity, "requesting " + parseMedia.getSongName() + " download", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(fragmentActivity, "Download complete", Toast.LENGTH_SHORT).show();
            imageButton.setImageDrawable(fragmentActivity.getResources().getDrawable(R.drawable.ic_action_play));
            application.mMediaPlayer = Utility.prepareMediaPlayer(fragmentActivity, application.mMediaPlayer, data);


            AudioPlayerFragment audioPlayerFragment = new AudioPlayerFragment(application, parseMedia);
            FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, audioPlayerFragment);
            // transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}