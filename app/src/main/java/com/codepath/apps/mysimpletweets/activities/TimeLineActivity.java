package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.adapters.TweetAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.R.id.rvTweets;

public class TimeLineActivity extends AppCompatActivity {
    View recyclerview;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    RecyclerView rvTweets;
    ArrayList<Tweet> mTweets;
    TweetAdapter mTweetAdapter;
    TwitterClient client;
    // Store a member variable for the listener
    EndlessRecyclerViewScrollListener mScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        View recyclerview = findViewById(R.id.recyclerview_layout);
        rvTweets = (RecyclerView) recyclerview.findViewById(R.id.rvTweets);


        mTweets = new ArrayList<Tweet>();
        mTweetAdapter = new TweetAdapter(this,mTweets);
        mTweetAdapter.setOnTweetClickListener(tweet -> {
            //TODO load detail page
               // loadDetailPage(tweet);
        });


        initFloatingComposeButton();

        //initialize Recycler View
        initRecyclerView();



        client = TwitterApp.getRestClient();
        populateTimeline();


    }

    private void initFloatingComposeButton() {
        //TODO
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager){
            public void onLoadMore(final int page, int totalItemsCount, final RecyclerView view) {

            }
        };
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(mTweetAdapter);
    }

    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                for(int i=0;i<response.length();i++){
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        mTweets.add(tweet);
                        mTweetAdapter.notifyItemInserted(mTweets.size() - 1 );

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR", errorResponse.toString(),throwable);
            }
        });
    }

}
