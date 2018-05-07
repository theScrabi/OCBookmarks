package org.schabi.ocbookmarks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.schabi.ocbookmarks.REST.Bookmark;
import org.schabi.ocbookmarks.REST.OCBookmarksRestConnector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Toolbar mToolbar;

    private static final String BOOKMARK_FRAGMENT = "bookmark_fragment";
    private BookmarkFragment mBookmarkFragment = null;
    private static final String TAGS_FRAGMENT = "tags_fragment";
    private TagsFragment mTagsFragment = null;
    private ProgressBar mainProgressBar;

    private SharedPreferences sharedPreferences;
    private static LoginData loginData;

    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //setup sliding tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditBookmarkDialog bookmarkDialog = new EditBookmarkDialog();
                AlertDialog dialog = bookmarkDialog.getDialog(MainActivity.this, null, new EditBookmarkDialog.OnBookmarkChangedListener() {
                    @Override
                    public void bookmarkChanged(Bookmark bookmark) {
                        addEditBookmark(bookmark);
                    }
                });
                dialog.show();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != 1) {
                    mBookmarkFragment.releaseTag();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);


        if(savedInstanceState == null) {
            mBookmarkFragment = new BookmarkFragment();
            setupBookmarkFragmentListener();
            mTagsFragment = new TagsFragment();
            setupTagFragmentListener();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        FragmentManager fm = getSupportFragmentManager();
        mBookmarkFragment = (BookmarkFragment) fm.getFragment(inState, BOOKMARK_FRAGMENT);
        mTagsFragment = (TagsFragment) fm.getFragment(inState, TAGS_FRAGMENT);
        setupBookmarkFragmentListener();
        setupTagFragmentListener();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fm = getSupportFragmentManager();
        fm.putFragment(outState, BOOKMARK_FRAGMENT, mBookmarkFragment);
        fm.putFragment(outState, TAGS_FRAGMENT, mTagsFragment);
    }

    private void setupBookmarkFragmentListener() {
        mBookmarkFragment.setOnRequestReloadListener(new BookmarkFragment.OnRequestReloadListener() {
            @Override
            public void requestReload() {
                reloadData();
            }
        });

        mBookmarkFragment.setOnBookmarkChangedListener(new EditBookmarkDialog.OnBookmarkChangedListener() {
            @Override
            public void bookmarkChanged(Bookmark bookmark) {
                addEditBookmark(bookmark);
            }
        });

        mBookmarkFragment.setOnBookmarkDeleteListener(new BookmarkFragment.OnBookmarkDeleteListener() {
            @Override
            public void deleteBookmark(final Bookmark bookmark) {
                setRefreshing(true);
                AsyncTask<Void, Void, String> updateTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        OCBookmarksRestConnector connector = new OCBookmarksRestConnector(
                                loginData.url,
                                loginData.user,
                                loginData.password);
                        try {
                            connector.deleteBookmark(bookmark);
                        } catch (Exception e) {
                            return getString(R.string.could_not_delete_bookmark);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if(result != null) {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG);
                        }
                        reloadData();
                    }
                }.execute();
            }
        });
    }

    private void addEditBookmark(final Bookmark bookmark) {
        setRefreshing(true);
        AsyncTask<Void, Void, String> updateTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                OCBookmarksRestConnector connector = new OCBookmarksRestConnector(
                        loginData.url,
                        loginData.user,
                        loginData.password);
                if(bookmark.getId() < 0) {
                    // add new bookmark
                    try {
                        connector.addBookmark(bookmark);
                    } catch (Exception e) {
                        if(BuildConfig.DEBUG) e.printStackTrace();
                        return getString(R.string.could_not_add_bookmark);
                    }
                } else {
                    try {
                        connector.editBookmark(bookmark);
                    } catch (Exception e) {
                        if(BuildConfig.DEBUG) e.printStackTrace();
                        return getString(R.string.could_not_change_bookmark);
                    }
                }
                return null;
            }

            @Override
            protected  void onPostExecute(String result) {
                if(result != null) {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                }
                reloadData();
            }
        }.execute();
    }

    private void setupTagFragmentListener() {
        mTagsFragment.setOnTagTapedListener(new TagsFragment.OnTagTapedListener() {
            @Override
            public void onTagTaped(String tag) {
                mBookmarkFragment.showByTag(tag);
                mViewPager.setCurrentItem(1);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        mTagsFragment.setOnRequestReloadListener(new TagsFragment.OnRequestReloadListener() {
            @Override
            public void requestReload() {
                reloadData();
            }
        });

        mTagsFragment.setOnTagEditedListener(new TagsRecyclerViewAdapter.OnTagEditedListener() {
            @Override
            public void onTagEdited(final String oldTag, final String newTag) {
                setRefreshing(true);
                AsyncTask<Void, Void, String> updateTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        OCBookmarksRestConnector connector =
                                new OCBookmarksRestConnector(
                                        loginData.url,
                                        loginData.user,
                                        loginData.password);
                        try {
                            connector.renameTag(oldTag, newTag);
                        } catch (Exception e) {
                            return getString(R.string.could_not_update_tag);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if(result != null) {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                        }
                        reloadData();
                    }
                };
                updateTask.execute();
            }
        });

        mTagsFragment.setOnTagDeletedListener(new TagsRecyclerViewAdapter.OnTagDeletedListener() {
            @Override
            public void onTagDeleted(final String tag) {
                setRefreshing(true);
                AsyncTask<Void, Void, String> updateTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        OCBookmarksRestConnector connector = new OCBookmarksRestConnector(
                                loginData.url,
                                loginData.user,
                                loginData.password);
                        try {
                            connector.deleteTag(tag);
                        } catch (Exception e) {
                            return getString(R.string.could_not_delete_tag);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if(result != null) {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG);
                        }
                        reloadData();
                    }
                };
                updateTask.execute();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //todo: only reload if no data is stored so fare
        // start login activity when nececary:
        sharedPreferences =
                getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        loginData = new LoginData();
        loginData.url = sharedPreferences.getString(getString(R.string.login_url), "");
        loginData.user = sharedPreferences.getString(getString(R.string.login_user), "");
        loginData.password = sharedPreferences.getString(getString(R.string.login_pwd), "");
        if(loginData.url.isEmpty()) {
            Intent intent = new Intent(this, LoginAcitivty.class);
            startActivity(intent);
        } else {
            reloadData();
            loadFromFile();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_change_login:
                Intent intent = new Intent(this, LoginAcitivty.class);
                startActivity(intent);
                return true;
            case R.id.action_reload_icons:
                IconHandler iconHandler = new IconHandler(MainActivity.this);
                iconHandler.deleteAll();
                reloadData();
            case android.R.id.home:
                mBookmarkFragment.releaseTag();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                mViewPager.setCurrentItem(0);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return mTagsFragment;
                case 1:
                    return mBookmarkFragment;
                default:
                    Log.e(TAG, "Fragment not found");
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tags);
                case 1:
                    return getString(R.string.bookmarks);
            }
            return null;
        }
    }

    private void reloadData() {
        RelodDataTask relodDataTask = new RelodDataTask();
        relodDataTask.execute();
    }

    private void setRefreshing(boolean refresh) {
        mBookmarkFragment.setRefreshing(refresh);
        mTagsFragment.setRefreshing(refresh);
    }

    private class RelodDataTask extends AsyncTask<Void, Void, Bookmark[]> {
        protected Bookmark[] doInBackground(Void... bla) {
            try {
                OCBookmarksRestConnector connector =
                        new OCBookmarksRestConnector(loginData.url, loginData.user, loginData.password);
                JSONArray data = connector.getRawBookmarks();
                storeToFile(data);
                return connector.getFromRawJson(data);
            } catch (Exception e) {
                if(BuildConfig.DEBUG) e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bookmark[] bookmarks) {
            if(bookmarks == null) {
                Toast.makeText(MainActivity.this, R.string.connectino_failed, Toast.LENGTH_SHORT)
                        .show();
            } else {
                mainProgressBar.setVisibility(View.GONE);
                mTagsFragment.updateData(Bookmark.getTagsFromBookmarks(bookmarks));
                mBookmarkFragment.updateData(bookmarks);
                setRefreshing(false);
            }
        }
    }

    private void loadFromFile() {
        File jsonFile = new File(getFilesDir() + "/data.json");
        StringBuilder text = new StringBuilder();
        if(jsonFile.exists()) {
            mainProgressBar.setVisibility(View.GONE);
            try {
                BufferedReader br = new BufferedReader(new FileReader(jsonFile));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append("\n");
                }
                br.close();
                OCBookmarksRestConnector connector =
                        new OCBookmarksRestConnector(loginData.url,
                                loginData.user,
                                loginData.password);
                Bookmark[] bookmarks = connector.getFromRawJson(new JSONArray(text.toString()));
                mTagsFragment.updateData(Bookmark.getTagsFromBookmarks(bookmarks));
                mBookmarkFragment.updateData(bookmarks);
            } catch (JSONException je) {
                if(BuildConfig.DEBUG) je.printStackTrace();
            } catch (Exception e) {
                if(BuildConfig.DEBUG) e.printStackTrace();
            }
        }
    }

    private void storeToFile(JSONArray data) {
        try {
            FileOutputStream jsonFile = new FileOutputStream(getFilesDir() + "/data.json", false);
            jsonFile.write(data.toString().getBytes());
            jsonFile.flush();
            jsonFile.close();
        } catch (Exception e) {
            if(BuildConfig.DEBUG) e.printStackTrace();
        }
    }
}
