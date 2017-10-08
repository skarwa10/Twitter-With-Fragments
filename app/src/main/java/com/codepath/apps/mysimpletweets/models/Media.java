package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.database.MyDatabase;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by skarwa on 9/30/17.
 */
@Parcel
public class Media extends BaseModel{

    @PrimaryKey
    @Column
    long mId;

    @Column
    String mType;

    @Column
    String mUrl;


    public Media() {
    }

    public static Media fromJSON(JSONObject jsonObject) throws JSONException{
        Media media = new Media();

        media.mId = jsonObject.getLong(TweetConstants.ID_KEY);
        media.mType = jsonObject.optString(TweetConstants.MEDIA_TYPE);
        media.mUrl = jsonObject.optString(TweetConstants.MEDIA_URL);

        return media;
    }

    public long getId() {
        return mId;
    }

    public String getType() {
        return mType;
    }

    public String getUrl() {
        return mUrl;
    }
}
