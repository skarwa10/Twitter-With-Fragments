package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.TimeLineActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.FetchTweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by skarwa on 10/5/17.
 */

public abstract class TweetsListFragment extends Fragment implements TweetAdapter.OnProfileImageSelectedListener,TweetAdapter.OnTweetSelectedListener{

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    ArrayList<Tweet> mTweets;
    TweetAdapter mTweetAdapter;
    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener mScrollListener;

    public TweetsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        ButterKnife.bind(this, view);

        initAdapter();

        initRecyclerView();

        initSwipeRefresh();

        return view;
    }

    private void initAdapter() {
        mTweets = new ArrayList<Tweet>();
        mTweetAdapter = new TweetAdapter(getContext(), mTweets,this,this);
    }

    public void initSwipeRefresh() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                long since_id = 1;
                long max_id = 1;
                if (mTweetAdapter.getItemCount() > 0) {
                    since_id = mTweets.get(0).getUid();
                    max_id = mTweetAdapter.getOldestTweetId() - 1;
                }

                populateTimeline(FetchTweet.REFRESH_NEW_TWEETS, since_id, max_id);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    private void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.scrollToPosition(0);
        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            public void onLoadMore(final int page, int totalItemsCount, final RecyclerView view) {
                //int curSize = mTweetAdapter.getItemCount();

                long max_id = mTweetAdapter.getOldestTweetId() - 1;
                populateTimeline(FetchTweet.SCROLL_OLD_TWEETS, 1, max_id);
            }
        };
        rvTweets.addOnScrollListener(mScrollListener);
        rvTweets.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);
        rvTweets.setAdapter(mTweetAdapter);
    }


    public void addItems(FetchTweet type, JSONArray response) {
        if (type.equals(FetchTweet.FIRST_LOAD)) {

            try {
                List<Tweet> tweets = loadTweets(response.toString());
                mTweets.addAll(tweets);
                mTweetAdapter.notifyItemRangeInserted(0, tweets.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equals(FetchTweet.REFRESH_NEW_TWEETS)) {

            // Remember to CLEAR OUT old items before appending in the new ones


            try {
                List<Tweet> tweets = loadTweets(response.toString());
                if(tweets.size() >=1){
                    mTweetAdapter.clear();
                    mScrollListener.resetState();
                    // ...the data has come back, add new items to your adapter...
                    //mTweetAdapter.addAll(tweets);
                    mTweets.addAll(tweets);
                    mTweetAdapter.notifyItemRangeInserted(0, tweets.size());

                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            } catch (JSONException e) {
                Log.e("ERROR", e.getMessage(), e.getCause());
            }

        } else if (type.equals(FetchTweet.SCROLL_OLD_TWEETS)) {

            try {
                int curSize = mTweetAdapter.getItemCount();
                List<Tweet> tweets = loadTweets(response.toString());
                mTweets.addAll(tweets);
                mTweetAdapter.notifyItemRangeInserted(curSize, tweets.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Tweet> loadTweets(String tweets) throws JSONException {
        JSONArray response = new JSONArray(tweets);

        List<Tweet> tweetList = new ArrayList<Tweet>();
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                //   tweet.save();
                tweetList.add(tweet);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweetList;
    }


    public interface OnProfileImageSelectedListener{
        void onProfileView(User user);
    }

    public interface OnTweetSelectedListener {
        void loadDetailPage(Tweet tweet);
    }

    @Override
    public void onTweetSelected(Tweet tweet){
        ((OnTweetSelectedListener)getActivity()).loadDetailPage(tweet);
    }


    @Override
    public void onProfileImageSelected(User user){
        ((OnProfileImageSelectedListener)getActivity()).onProfileView(user);
    }

    public abstract void populateTimeline(FetchTweet type, long since_id, long max_id);

}
