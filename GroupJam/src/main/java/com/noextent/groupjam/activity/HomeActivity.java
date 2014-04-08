package com.noextent.groupjam.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.noextent.groupjam.MusicPlayerApplication;
import com.noextent.groupjam.R;
import com.noextent.groupjam.callbacks.GroupInterface;
import com.noextent.groupjam.callbacks.Observable;
import com.noextent.groupjam.callbacks.Observer;
import com.noextent.groupjam.callbacks.RegisterInterface;
import com.noextent.groupjam.fragments.AudioPlayerFragment;
import com.noextent.groupjam.fragments.ChannelDialogFragment;
import com.noextent.groupjam.fragments.NavigationDrawerFragment;
import com.noextent.groupjam.fragments.PlaylistFragment;
import com.noextent.groupjam.model.Device;
import com.noextent.groupjam.service.AllJoynService;
import com.noextent.groupjam.utility.Utility;

import java.util.Locale;

public class HomeActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, NavigationDrawerFragment.NavigationDrawerCallbacks, Observer, GroupInterface, RegisterInterface {

    private static final String TAG = "HomeActivity";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateHostActivity();

        setContentView(R.layout.activity_home);

        MusicPlayerApplication.mContext = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Utility.registerDevice(HomeActivity.this, "Device_Galaxy_Nexus", HomeActivity.this);
    }

    private void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_actionbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

    private SearchView mSearchView;
    private MenuItem searchMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_add_group);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_GO);
        mSearchView.setQueryHint("Group name");
        mSearchView.setOnQueryTextListener(this);
        showActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_add_group) {
            mSearchView.setIconified(false);
            searchMenuItem = menuItem;
            return true;
        }

        if (id == R.id.action_leave_group) {
            // Toast.makeText(HomeActivity.this, "All groups removed", Toast.LENGTH_SHORT).show();
            // getSupportActionBar().setSubtitle("No group selected");
            // mChatApplication.useLeaveChannel();

            ChannelDialogFragment channelDialogFragment = new ChannelDialogFragment(mChatApplication, HomeActivity.this);
            channelDialogFragment.show(getSupportFragmentManager(), "dialog");

            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        if (text.length() > 0) {
            Toast.makeText(this, "Channel created :: " + text, Toast.LENGTH_LONG).show();
            // stop any channel running
            mChatApplication.hostStopChannel();

            // set group name
            mChatApplication.hostSetChannelName(text);
            mChatApplication.hostInitChannel();
            // start group
            mChatApplication.hostStartChannel();

            if (searchMenuItem != null) {
                MenuItemCompat.collapseActionView(searchMenuItem);
                searchMenuItem = null;
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onGroupSelected(String group) {
        if (group != null)
            getSupportActionBar().setSubtitle(group);
    }

    @Override
    public void onRegistrationComplete(Device device) {
        if (device != null) {
            Toast.makeText(HomeActivity.this, "Registration completed", Toast.LENGTH_SHORT).show();
            mChatApplication.mDevice = device;
            // register for parse push
            Utility.registerParseInstallation(device);

            //addMessageToList("Registration successful");
            // upload file to server
            // INFO: now user should be able to host and retrieve songs from
            // server send download instruction on the group and download it
            // locally

            // Utility.downloadMusic(device, Utility.songObjectId, this);
            //addMessageToList("receiving song... from server");
        } else {
            Toast.makeText(HomeActivity.this, "Registration UN-SUCCESSFUL", Toast.LENGTH_SHORT).show();
            // addMessageToList("Registration un-successful");
            // Registration failed
            // show a retry button on the UI to retry registration
            // User can not proceed further without this step only if he has
            // something to from/to server. Else all functionalities should be
            // accessible.
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                AudioPlayerFragment audioPlayerFragment = new AudioPlayerFragment(mChatApplication, HomeActivity.this);
                return audioPlayerFragment;
            } else {
                PlaylistFragment playlistFragment = new PlaylistFragment(mChatApplication);
                return playlistFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_media_player);
                case 1:
                    return getString(R.string.title_playlist);
            }
            return null;
        }
    }

    /**
     * Host Activity Integration
     */

    public static MusicPlayerApplication mChatApplication = null;

    static final int DIALOG_SET_NAME_ID = 0;
    static final int DIALOG_START_ID = 1;
    static final int DIALOG_STOP_ID = 2;
    public static final int DIALOG_ALLJOYN_ERROR_ID = 3;


    private void onCreateHostActivity() {
        /*
         * Keep a pointer to the Android Appliation class around.  We use this
         * as the Model for our MVC-based application.  Whenever we are started
         * we need to "check in" with the application so it can ensure that our
         * required services are running.
         */
        mChatApplication = (MusicPlayerApplication) getApplication();
        mChatApplication.checkin();

        /*
         * Call down into the model to get its current state.  Since the model
         * outlives its Activities, this may actually be a lot of state and not
         * just empty.
         */
        updateChannelState();

        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        mChatApplication.addObserver(this);
    }

    public void onDestroy() {
        mChatApplication = (MusicPlayerApplication) getApplication();
        mChatApplication.deleteObserver(this);
        super.onDestroy();
    }

    public synchronized void update(Observable o, Object arg) {
        Log.i(TAG, "update(" + arg + ")");
        String qualifier = (String) arg;

        if (qualifier.equals(MusicPlayerApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(MusicPlayerApplication.HOST_CHANNEL_STATE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(MusicPlayerApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }

    private void updateChannelState() {
        AllJoynService.HostChannelState channelState = mChatApplication.hostGetChannelState();
        String name = mChatApplication.hostGetChannelName();
        boolean haveName = true;
        if (name == null) {
            haveName = false;
            name = "Not set";
        }
        //mChannelName.setText(name);
        switch (channelState) {
            case IDLE:
//            mChannelStatus.setText("Idle");
                break;
            case NAMED:
//            mChannelStatus.setText("Named");
                break;
            case BOUND:
//            mChannelStatus.setText("Bound");
                break;
            case ADVERTISED:
//            mChannelStatus.setText("Advertised");
                break;
            case CONNECTED:
//            mChannelStatus.setText("Connected");
                break;
            default:
//            mChannelStatus.setText("Unknown");
                break;
        }

//        if (channelState == AllJoynService.HostChannelState.IDLE) {
//            mSetNameButton.setEnabled(true);
//            if (haveName) {
//            	mStartButton.setEnabled(true);
//            } else {
//                mStartButton.setEnabled(false);
//            }
//            mStopButton.setEnabled(false);
//        } else {
//            mSetNameButton.setEnabled(false);
//            mStartButton.setEnabled(false);
//            mStopButton.setEnabled(true);
//        }
    }

    private void alljoynError() {
        if (mChatApplication.getErrorModule() == MusicPlayerApplication.Module.GENERAL ||
                mChatApplication.getErrorModule() == MusicPlayerApplication.Module.USE) {
            showDialog(DIALOG_ALLJOYN_ERROR_ID);
        }
    }

    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 1;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_APPLICATION_QUIT_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
                    finish();
                }
                break;
                case HANDLE_CHANNEL_STATE_CHANGED_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
                    updateChannelState();
                }
                break;
                case HANDLE_ALLJOYN_ERROR_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
                    alljoynError();
                }
                break;
                default:
                    break;
            }
        }
    };

    public void MoveNext() {
        //it doesn't matter if you're already in the last item
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    public void MovePrevious() {
        //it doesn't matter if you're already in the first item
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

}
