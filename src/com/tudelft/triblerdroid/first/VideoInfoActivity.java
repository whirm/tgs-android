package com.tudelft.triblerdroid.first;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



public class VideoInfoActivity extends Activity {
	
 	String title = "TT";
	String description = "DD";
	String url;
	String hash;
	String tracker;
	String destination;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.video_info);
    	
    	Bundle extras = getIntent().getExtras();
    	final Integer pos = extras.getInt("video_pos", 0);
    	
    	
    	switch (pos) {
    		case 0: 
    			title =  (String) getResources().getText(R.string.v1_title);
    			description =  (String) getResources().getText(R.string.v1_description);
    			url =  (String) getResources().getText(R.string.v1_url);
    			hash =  (String) getResources().getText(R.string.v1_hash);
    			tracker =  (String) getResources().getText(R.string.v1_tracker);
    			destination =  (String) getResources().getText(R.string.v1_destination);
    			
        		break;
    		case 1:
    			title =  (String) getResources().getText(R.string.v2_title);
    			description =  (String) getResources().getText(R.string.v2_description);
    			url =  (String) getResources().getText(R.string.v2_url);
    			hash =  (String) getResources().getText(R.string.v2_hash);
    			tracker =  (String) getResources().getText(R.string.v2_tracker);
    			destination =  (String) getResources().getText(R.string.v2_destination);
    			break;
    		case 2:
    			title =  (String) getResources().getText(R.string.v3_title);
    			description =  (String) getResources().getText(R.string.v3_description);
    			url =  (String) getResources().getText(R.string.v3_url);
    			hash =  (String) getResources().getText(R.string.v3_hash);
    			tracker =  (String) getResources().getText(R.string.v3_tracker);
    			destination =  (String) getResources().getText(R.string.v3_destination);
    			break;
    	}
    	
    	TextView t = (TextView) findViewById(R.id.title);
    	t.setText(title);
    	t = (TextView) findViewById(R.id.description);
    	t.setText(description);

    	Button b_play = (Button) findViewById(R.id.b_play);
  	  
    	b_play.setOnClickListener(new OnClickListener() {
      	    public void onClick(View view) {
      	    	//Start video
//      	    	Intent intent = new Intent(getBaseContext(), VideoActivity.class);
//    	    	intent.putExtra("video_pos", pos);
      	    	//FIXME
      	    	Intent intent = new Intent(getBaseContext(), ScriptActivity.class);
    	    	intent.putExtra("hash", hash);
    	    	intent.putExtra("tracker", tracker);
    	    	intent.putExtra("destination", destination);
      	    	startActivity(intent);
      	    }
      	});
    	
      	Button b_web_info = (Button) findViewById(R.id.b_web_info);
      	 	  
      	b_web_info.setOnClickListener(new OnClickListener() {
      	    public void onClick(View view) {
      	    	//Start video
      	    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      	      	startActivity(intent);
      	    }
      	});
    }
      
}