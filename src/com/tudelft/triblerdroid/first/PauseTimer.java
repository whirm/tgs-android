package com.tudelft.triblerdroid.first;


import android.util.Log;
import java.util.TimerTask;

	public class PauseTimer extends TimerTask
	{
		public void run()
		{
			if (P2PStartActivity.allActPaused() == 2)
			{
				Log.w("SwiftPause","Arno says: HALT ENGINE" );
				P2PStartActivity.globalP2PStartActivity.stopP2PEngine();
			}
			else
				Log.w("SwiftPause","Arno says: NOT HALT ENGINE" );
		}
	};
	