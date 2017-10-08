package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.FetchTweet;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skarwa on 10/5/17.
 */

public class HomeTimelineFragment extends TweetsListFragment {
    TwitterClient client;

    public HomeTimelineFragment() {
    }

    public static HomeTimelineFragment newInstance(Parcelable tweet){
        HomeTimelineFragment homeTimelineFragment = new HomeTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable(TweetConstants.TWEET_OBJ,tweet);
        homeTimelineFragment.setArguments(args);
        return homeTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApp.getRestClient();

        //populates the timeline on first load.
        populateTimeline(FetchTweet.FIRST_LOAD, 1, 0);

    }

    public void populateTimeline(FetchTweet type, long since_id, long max_id) {
        client.getHomeTimeline(type, since_id, max_id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                addItems(type,response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR", errorResponse.toString(), throwable);
            }
        });
    }
}
