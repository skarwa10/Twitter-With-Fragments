package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.database.MyDatabase;
import com.codepath.apps.mysimpletweets.utils.TweetType;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.codepath.apps.mysimpletweets.utils.TweetTypeConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skarwa on 9/27/17.
 */

@Parcel
public class Tweet extends BaseModel {

    @PrimaryKey
    @Column
    long mUid;

    @Column
    String mBody;

    @ForeignKey(saveForeignKeyModel = true)
    User mUser;

    @Column
    String mCreatedAt;

    @Column
    boolean mRetweeted;

    @Column
    boolean mFavorited;

    @Column
    int mRetweet_count;

    @Column
    int mFavorite_count;

    @Column(typeConverter = TweetTypeConverter.class)
    TweetType type;

    List<Media> media;

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
        tweet.mFavorited = jsonObject.getBoolean(TweetConstants.FAVORITED);
        tweet.mFavorite_count = jsonObject.optInt(TweetConstants.FAVORITE_COUNT);
        tweet.mRetweeted = jsonObject.optBoolean(TweetConstants.RETWEETED);
        tweet.mRetweet_count = jsonObject.optInt(TweetConstants.RETWEET_COUNT);

        JSONObject extendedEntitiesJSON = jsonObject.optJSONObject(TweetConstants.EXTENDED_ENTITIES_KEY);
        if(extendedEntitiesJSON != null){
            tweet.type = TweetType.HAS_MEDIA;
            JSONArray mediaArray = extendedEntitiesJSON.getJSONArray(TweetConstants.MEDIA);
            for (int i = 0; i < mediaArray.length(); i++) {
                tweet.media.add(Media.fromJSON(mediaArray.getJSONObject(i)));
            }
        } else {
            tweet.type = TweetType.NO_MEDIA;
        }
        return tweet;
    }

/*    @OneToMany(methods = OneToMany.Method.ALL, variableName = "media")
    public List<Media> oneToManyAuthors() {
        if (media == null) {
            media = SQLite.select()
                    .from(Media.class)
                    .where(Media_Table.tweet_mUid.eq(mUid))
                    .queryList();
        }
        return media;
    }*/

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

    public boolean isRetweeted() {
        return mRetweeted;
    }

    public boolean isFavorited() {
        return mFavorited;
    }

    public int getRetweet_count() {
        return mRetweet_count;
    }

    public int getFavorite_count() {
        return mFavorite_count;
    }

    public TweetType getType() {
        return type;
    }

    public List<Media> getMedia() {
        return media;
    }


    // Record Finders
    /*public static SampleModel byId(long id) {
        return new Select().from(SampleModel.class).where(Tweet_Table.mUid.eq(id)).querySingle();
    }

    public static List<SampleModel> recentItems() {
        return new Select().from(SampleModel.class).orderBy(Tweet_Table.mUid, false).limit(25).queryList();
    }*/

   /* @Override
    public boolean save() {
        boolean res = super.save();
        if (media != null) {
            for (Media m : media) {
                m.tweet = this;
                m.save();
            }
        }
        return res;
    }*/
}
