package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.utils.TweetConstants;

import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by skarwa on 9/30/17.
 */
@Parcel
public class Media {
    String mType;
    String mUrl;

    public Media() {
    }

    public static Media fromJSON(JSONObject jsonObject){
        Media media = new Media();

        media.mType = jsonObject.optString(TweetConstants.MEDIA_TYPE);
        media.mUrl = jsonObject.optString(TweetConstants.MEDIA_URL);

        return media;
    }

    public String getType() {
        return mType;
    }

    public String getUrl() {
        return mUrl;
    }
}
