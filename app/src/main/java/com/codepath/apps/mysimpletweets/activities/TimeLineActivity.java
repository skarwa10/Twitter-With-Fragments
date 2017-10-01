package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.mysimpletweets.fragments.PostTweetFragment;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.adapters.TweetAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class TimeLineActivity extends AppCompatActivity implements PostTweetFragment.OnPostTweetListener {
    public static final String POST_TWEET_FRAGMENT_TITLE = "Post Tweet";

    @BindView(R.id.recyclerview_layout)
    View recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    ArrayList<Tweet> mTweets;
    TweetAdapter mTweetAdapter;
    TwitterClient client;
    User loggedInUser;
    IncludedLayout recylerViewLayout;
    LinearLayoutManager linearLayoutManager;
    // Store a member variable for the listener
    EndlessRecyclerViewScrollListener mScrollListener;

    static class IncludedLayout{
        @BindView(R.id.rvTweets)
        RecyclerView rvTweets;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ButterKnife.bind(this);

        recylerViewLayout = new IncludedLayout();

        ButterKnife.bind(recylerViewLayout, recyclerView);

        client = TwitterApp.getRestClient();

        mTweets = new ArrayList<Tweet>();
        mTweetAdapter = new TweetAdapter(this, mTweets);
        mTweetAdapter.setOnTweetClickListener(tweet -> {
            //TODO load detail page
            // loadDetailPage(tweet);
        });


        initFloatingComposeButton();

        //initialize Recycler View
        initRecyclerView();


        JSONArray userresponse = null; //TODO remove

        //get user from file
        try {
            userresponse = new JSONArray(readJsonFile(R.raw.userresponse));
            loggedInUser = User.fromJSON(userresponse.getJSONObject(0).getJSONObject("user"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //initUserDetails(); //TODO uncomment

        try {
            loadTweets(readJsonFile(R.raw.twitterresponse_5));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // populateTimeline();  //TODO: uncomments this


    }

    private void initUserDetails() {
        client.getUserTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                try {
                      loggedInUser = User.fromJSON(response.getJSONObject(0).getJSONObject("user"));
                } catch (JSONException e) {
                        e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR", errorResponse.toString(), throwable);
            }
        });
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }


    private void initFloatingComposeButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: remove
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //navigate to Post Tweet fragment

                FragmentManager fm = getSupportFragmentManager();
                PostTweetFragment postTweetFragment = PostTweetFragment.newInstance(Parcels.wrap(loggedInUser));
                postTweetFragment.show(fm,POST_TWEET_FRAGMENT_TITLE );

            }
        });
    }

    public void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.scrollToPosition(0);
        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            public void onLoadMore(final int page, int totalItemsCount, final RecyclerView view) {
                //TODO add how to load more data
            }
        };
        recylerViewLayout.rvTweets.addOnScrollListener(mScrollListener);
        recylerViewLayout.rvTweets.setLayoutManager(linearLayoutManager);
        recylerViewLayout.rvTweets.setAdapter(mTweetAdapter);
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        mTweets.add(tweet);
                        mTweetAdapter.notifyItemInserted(mTweets.size() - 1);

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

    public void loadTweets(String tweets) throws JSONException {
        JSONArray response = new JSONArray(tweets);

        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                mTweets.add(tweet);
                mTweetAdapter.notifyItemInserted(mTweets.size() - 1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public String readJsonFile(int input) throws IOException {
        InputStream is = getResources().openRawResource(input);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }

        String jsonString = writer.toString();
        log.d("Response", jsonString);

        return jsonString;
    }

    @Override
    public void onPostTweet(Tweet tweet) {
        //TODO will the tweet be inserted on top or bottom ?

        mTweets.add(tweet);
        mTweetAdapter.notifyItemInserted(mTweets.size() - 1);
        linearLayoutManager.scrollToPositionWithOffset(0,0);


    }
}
