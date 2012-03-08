package com.tudelft.triblerdroid.first;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



public class VodoEitActivity extends ListActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	  super.onCreate(savedInstanceState);

    	  ArrayList<String> videoList = new ArrayList<String>();
    	  videoList.add((String) getResources().getText(R.string.v1_title));
    	  videoList.add((String) getResources().getText(R.string.v2_title));
    	  videoList.add((String) getResources().getText(R.string.v3_title));
    	  
    	  
    	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, videoList));

    	  ListView lv = getListView();
    	  lv.setTextFilterEnabled(true);


    	  
    	  
    	  lv.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,
    	        int position, long id) {
    	    	// 	When clicked, show a toast with the TextView text
//    	    	TextView selected_item = (TextView) view;
//    	    	Toast.makeText(getApplicationContext(), Integer.toString(position),//((TextView) view).getText(),
//    	    			Toast.LENGTH_SHORT).show();

    	    	//See video info
    	    	Intent intent = new Intent(getBaseContext(), VideoInfoActivity.class);
    	    	intent.putExtra("video_pos", position);
    	    	startActivity(intent);

    	    	
    	      
    	    }
    	  });
    	}
//    static final String[] VIDEOS = new String[] {
//       	"Video 1","Video 2","Video 3","Video 4","Video 5","Video 6","Video 7", 
//       	"Video 1","Video 2","Video 3","Video 4","Video 5","Video 6","Video 7",
//       	"Video 1","Video 2","Video 3","Video 4","Video 5","Video 6","Video 7",
//       	"Video 1","Video 2","Video 3","Video 4","Video 5","Video 6","Video 7",
//   };
      
}

