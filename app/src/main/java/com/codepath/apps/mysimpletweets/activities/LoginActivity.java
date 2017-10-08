package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.application.TwitterApp;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.TweetConstants;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
	TwitterClient client;
	User loggedInUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		client = TwitterApp.getRestClient();
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_timeline, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {

		initUserDetails();
		//Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show();
	}


	private void initUserDetails() {
		client.getUser(new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
				Log.d("DEBUG", jsonObject.toString());
				try {
					loggedInUser = User.fromJSON(jsonObject);

					Intent i = new Intent(getApplicationContext(), TimeLineActivity.class);
					i.putExtra(TweetConstants.USER_OBJ, Parcels.wrap(loggedInUser));
					startActivity(i);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				Log.d("DEBUG", responseString.toString());
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Log.d("DEBUG", response.toString());
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Log.e("ERROR", errorResponse.toString(), throwable);
			}
		});
	}


	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		//e.printStackTrace();
		Toast.makeText(this,"Login Failed", Toast.LENGTH_SHORT).show();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to menu_timeline
	public void loginToRest(View view) {
		getClient().connect();
	}

}
