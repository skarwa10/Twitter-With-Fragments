package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SearchViewCompat;
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
import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import android.support.v7.app.AlertDialog;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.utils.TweetConstants.CANCEL_BUTTON_VALUE;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.DRAFT_TWEET_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.MAX_COUNT;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.OK_BUTTON_VALUE;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.USER_OBJ;
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

    @BindColor(R.color.lightGrey)
    int colorGrey;

    TwitterClient client;
    User user;

    private OnPostTweetListener mPostTweetListener;
    private OnComposeCloseListener mOnComposeCloseListener;

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
    public static PostTweetFragment newInstance(Parcelable user,String savedDraftTweet) {
        PostTweetFragment fragment = new PostTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(TweetConstants.USER_OBJ,user);
        args.putString(TweetConstants.DRAFT_TWEET_KEY,savedDraftTweet);
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

        user = (User) Parcels.unwrap(getArguments().getParcelable(USER_OBJ));

        if (user != null) {
            String profileImage =  user.getProfileImageUrl();

            Glide.with(getContext()).load(profileImage).fitCenter()
                    .into(ivProfileImage);
        }

        String draftTweet = getArguments().getString(DRAFT_TWEET_KEY);
        if(draftTweet!= null && !draftTweet.isEmpty()){
            etTweet.setText(draftTweet);
            int availableChar = TweetConstants.MAX_COUNT - draftTweet.length();
            if(availableChar < 0){
                tvCount.setText(String.valueOf(0));
            }
            tvCount.setText(String.valueOf(availableChar));
        }
        etTweet.setSelected(true);
        etTweet.requestFocus();

        etTweet.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                log.d("count",String.valueOf(count));
                log.d("before",String.valueOf(before));

                int availableChar = Integer.parseInt(tvCount.getText().toString());
                int newCharsAdded = count-before;
                int remainingChar = availableChar - newCharsAdded;
                if(remainingChar <= 140 && remainingChar >= 0){
                    tvCount.setTextColor(Color.LTGRAY);
                    tvCount.setText(String.valueOf(remainingChar));
                    btnTweet.setClickable(true);
                    btnTweet.setVisibility(View.VISIBLE);
                } else if(availableChar == 0){
                    btnTweet.setClickable(false);
                    tvCount.setTextColor(Color.RED);
                    btnTweet.setVisibility(View.GONE);
                    btnTweet.setBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ivCloseCompose.setOnClickListener(v -> {

            String composeText = etTweet.getText().toString();
            if(composeText.length() > 0){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setTitle(TweetConstants.SAVE_DRAFT_TWEET);
                alertDialogBuilder.setPositiveButton(TweetConstants.OK_BUTTON_VALUE,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnComposeCloseListener listener = (OnComposeCloseListener) getActivity();
                        listener.onComposeTextSave(composeText);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton(TweetConstants.CANCEL_BUTTON_VALUE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        dismiss();
                    }
                });
                alertDialogBuilder.create().show();
            } else {
                dismiss();
            }
        });

        btnTweet.setOnClickListener(v -> {
            String tweet =  etTweet.getText().toString();
            postTweet(tweet);
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostTweetListener) {
            mPostTweetListener = (OnPostTweetListener) context;
            mOnComposeCloseListener = (OnComposeCloseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPostTweetListener = null;
        mOnComposeCloseListener = null;
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

    public interface OnComposeCloseListener {
        void onComposeTextSave(String text);
    }
}
