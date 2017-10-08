package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.activities.TimeLineActivity;
import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.FetchTweet;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skarwa on 10/6/17.
 */

public class UserTimelineFragment extends TweetsListFragment {
    TwitterClient client;

    public UserTimelineFragment() {
    }

    public static UserTimelineFragment newInstance(String screenName){
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name",screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApp.getRestClient();

        //populates the timeline on first load.
        populateTimeline(FetchTweet.FIRST_LOAD, 1, 0);

    }

    public void populateTimeline(FetchTweet type, long since_id, long max_id) {
        //((ProfileActivity)getActivity()).showProgressBar();
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeline(type,since_id,max_id,screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                addItems(type,response);
                ((ProfileActivity)getActivity()).hideProgressBar();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR", errorResponse.toString(), throwable);
            }
        });
    }
}
