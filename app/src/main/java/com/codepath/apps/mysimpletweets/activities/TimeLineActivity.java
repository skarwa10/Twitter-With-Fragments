package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.adapters.TweetAdapter;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.SmartFragmentStatePagerAdapter;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsPagerAdapter;
import com.codepath.apps.mysimpletweets.utils.FetchTweet;
import com.codepath.apps.mysimpletweets.fragments.PostTweetFragment;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.codepath.apps.mysimpletweets.views.CircleTransform;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.parceler.Parcels;

import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static android.support.v4.view.MenuItemCompat.getActionView;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.POST_TWEET_FRAGMENT_TITLE;

public class TimeLineActivity extends AppCompatActivity implements PostTweetFragment.OnPostTweetListener,
        PostTweetFragment.OnComposeCloseListener , TweetsListFragment.OnTweetSelectedListener ,
        TweetsListFragment.OnProfileImageSelectedListener {

    @BindView(R.id.tablayout)
    View tabView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.ivProfilePic)
    ImageView ivProfilePic;

    User loggedInUser;
    Menu menu;
    MenuItem miActionProgressItem;
    SharedPreferences draftTweets;
    IncludedLayout includedTabLayout;
    SearchView mSearchView;
    SmartFragmentStatePagerAdapter adapterViewPager;

    static class IncludedLayout {
        @BindView(R.id.sliding_tabs)
        TabLayout tabLayout;

        @BindView(R.id.viewpager)
        ViewPager vpPager;

        @BindView(R.id.toolbar)
        Toolbar toolbar;

        @BindView(R.id.ivProfilePic)
        ImageView profilePic;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_time_line);

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(TweetConstants.USER_OBJ));

        ButterKnife.bind(this);

        includedTabLayout = new IncludedLayout();

        ButterKnife.bind(includedTabLayout, tabView);


        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
        includedTabLayout.vpPager.setAdapter(adapterViewPager);
        includedTabLayout.tabLayout.setupWithViewPager(includedTabLayout.vpPager);
        includedTabLayout.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileView(loggedInUser);
            }
        });

        setSupportActionBar(includedTabLayout.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        draftTweets =
                PreferenceManager.getDefaultSharedPreferences(this);

        Glide.with(this).load(loggedInUser.getProfileImageUrl())
                .transform(new CircleTransform(this))
                .into(ivProfilePic);

        initFloatingComposeButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) getActionView(searchItem);
        mSearchView.setOnQueryTextListener(createQueryTextListener());
        mSearchView.setOnCloseListener(createOnCloseListener());
        this.menu = menu;

      //  searchItem.expandActionView();
      //  mSearchView.requestFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Store instance of the menu item containing progress
        ProgressBar v = (ProgressBar) getActionView(miActionProgressItem);
        // Return to finish

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void loadDetailPage(Tweet tweet){
        Intent i = new Intent(getApplicationContext(), TweetDetailActivity.class);
        i.putExtra(TweetConstants.TWEET_OBJ, Parcels.wrap(tweet));
        startActivity(i);
    }

    public SearchView.OnQueryTextListener createQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                showProgressBar();

                //clear Adapter
               /* mArticleAdapter.clear();
                // 3. Reset endless scroll listener when performing a new search
                mScrollListener.resetState();

                queryString = query;

                fetchArticles(0);*/

                hideProgressBar();
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    public SearchView.OnCloseListener createOnCloseListener() {
        return () -> {
            Log.i("SearchView:", "onClose");
            mSearchView.onActionViewCollapsed();
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            return false;
        };
    }



    @Override
    public void onProfileView(User user){
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra(TweetConstants.USER_OBJ, Parcels.wrap(user));
        startActivity(i);
    }


    private void initFloatingComposeButton() {
        fab.setOnClickListener(view -> {
            FragmentManager fm = getSupportFragmentManager();

            String draftTweet = draftTweets.getString(TweetConstants.DRAFT_TWEET_KEY,null);
            PostTweetFragment postTweetFragment = PostTweetFragment.newInstance(Parcels.wrap(loggedInUser),draftTweet);
            postTweetFragment.show(fm, POST_TWEET_FRAGMENT_TITLE);
        });
    }

    @Override
    public void onPostTweet(Tweet tweet) {
        HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
        homeTimelineFragment.addNewTweet(tweet);
        includedTabLayout.vpPager.setCurrentItem(0);
    }

    @Override
    public void onComposeTextSave(String text) {
        //save text to shared preferences

        SharedPreferences.Editor editor = draftTweets.edit();
        editor.putString(TweetConstants.DRAFT_TWEET_KEY, text);
        editor.apply();
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }
}
