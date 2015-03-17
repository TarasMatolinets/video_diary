/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mti.videodialy.uploads;

import java.io.IOException;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.Video;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 * 
 * @author Yaniv Inbar
 */
abstract class YoutubeAsyncTask extends AsyncTask<Void, Integer, Video> {
	
  static final String TAG = "YoutubeSampleActivity";
  final SubmitActivity activity;
  final com.google.api.services.youtube.YouTube youtube;
//  private final View progressBar;
  final TextView textViewStatus;
  YoutubeAsyncTask(SubmitActivity activity) {
    this.activity = activity;
    youtube = activity.youtube;
//    progressBar = activity.findViewById(R.id.title_refresh_progress);
    textViewStatus = activity.textViewStatus;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    activity.numAsyncTasks++;
//    progressBar.setVisibility(View.VISIBLE);
    activity.showDialog(1);
  }


  @Override
  protected void onProgressUpdate(Integer... values) {
	Log.d("","onProgressUpdate: " + values[0]);  
    activity.dialog.setProgress(values[0]);
    
  }
  @Override
  protected final Video doInBackground(Void... Ignored) {
    try {
      return doInBackground();
      // return true;
    } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
    	Log.e(SubmitActivity.TAG,"GooglePlayServicesAvailabilityIOException");
      activity.showGooglePlayServicesAvailabilityErrorDialog(
          availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
    	Log.e(SubmitActivity.TAG,"UserRecoverableAuthIOException");
      activity.startActivityForResult(
          userRecoverableException.getIntent(), SubmitActivity.REQUEST_AUTHORIZATION);
    } catch (IOException e) {
    	Log.e(SubmitActivity.TAG,"IOException");
      Utils.logAndShow(activity, SubmitActivity.TAG, e);
    }
    return null;
  }


  @Override
  protected final void onPostExecute(Video returnedVideo) {
    super.onPostExecute(returnedVideo);
    if (0 == --activity.numAsyncTasks) {
//      progressBar.setVisibility(View.GONE);
      activity.dismissDialog(1);
    }
    if (returnedVideo != null) {
    	// if upload succeed then.. 
    	// activity.refreshView();
    	textViewStatus.setText("\n=== Returned Video ===\n"
	    		  + "\n  - Id: " + returnedVideo.getId() 
	    		  + "\n  - Title: " + returnedVideo.getSnippet().getTitle()
	    		  + "\n  - Tags: " + returnedVideo.getSnippet().getTags()
	    		  + "\n  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus()
	    		  + "\n  - Video Count: " + returnedVideo.getStatistics().getViewCount());
    }
  }

  abstract protected Video doInBackground() throws IOException;
}
