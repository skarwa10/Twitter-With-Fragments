package com.codepath.apps.mysimpletweets.activities;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import static android.support.v4.view.MenuItemCompat.getActionView;

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

    @BindView(R.id.tvFollowersCount)
    TextView tvNumOfFollowers;

    @BindView(R.id.tvFollowingCount)
    TextView tvNumFollowing;

    MenuItem miActionProgressItem;
    SearchView mSearchView;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Store instance of the menu item containing progress
        ProgressBar v = (ProgressBar) getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {

        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }


    public void setUserHeader(User user){
        name.setText(user.getName());
        username.setText("@"+user.getScreenName());
        tagline.setText(user.getDescription());
        tvNumFollowing.setText(String.valueOf(user.getNumFollowing()));

        tvNumOfFollowers.setText(String.valueOf(user.getNumOfFollowers()));


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
