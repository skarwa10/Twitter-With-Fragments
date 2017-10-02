package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.utils.TweetConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.codepath.apps.mysimpletweets.utils.TweetConstants.NAME_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.PROFILE_IMAGE_URL;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.SCREEN_NAME_KEY;

/**
 * Created by skarwa on 9/27/17.
 */
@Parcel
public class User {
    String mName;
    String mScreenName;
    long mUid;
    String mProfileImageUrl;

    // empty constructor needed by the Parceler library
    public User() {
    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.mName =jsonObject.getString(NAME_KEY);
        user.mScreenName = jsonObject.getString(SCREEN_NAME_KEY);
        user.mUid = jsonObject.getLong(TweetConstants.ID_KEY);
        user.mProfileImageUrl = jsonObject.getString(PROFILE_IMAGE_URL);
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
}
