package com.codepath.apps.mysimpletweets.utils;


/**
 * Created by skarwa on 10/5/17.
 */

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class TweetTypeConverter extends com.raizlabs.android.dbflow.converter.TypeConverter<String,TweetType> {

    @Override
    public String getDBValue(TweetType type) {
        return type.name();
    }

    @Override
    public TweetType getModelValue(String type) {
        return TweetType.valueOf(type);
    }
}
