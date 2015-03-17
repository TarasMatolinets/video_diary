package com.mti.videodialy.uploads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTubeScopes;
//import android.provider.MediaStore.Video;


public class SubmitActivity extends Activity {
	
	
	  /**
	   * Logging level for HTTP requests/responses.
	   * 
	   * <p>
	   * To turn on, set to {@link java.util.logging.Level#CONFIG} or {@link java.util.logging.Level#ALL} and run this from command line:
	   * </p>
	   * 
	   * <pre>
	adb shell setprop log.tag.HttpTransport DEBUG
	   * </pre>
	   */
	
	  private static final Level LOGGING_LEVEL = Level.OFF;

	  private static final String PREF_ACCOUNT_NAME = "accountName";
	
	  static final String TAG = "YoutubeSampleActivity";
	
	  static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	
	  static final int REQUEST_AUTHORIZATION = 1;
	
	  static final int REQUEST_ACCOUNT_PICKER = 2;
	
	  final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	  
	  final JsonFactory jsonFactory = new GsonFactory();
	
	  GoogleAccountCredential credential;
	
	  /** Global instance of Youtube object to make all API requests. */ 
	  com.google.api.services.youtube.YouTube youtube;
	  
	  int numAsyncTasks;
	  
	  /* Global instance of the format used for the video being uploaded (MIME type). */
	  private static String VIDEO_FILE_FORMAT = "video/*";
	  
	  
//	  CalendarModel model = new CalendarModel();
//	  ArrayAdapter<CalendarInfo> adapter;
//	  com.google.api.services.calendar.Calendar client;
	  
	  
	  Uri videoUri = null;
	  TextView textViewStatus ;
	  ProgressDialog dialog = null;
	  
	  private Date dateTaken = null;
	  private Long uploadFileSize = null;
	 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    // enable logging
	    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
	    // view and menu

	    // Google Accounts
	    credential = GoogleAccountCredential.usingOAuth2(this, YouTubeScopes.YOUTUBE_UPLOAD );
	    // save simple data 
	    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
	    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
	    // youtube client
	    youtube = new com.google.api.services.youtube.YouTube.Builder(
	        transport, jsonFactory, credential).setApplicationName("Google-YoutubeUploadAndroidSample/1.0")
	        .build();
	    
	    
	    Intent intent = this.getIntent();
	    this.videoUri = intent.getData();
	    Log.d(TAG,intent.getData().toString());
	    

	  }

void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		    runOnUiThread(new Runnable() {
		      public void run() {
		        Dialog dialog =
		            GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, SubmitActivity.this,
		                REQUEST_GOOGLE_PLAY_SERVICES);
		        dialog.show();
		      }
		    });
		  }


	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	      case REQUEST_GOOGLE_PLAY_SERVICES:
	        if (resultCode == Activity.RESULT_OK) {
	          haveGooglePlayServices();
	        } else {
	          checkGooglePlayServicesAvailable();
	        }
	        break;
	      case REQUEST_AUTHORIZATION:
	        if (resultCode == Activity.RESULT_OK) {
	        	AsyncLoadYoutube.run(this);
	        } else {
	          chooseAccount();
	        }
	        break;
	      case REQUEST_ACCOUNT_PICKER:
	        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
	          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
	          if (accountName != null) {
	            credential.setSelectedAccountName(accountName);
	            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putString(PREF_ACCOUNT_NAME, accountName);
	            editor.commit();
	            AsyncLoadYoutube.run(this);
	          }
	        }
	        break;
	    }
	  }

	  /** Check that Google Play services APK is installed and up to date. */
	  private boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
	      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
	      return false;
	    }
	    return true;
	  }

	  private void haveGooglePlayServices() {
		Log.d(TAG,"accountName="+credential.getSelectedAccountName());
	    // check if there is already an account selected
	    if (credential.getSelectedAccountName() == null) {
	      // ask user to choose account
	      chooseAccount();
	    } else {
	      // upload youtube.
	    	AsyncLoadYoutube.run(this);
	    }
	  }

	  private void chooseAccount() {
	    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	  }

	  @Override	
	  protected Dialog onCreateDialog(int id) {
		  
		  if ( id==1 ){
			  dialog = new ProgressDialog(this);
			  dialog.setTitle("upload video on youtube");
			  dialog.setMessage("upload video on youtube");
			  dialog.setMax(100);
			  dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			  
		  }
		  return dialog;
	  }



}