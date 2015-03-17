package com.mti.videodialy.uploads;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class YoutubeUploadActivity extends Activity {

	  private static final String LOG_TAG = YoutubeUploadActivity.class.getSimpleName();
	  private static final int CAPTURE_RETURN = 1;
	  private static final int GALLERY_RETURN = 2;
	  private static final int SUBMIT_RETURN = 3;

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.activity_youtube_upload);

	    findViewById(R.id.captureButton).setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        Intent i = new Intent();
	        i.setAction("android.media.action.VIDEO_CAPTURE");
	        startActivityForResult(i, CAPTURE_RETURN);
	      }
	    });

	    findViewById(R.id.galleryButton).setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        Intent intent = new Intent();
	        intent.setAction(Intent.ACTION_PICK);
	        intent.setType("video/*");

	        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
	            PackageManager.MATCH_DEFAULT_ONLY);
	        if (list.size() <= 0) {
	          Log.d(LOG_TAG, "no video picker intent on this hardware");
	          return;
	        }

	        startActivityForResult(intent, GALLERY_RETURN);
	      }
	    });
	  }

	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    switch (requestCode) {
	    case CAPTURE_RETURN:
	    case GALLERY_RETURN:
	      if (resultCode == RESULT_OK) {
	        Intent intent = new Intent(this, SubmitActivity.class);
	        intent.setData(data.getData());
	        startActivityForResult(intent, SUBMIT_RETURN);
	      }
	      break;
	    case SUBMIT_RETURN:
	      if (resultCode == RESULT_OK) {
	        Toast.makeText(YoutubeUploadActivity.this, "thank you!", Toast.LENGTH_LONG).show();
	      } else {
	        // Toast.makeText(DetailsActivity.this, "submit failed or cancelled",
	        // Toast.LENGTH_LONG).show();
	      }
	      break;
	    }
	  }

	  @Override
	  public void onStart() {
	    super.onStart();
	  }

	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_youtube_upload, menu);
		return true;
	}

}
