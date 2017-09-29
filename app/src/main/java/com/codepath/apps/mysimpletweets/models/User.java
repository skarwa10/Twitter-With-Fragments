package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by skarwa on 9/27/17.
 */
@Parcel
public class User {
    String mName;
    String mScreenName;
    long mUid;
    String mProfileImageUrl;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.mName =jsonObject.getString("name");
        user.mScreenName = jsonObject.getString("screen_name");
        user.mUid = jsonObject.getLong("id");
        user.mProfileImageUrl = jsonObject.getString("profile_image_url");
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
