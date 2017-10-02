package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.utils.FetchTweet;
import com.codepath.apps.mysimpletweets.fragments.PostTweetFragment;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.adapters.TweetAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.utils.TweetConstants.POST_TWEET_FRAGMENT_TITLE;

public class TimeLineActivity extends AppCompatActivity implements PostTweetFragment.OnPostTweetListener,PostTweetFragment.OnComposeCloseListener {


    @BindView(R.id.recyclerview_layout)
    View recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.ivProfilePic)
    ImageView ivProfilePic;

    ArrayList<Tweet> mTweets;
    TweetAdapter mTweetAdapter;
    TwitterClient client;
    User loggedInUser;
    IncludedLayout recylerViewLayout;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener mScrollListener;
    Handler handler;
    SharedPreferences draftTweets;

    static class IncludedLayout {
        @BindView(R.id.rvTweets)
        RecyclerView rvTweets;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up notitle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loggedInUser = Parcels.unwrap(getIntent().getParcelableExtra(TweetConstants.USER_OBJ));

        ButterKnife.bind(this);

        recylerViewLayout = new IncludedLayout();

        ButterKnife.bind(recylerViewLayout, recyclerView);

        client = TwitterApp.getRestClient();
        handler = new Handler();
        draftTweets =
                PreferenceManager.getDefaultSharedPreferences(this);

        Glide.with(this).load(loggedInUser.getProfileImageUrl()).fitCenter()
                .into(ivProfilePic);

        initSwipeRefresh();

        initAdapter();

        initFloatingComposeButton();

        //initialize Recycler View
        initRecyclerView();

        //populates the timeline on first load.
        populateTimeline(FetchTweet.FIRST_LOAD, 1, 0);
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    private void initAdapter() {
        mTweets = new ArrayList<Tweet>();
        mTweetAdapter = new TweetAdapter(this, mTweets);
        mTweetAdapter.setOnTweetClickListener(tweet -> {
            loadDetailPage(tweet);
        });
    }

    private void loadDetailPage(Tweet tweet){
        //TODO load detail page
        Intent i = new Intent(getApplicationContext(), TweetDetailActivity.class);
        i.putExtra(TweetConstants.TWEET_OBJ, Parcels.wrap(tweet));
        startActivity(i);
    }

    public void initSwipeRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                long since_id = 0;
                if(mTweets.size() > 0 ){
                    since_id = mTweets.get(0).getUid();
                }
                populateTimeline(FetchTweet.REFRESH_NEW_TWEETS,since_id,0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void initFloatingComposeButton() {
        fab.setOnClickListener(view -> {
            FragmentManager fm = getSupportFragmentManager();

            String draftTweet = draftTweets.getString(TweetConstants.DRAFT_TWEET_KEY,null);
            PostTweetFragment postTweetFragment = PostTweetFragment.newInstance(Parcels.wrap(loggedInUser),draftTweet);
            postTweetFragment.show(fm, POST_TWEET_FRAGMENT_TITLE);
        });
    }

    private void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.scrollToPosition(0);
        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            public void onLoadMore(final int page, int totalItemsCount, final RecyclerView view) {
                int curSize = mTweetAdapter.getItemCount();
                if(page == 0 || curSize == 0){
                    populateTimeline(FetchTweet.FIRST_LOAD, 1, 0);
                } else {
                    long max_id = mTweets.get(mTweets.size() - 1).getUid() - 1;
                    populateTimeline(FetchTweet.SCROLL_OLD_TWEETS,1,max_id);
                }
            }
        };
        recylerViewLayout.rvTweets.addOnScrollListener(mScrollListener);
        recylerViewLayout.rvTweets.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recylerViewLayout.rvTweets.addItemDecoration(itemDecoration);
        recylerViewLayout.rvTweets.setAdapter(mTweetAdapter);
    }

    private void populateTimeline(FetchTweet type, long since_id, long max_id) {
        client.getHomeTimeline(type, since_id, max_id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                if (type.equals(FetchTweet.FIRST_LOAD)) {

                    try {
                        mTweetAdapter.clear();
                        mScrollListener.resetState();

                        List<Tweet> tweets = loadTweets(response.toString());
                        mTweets.addAll(tweets);
                        mTweetAdapter.notifyItemRangeInserted(0, mTweets.size());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(type.equals(FetchTweet.REFRESH_NEW_TWEETS)) {

                    // Remember to CLEAR OUT old items before appending in the new ones

                    mTweetAdapter.clear();
                    mScrollListener.resetState();
                    try {
                        List<Tweet> tweets = loadTweets(response.toString());
                        // ...the data has come back, add new items to your adapter...
                        mTweetAdapter.addAll(tweets);
                        // Now we call setRefreshing(false) to signal refresh has finished
                        swipeContainer.setRefreshing(false);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage(), e.getCause());
                    }

                }
                else if(type.equals(FetchTweet.SCROLL_OLD_TWEETS)) {

                    try {
                        int curSize = mTweetAdapter.getItemCount();
                        List<Tweet> tweets = loadTweets(response.toString());
                        mTweets.addAll(tweets);
                        mTweetAdapter.notifyItemRangeInserted(curSize, mTweets.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR", errorResponse.toString(), throwable);
            }
        });
    }

    private List<Tweet> loadTweets(String tweets) throws JSONException {
        JSONArray response = new JSONArray(tweets);

        List<Tweet> tweetList = new ArrayList<Tweet>();
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweetList.add(tweet);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweetList;
    }

    @Override
    public void onPostTweet(Tweet tweet) {
        mTweets.add(0,tweet);
        mTweetAdapter.notifyItemInserted(0);
        linearLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onComposeTextSave(String text) {
        //save text to shared preferences

        SharedPreferences.Editor editor = draftTweets.edit();
        editor.putString(TweetConstants.DRAFT_TWEET_KEY, text);

        editor.apply();
    }
}
