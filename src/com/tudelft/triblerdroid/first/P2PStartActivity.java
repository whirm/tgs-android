/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tudelft.triblerdroid.first;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.android_scripting.Constants;
import com.googlecode.android_scripting.facade.ActivityResultFacade;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;

/**
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 */
public class P2PStartActivity extends Activity implements Pausable {


	boolean ispaused = false;
	
	/*
	 * Arno, 2012-03-23: Global admin of activities
	 */
	public static Boolean globalP2Prunning = Boolean.TRUE;
	public static P2PStartActivity  globalP2PStartActivity = null;
	private SwiftService scriptService = null;

	public static Set<Activity>		appSet;
	
	
	public static synchronized void addAct(Activity a)
	{
		if (appSet == null)
			appSet = new HashSet<Activity>();
		
		Log.w("Swift","ADD activity" + a );
		appSet.add(a);
	}
	
	public static synchronized void delAct(Activity a)
	{
		try
		{
			appSet.remove(a);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static synchronized int allActPaused()
	{
		Log.w("Swift","Pausing activitiy set is " + appSet.size() );
		
		
		Iterator<Activity> iter = appSet.iterator();
		boolean oneHasFocus = false;
		while(iter.hasNext()) {

			Activity a = (Activity)iter.next();
			if (a.hasWindowFocus())
				oneHasFocus = true;
		    Pausable p = (Pausable)a; 
		    if (!p.isPaused())
		    {
		    	return 0;
		    }
		    	
		}
		if (!oneHasFocus)
			return 2; // All paused and none have focus
		else
			return 1; // All paused
	}
	
	public static synchronized int numActs()
	{
		return appSet.size();
	}
	
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  globalP2PStartActivity = this;
	  P2PStartActivity.addAct(this);
	  
	  globalP2Prunning = Boolean.TRUE;
	  
	  setContentView(R.layout.p2p);
  }
	
  private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}

  protected void startP2PEngine() {
	  
	/* Arno, 2012-03-05: Moved from onCreate, such that we only launch the
	 * service when Python is installed.
	 */
	Log.w("QMediaPython","prepareUninstallButton");
    if (1==1){//Constants.ACTION_LAUNCH_SCRIPT_FOR_RESULT.equals(getIntent().getAction())) {
    	
      // Arno: layout moved up
      //setTheme(android.R.style.Theme_Dialog);
      //setContentView(R.layout.dialog);
      ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          scriptService = ((SwiftService.LocalBinder) service).getService();
          try {
            RpcReceiverManager manager = scriptService.getRpcReceiverManager();
            ActivityResultFacade resultFacade = manager.getReceiver(ActivityResultFacade.class);
            resultFacade.setActivity(P2PStartActivity.this);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          // Ignore.
        }
      };
//      Raul, 2012-03-28: This creates problems when restarting P2P
//      bindService(new Intent(this, ScriptService.class), connection, Context.BIND_AUTO_CREATE);
      startService(new Intent(this, SwiftService.class));
    } else {
    	
    	
      ScriptApplication application = (ScriptApplication) getApplication();
      if (application.readyToStart()) {
        startService(new Intent(this, SwiftService.class));
      }
      // Arno, 2012-02-15: Hack to keep this activity alive.
      // finish();
    }
//    Raul, 2012-03-26: Autoinstall done, show video list (no need for button) 
    Intent intent = new Intent(getBaseContext(), VideoListActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  
	public void stopP2PEngine()
	{
		P2PStartActivity.globalP2Prunning = Boolean.FALSE;
		stopService(new Intent(getBaseContext(), SwiftService.class));
//		unbindService(scriptService);
		
		// Arno, 2012-03-23: Don't work if called by TimerTask :-(
		// Toast.makeText(getBaseContext(), "P2P Engine DOWN", Toast.LENGTH_LONG).show();
		
		String msg = "KILL_DHT";
		InetAddress IPAddress = null;
		try {
			IPAddress = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		DatagramPacket sendPacket = 
				new DatagramPacket(msg.getBytes(), msg.length(), IPAddress, 9999); 
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		clientSocket.close(); 
	}
	
  
  // From Pausable interface
  public boolean isPaused()
  {
	  return ispaused;
  }
  


	public void checkAllActPaused()
	{
		Log.w("Swift","Checking P2PStartActivityActivity" );
		if (P2PStartActivity.allActPaused() > 0)
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
	Log.w("Swift","P2PStartActivity.onPause" );
	ispaused = true;
	
	checkAllActPaused();
  }

  public void onResume()
  {
	super.onResume();
	Log.w("Swift","P2PStartActivity.onResume" );
	ispaused = false;
  }
  
  public void onStart()
  {
	super.onStart();
	Log.w("Swift","P2PStartActivity.onStart" );
	if (1==1){//!globalP2Prunning) {
		startP2PEngine();
	}
  }
  
  public void onRestart()
  {
	super.onRestart();
	Log.w("Swift","P2PStartActivity.onRestart" );
  }

  public void onDestroy()
  {
		super.onDestroy();
		
		P2PStartActivity.delAct(this);	
  }
}
