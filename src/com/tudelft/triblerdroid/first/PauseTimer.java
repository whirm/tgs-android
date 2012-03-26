package com.tudelft.triblerdroid.first;


import android.util.Log;
import java.util.TimerTask;

	public class PauseTimer extends TimerTask
	{
		public void run()
		{
			if (PythonAutoinstallActivity.allActPaused() == 2)
			{
				Log.w("SwiftPause","Arno says: HALT ENGINE" );
				PythonAutoinstallActivity.globalPythonAutoinstallActivity.stopP2PEngine();
			}
			else
				Log.w("SwiftPause","Arno says: NOT HALT ENGINE" );
		}
	};
	