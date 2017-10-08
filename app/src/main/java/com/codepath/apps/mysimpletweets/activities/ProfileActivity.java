package com.codepath.apps.mysimpletweets.activities;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.N;

public class ProfileActivity extends AppCompatActivity {
    User user;
    TwitterClient client;

    @Nullable
    //@BindView(R.id.ivBanner)
    //ImageView bannerImg;

    @BindView(R.id.ivProfileImage)
    ImageView profileImg;

    @BindView(R.id.tvName)
    TextView name;

    @BindView(R.id.tvUserName)
    TextView username;

    @BindView(R.id.tvBody)
    TextView tagline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = Parcels.unwrap(getIntent().getParcelableExtra(TweetConstants.USER_OBJ));

        ButterKnife.bind(this);

        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user.getScreenName());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.flContainer,userTimelineFragment);

        fragmentTransaction.commit();

        //get user if not loggedInUser

        setUserHeader(user);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setUserHeader(User user){
        name.setText(user.getName());
        username.setText("@"+user.getScreenName());
        tagline.setText(user.getDescription());

        Glide.with(this).load(user.getProfileImageUrl()).fitCenter()
                .into(profileImg);


        //String banner = user.getProfileBannerUrl();

        /*if( banner != null) { //banner image present
            banner = banner.concat("/mobile");
            Glide.with(this).load(banner).fitCenter()
                    .into(bannerImg);
        }*/
    }
}
