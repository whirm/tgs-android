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
    	  
    	  
    	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, VIDEOS));

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
//    	    	Intent intent = new Intent(getBaseContext(), VideoInfoActivity.class);
//    	    	intent.putExtra("video_pos", position);
//    	    	startActivity(intent);      	    	
    	    	
//    	    	Play video
    	    	Intent intent = new Intent(getBaseContext(), ScriptActivity.class);
    	    	intent.putExtra("hash", HASHES[position]);
    	    	intent.putExtra("tracker", "tracker3.p2p-next.org:2002");
//    	    	intent.putExtra("destination", destination);
      	    	startActivity(intent);


    	    	
    	      
    	    }
    	  });
    	}
	static final String[] VIDEOS = new String[] {
		"Ken Robinson says schools kill creativity", 
		"Jill Bolte Taylor's stroke of insight", 
		"Pranav Mistry: The thrilling potential of SixthSense technology", 
		"David Gallo shows underwater astonishments", 
		"Pattie Maes and Pranav Mistry demo SixthSense", 
		"Simon Sinek: How great leaders inspire action", 
		"Arthur Benjamin does 'Mathemagic'", 
		"Hans Rosling shows the best stats you've ever seen", 
		"Rob Reid: The $8 billion iPod", 
		"Brené Brown: Listening to shame", 
    	"Susan Cain: The power of introverts", 
		"Vijay Kumar: Robots that fly ... and cooperate", 
    	"I Think Were Alone Now", 
		"L5 Part 1", 
    	"Pioneer One S01E06", 
		"An Honest Man", 
    };
	static final String[] HASHES = new String[] {
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
		"280244b5e0f22b167f96c08605ee879b0274ce22", 
    };
      
}

