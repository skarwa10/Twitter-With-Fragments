package com.codepath.apps.mysimpletweets.network;

import android.content.Context;

import com.codepath.apps.mysimpletweets.utils.FetchTweet;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "M5MEaaTcOLG858cpCwe3LfMmq";
	public static final String REST_CONSUMER_SECRET = "mHY9EpoSmuvjWTnwbJjEwBLRDqR6bbi34fnufEFAb6DlqtKbRB";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// API to get Tweets from Timeline
	public void getHomeTimeline(FetchTweet tweetType, long since_id, long max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.

		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(tweetType.equals(FetchTweet.FIRST_LOAD)) {
			params.put("since_id",1);
		} else if(tweetType.equals(FetchTweet.REFRESH_NEW_TWEETS)){
			params.put("since_id",since_id);
			//params.put("max_id", max_id);
		} else if(tweetType.equals(FetchTweet.SCROLL_OLD_TWEETS)){ //load old tweets
			params.put("max_id", max_id);
		}
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(FetchTweet tweetType, long since_id, long max_id, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(tweetType.equals(FetchTweet.FIRST_LOAD)) {
			params.put("since_id",1);
		} else if(tweetType.equals(FetchTweet.REFRESH_NEW_TWEETS)){
			params.put("since_id",since_id);
		} else if(tweetType.equals(FetchTweet.SCROLL_OLD_TWEETS)){ //load old tweets
			params.put("max_id", max_id);
		}
		client.get(apiUrl, params, handler);
	}

	//API to update status
	public void postTweet(String status,AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", status);
		client.post(apiUrl, params, handler);
	}

	public void getUserTimeline(FetchTweet tweetType, long since_id, long max_id,String screenName,AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name",screenName);

		if(tweetType.equals(FetchTweet.FIRST_LOAD)) {
			params.put("since_id",1);
		} else if(tweetType.equals(FetchTweet.REFRESH_NEW_TWEETS)){
			params.put("since_id",since_id);
		} else if(tweetType.equals(FetchTweet.SCROLL_OLD_TWEETS)){ //load old tweets
			params.put("max_id", max_id);
		}
		client.get(apiUrl,params, handler);
	}

	public void getUser(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name","sonalikarwa");
		client.get(apiUrl,params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
