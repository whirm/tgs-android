package com.tudelft.triblerdroid.first;

/*
 * StopP2PEngine diagram:
 * 
 * VodoEitActivity.onDestroy():
 *        Always
 * 
 * VodoEitActivity.onUserLeaveHint:
 *        Unfortunately also generated when SwiftAct is started,
 *        so only stopEngine when swift doesn't have focus? Assumes
 *        order of events (SwiftAct focus before VodoAct leave)
 *        
 * When in SwiftAct and Home button is pressed,
 *        SwiftAct gets onUserLeaveHint and pause.
 * 
 * When in SwiftAct and Back button is pressed,
 * 		  SwiftAct gets pause and destroy.
 */


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Timer;



public class VodoEitActivity extends ListActivity implements Pausable {
	
	boolean ispaused = false;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	  super.onCreate(savedInstanceState);
    	  
    	  PythonAutoinstallActivity.addAct(this);
    	  
    	  if (!PythonAutoinstallActivity.globalP2Prunning) {
	    		Toast.makeText(getBaseContext(), "Restarting P2P Engine ...", Toast.LENGTH_LONG).show();
  	    	Intent intent = new Intent(getBaseContext(), PythonAutoinstallActivity.class);
    	    	startActivity(intent);
    	  }

//    	  ArrayList<String> videoList = new ArrayList<String>();
//    	  videoList.add((String) getResources().getText(R.string.v1_title));
//    	  videoList.add((String) getResources().getText(R.string.v2_title));
//    	  videoList.add((String) getResources().getText(R.string.v3_title));
    	  
    	  
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
    	    	if (position != 0) {
    	    		if (!PythonAutoinstallActivity.globalP2Prunning) {
        	    		Toast.makeText(getBaseContext(), "Restarting P2P Engine ...", Toast.LENGTH_LONG).show();
    	    	    	Intent intent = new Intent(getBaseContext(), PythonAutoinstallActivity.class);
    	      	    	startActivity(intent);
    	    		}
    	    		else {
		//    	    	Play video
		    	    	Intent intent = new Intent(getBaseContext(), ScriptActivity.class);
		    	    	intent.putExtra("hash", HASHES[position]);
		    	  	    // Arno, 2012-03-22: Default tracker is central tracker, swift now
		    	  	    // has a default local peer which is the DHT.
	//	    	    	intent.putExtra("tracker", "192.16.127.98:20050"); // KTH's tracker
		    	    	intent.putExtra("tracker", "tracker3.p2p-next.org:20050"); // Delft's tracker
		    	    	//intent.putExtra("tracker", "127.0.0.1:9999"); // DHT
		//    	    	intent.putExtra("destination", destination);
		      	    	startActivity(intent);
    	    		}
    	    	}
    	    	else {
    	    		PythonAutoinstallActivity.globalPythonAutoinstallActivity.stopP2PEngine();
    	    	}

    	    	
    	      
    	    }
    	  });
    	} // Arno: If you change the order here, change HASHES[] order as well!
	static final String[] VIDEOS = new String[] {
		">>> STOP P2P Engine <<<",
		"Ken Robinson says schools kill creativity", 
		"Jill Bolte Taylor's stroke of insight", 
		"Pranav Mistry: The thrilling potential of SixthSense technology", 
		"David Gallo shows underwater astonishments", 
		"Pattie Maes and Pranav Mistry demo SixthSense", 
		"Simon Sinek: How great leaders inspire action", 
		"Arthur Benjamin does 'Mathemagic'", 
		"Hans Rosling shows the best stats you've ever seen", 
		"Rob Reid: The $8 billion iPod", 
		"Brene Brown: Listening to shame", 
    	"Susan Cain: The power of introverts", 
		"Vijay Kumar: Robots that fly ... and cooperate", 
    	"I Think Were Alone Now", 
		"L5 Part 1", 
    	"Pioneer One S01E06", 
		"An Honest Man", 
    };
	static final String[] HASHES = new String[] {
		"",
		"2b2fe5f1462e5b7ac4d70fa081e0169160b2d3a6", // SirKenRobinson_2006-480p.ts
		"a004e583a05de39f87ceb7a6eb5608c89415e2f0", // JillBolteTaylor_2008-480p.ts
		"23f99be0f5198efceb4da15fd196106b70216e1e", // PranavMistry_2009I-480p.ts
		"022e1c308d991c653c5e3549fee247cfabc7cf55", // DavidGallo_2007-480p.ts
		"5e615dfdf66953f63be284ae0763f80cb70f0892", // PattieMaes_2009-480p.ts
		"e5478e34e01551a2925fc12f4d28a523b2911af5", // SimonSinek_2009X-480p.ts
		"ec677ff98abe4a0b2b5c122065c080f14ad4a272", // ArthurBenjamin_2005-480p.ts
		"71ccb9341537a9a5738650e6842c97fc88306582", // HansRosling_2006.ts
		"ad2fa2dd346f67583ab327a14d739bdccd44cdb3", // RobReid_2012-480p.ts
		"2dcb65253916e44a791ac7a1a0ee56f51f30086f", // BreneBrown_2012-480p.ts
		"5692014fadcdb33792f0cfa7cc87287bfb8deb91", // SusanCain_2012-480p.ts
		"dbfcbf3e5ca676d1e4ea8a88375ea95ce2f2184f", // VijayKumar_2012-480p.ts
		"db5dabb90a3cbd61a90866a4cc208ae959440ec9", // I.Think.Were.Alone.Now.2008.720p.x264-VODO.ts
		"071d43828a3291defa073008b601aacfb09fd281", // L5.Part.1.2012.Xvid-VODO.ts
		"cbc48a70222e37230bf3f2b3bd84eaef5ae16b41", // Pioneer.One.S01E06.Xvid-VODO.ts
		"3ce3f4a5bb785d5e8eb7bf3f2615e37095eb5170", // An.Honest.Man.Xvid-VODO.ts
	};
      

	  // From Pausable interface
	public boolean isPaused()
	{
		  return ispaused;
	}
	
	
	public void checkAllActPaused()
	{
		Log.w("Swift","Checking VodoActivity" );
		if (PythonAutoinstallActivity.allActPaused() > 0)
		{
			Log.w("Swift","Starting timer" );
			Timer t = new Timer("AllActPausedTimer",true);
			PauseTimer pt = new PauseTimer();
			t.schedule(pt, 2000);
		}
	}

	
	public void onPause()
	{
			super.onPause();
			ispaused = true;

			checkAllActPaused();
	}
		
	public void onResume()
	{
			super.onResume();
			ispaused = false;
			if (!PythonAutoinstallActivity.globalP2Prunning) {
				Toast.makeText(getBaseContext(), "Restarting P2P Engine ...", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getBaseContext(), PythonAutoinstallActivity.class);
				startActivity(intent);
			  }
	}
		
	public void onDestroy()
	{
			super.onDestroy();
				
			PythonAutoinstallActivity.delAct(this);
	}
}

