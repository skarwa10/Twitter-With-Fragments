package com.codepath.apps.mysimpletweets.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Media;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.apps.mysimpletweets.R.id.etTweet;
import static com.codepath.apps.mysimpletweets.R.id.tvBody;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.TWEET_OBJ;
import static com.raizlabs.android.dbflow.config.FlowLog.Level.I;

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.tvBody)
    TextView tvBody;

    @BindView(R.id.ivMedia1)
    ImageView ivMedia1;

    @BindView(R.id.ivMedia2)
    ImageView ivMedia2;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(TWEET_OBJ));

        ButterKnife.bind(this);

        generateDetailTweetView();
    }

    private void generateDetailTweetView(){
        User user = tweet.getUser();
        tvName.setText(user.getName());
        tvUserName.setText("@"+user.getScreenName());
        tvBody.setText(tweet.getBody());

        Glide.with(this).load(user.getProfileImageUrl()).fitCenter()
                .into(ivProfileImage);

        List<Media> media = tweet.getMedia();
        if(media.size() >= 1){
            int count = 1;

            for(int i = 0;i< media.size(); i ++){
                if(media.get(i).getType().equals(TweetConstants.MEDIA_TYPE_PHOTO)){

                    if(count == 1){
                        ivMedia1.setVisibility(View.VISIBLE);
                        Glide.with(this).load(media.get(i).getUrl())
                                .into(ivMedia1);
                        count++;
                    }
                    if(count == 2) {
                        ivMedia2.setVisibility(View.VISIBLE);
                        Glide.with(this).load(media.get(i).getUrl())
                                .into(ivMedia2);
                        count++;
                    }
                }
            }
        }else {
            ivMedia1.setVisibility(View.INVISIBLE);
            ivMedia2.setVisibility(View.INVISIBLE);
        }
    }
}
