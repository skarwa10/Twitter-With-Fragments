package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.activities.TimeLineActivity;
import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.FetchTweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by skarwa on 10/5/17.
 */

public class MentionsTimelineFragment extends TweetsListFragment {
    TwitterClient client;
    private String title;
    private int page;

    public MentionsTimelineFragment() {
    }

    public static MentionsTimelineFragment newInstance(int page , String title){
        MentionsTimelineFragment mentionsTimelineFragment = new MentionsTimelineFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        mentionsTimelineFragment.setArguments(args);
        return mentionsTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("page", 0);
        title = getArguments().getString("title");

        client = TwitterApp.getRestClient();

        //populates the timeline on first load.
        populateTimeline(FetchTweet.FIRST_LOAD, 1, 0);

    }

    public void populateTimeline(FetchTweet type, long since_id, long max_id) {
        client.getMentionsTimeline(type, since_id, max_id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                addItems(type,response);
                ((TimeLineActivity)getActivity()).hideProgressBar();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR", errorResponse.toString(), throwable);
            }
        });
    }
}
