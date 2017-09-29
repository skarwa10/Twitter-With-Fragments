package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by skarwa on 9/27/17.
 */

@Parcel
public class Tweet {
    String mBody;
    long mUid;
    User mUser;
    String mCreatedAt;
    int isTweetWithMedia = 0; //TODO update

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        tweet.mBody =jsonObject.getString("text");
        tweet.mUid = jsonObject.getLong("id");
        tweet.mCreatedAt = jsonObject.getString("created_at");
        tweet.mUser = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }

    public String getBody() {
        return mBody;
    }

    public long getUid() {
        return mUid;
    }

    public User getUser() {
        return mUser;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public int isTweetWithMedia() {
        return isTweetWithMedia;
    }
}
