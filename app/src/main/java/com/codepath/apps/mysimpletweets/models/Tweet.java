package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.utils.TweetConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

import static com.codepath.apps.mysimpletweets.utils.TweetConstants.CREATED_AT;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.EXTENDED_ENTITIES_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.ID_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.MEDIA;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.TEXT_KEY;
import static com.codepath.apps.mysimpletweets.utils.TweetConstants.USER_KEY;

/**
 * Created by skarwa on 9/27/17.
 */

@Parcel
public class Tweet {
    String mBody;
    long mUid;
    User mUser;
    String mCreatedAt;
    int isTweetWithMedia = 0;
    ArrayList<Media> media;

    //needed by parceler
    public Tweet() {
    }

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();
        tweet.media = new ArrayList<>();

        tweet.mBody =jsonObject.optString(TweetConstants.TEXT_KEY);
        tweet.mUid = jsonObject.optLong(TweetConstants.ID_KEY);
        tweet.mCreatedAt = jsonObject.optString(TweetConstants.CREATED_AT);
        tweet.mUser = User.fromJSON(jsonObject.getJSONObject(TweetConstants.USER_KEY));

        JSONObject extendedEntitiesJSON = jsonObject.optJSONObject(TweetConstants.EXTENDED_ENTITIES_KEY);
        if(extendedEntitiesJSON != null){
            JSONArray mediaArray = extendedEntitiesJSON.getJSONArray(TweetConstants.MEDIA);
            for (int i = 0; i < mediaArray.length(); i++) {
                tweet.media.add(Media.fromJSON(mediaArray.getJSONObject(i)));
            }
        }
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

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public void setCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public void setIsTweetWithMedia(int isTweetWithMedia) {
        this.isTweetWithMedia = isTweetWithMedia;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }
}
