package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApp;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Parcelable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostTweetFragment.OnPostTweetListener} interface
 * to handle interaction events.
 * Use the {@link PostTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostTweetFragment extends DialogFragment {
    public static final String USER_OBJ = "user";
    public static final int MAX_COUNT = 140;
    //public static final String

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.etTweet)
    EditText etTweet;

    @BindView(R.id.ivCloseCompose)
    ImageView ivCloseCompose;

    @BindView(R.id.tvCount)
    TextView tvCount;

    @BindView(R.id.btnTweet)
    Button btnTweet;

    TwitterClient client;
    User user;

    private OnPostTweetListener mListener;

    public PostTweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user
     * @return A new instance of fragment PostTweetFragment.
     */
    public static PostTweetFragment newInstance(Parcelable user) {
        PostTweetFragment fragment = new PostTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_OBJ,user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_tweet, null);
        ButterKnife.bind(this, view);

        user = (User) Parcels.unwrap(getArguments().getParcelable("user"));

        if (user != null) {
            String profileImage =  user.getProfileImageUrl();

            Glide.with(getContext()).load(profileImage).fitCenter()
                    .into(ivProfileImage);
        }

        etTweet.setSelected(true);
        etTweet.requestFocus();

       // tvCount.setSelected(false);
        etTweet.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                log.d("count",String.valueOf(count));
                log.d("before",String.valueOf(before));

                int remainingChar = MAX_COUNT;
                if(count<=MAX_COUNT){
                    remainingChar = MAX_COUNT - count;
                    tvCount.setText(String.valueOf(remainingChar));
                    btnTweet.setEnabled(true);
                } else {
                    btnTweet.setEnabled(false);
                    tvCount.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String tweet =  etTweet.getText().toString();
                postTweet(tweet);
            }
        });

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostTweetListener) {
            mListener = (OnPostTweetListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void postTweet(String tweetMsg){
        client.postTweet(tweetMsg,new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());

                Tweet tweet = null;
                try {
                    tweet = Tweet.fromJSON(response);

                    OnPostTweetListener listener = (OnPostTweetListener) getActivity();
                    listener.onPostTweet(tweet);

                    dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", errorResponse.toString(),throwable);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPostTweetListener {
        void onPostTweet(Tweet tweet);
    }
}
