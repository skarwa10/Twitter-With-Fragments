package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.database.MyDatabase;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.codepath.apps.mysimpletweets.utils.TweetConstants.FOLLOWERS_COUNT;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.FOLLOWING_COUNT;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.NAME_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.PROFILE_BANNER_URL;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.PROFILE_IMAGE_URL;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.SCREEN_NAME_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.USER_TAGLINE;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.VERIFIED;

/**
 * Created by skarwa on 9/27/17.
 */
@Parcel
public class User extends BaseModel {

    long mUid;


    String mName;

    String mScreenName;


    String mProfileImageUrl;


    String mProfileBannerUrl;


    String mDescription;


    int mNumOfFollowers;


    int mNumFollowing;

    boolean mVerified;

    // empty constructor needed by the Parceler library
    public User() {
    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.mName =jsonObject.getString(NAME_KEY);
        user.mScreenName = jsonObject.getString(SCREEN_NAME_KEY);
        user.mUid = jsonObject.optLong(TweetConstants.ID_KEY);
        user.mProfileImageUrl = jsonObject.optString(PROFILE_IMAGE_URL);
        user.mProfileBannerUrl = jsonObject.optString(PROFILE_BANNER_URL);
        user.mDescription = jsonObject.optString(USER_TAGLINE);
        user.mNumOfFollowers = jsonObject.optInt(FOLLOWERS_COUNT);
        user.mNumFollowing = jsonObject.optInt(FOLLOWING_COUNT);
        user.mVerified = jsonObject.optBoolean(VERIFIED,false);

        return user;
    }

    public String getName() {
        return mName;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public long getUid() {
        return mUid;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getNumOfFollowers() {
        return mNumOfFollowers;
    }

    public int getNumFollowing() {
        return mNumFollowing;
    }

    public boolean isVerified() {
        return mVerified;
    }

    public String getProfileBannerUrl() {
        return mProfileBannerUrl;
    }
}
