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
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.googlecode.android_scripting.Constants;
import com.googlecode.android_scripting.facade.ActivityResultFacade;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 */
public class IntroActivity extends PythonInstallIntegration implements Pausable {

	/*
	 * Arno: From Riccardo's original SwiftBeta
	 */
	Button b_continue;
	boolean ispaused = false;
	
	/*
	 * Arno, 2012-03-23: Global admin of activities
	 */
	public static Boolean globalP2Prunning = Boolean.TRUE;
	public static IntroActivity  globalIntroActivity = null;
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

	  globalIntroActivity = this;
	  IntroActivity.addAct(this);
	  
	  globalP2Prunning = Boolean.TRUE;
	  
//	  Raul, 2012-03-09: moved here because pymdht creates files in this directory
	    try
	    {
	  	  // create dir for swift
	    	String swiftFolder = "/swift";
	    	String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	    	File mySwiftFolder = new File(extStorageDirectory + swiftFolder);
	    	mySwiftFolder.mkdir();
	    }
	    catch(Exception e)
	    {
	  	  e.printStackTrace();
	    }

	  // ARNO TEST
	  File pythonBin = new File("/data/data/"+getClass().getPackage().getName()+"/files/python/bin/python");
	  if (pythonBin.exists() && pythonBin.canExecute())
		  setInstalled(true);
	  else
		  setInstalled(false);
	  
	  //SwiftStartDownload();
	  
//	  Raul, 2012-03-08: This is done in PythonInstallIntegration
//	  setContentView(R.layout.video_info);
	  b_continue = (Button) findViewById(R.id.b_continue);
//	  b_continue.setVisibility(View.INVISIBLE);
	  b_continue.setOnClickListener(new OnClickListener() {
		  public void onClick(View v) {
			  
			  Intent intent = new Intent(getBaseContext(), VodoEitActivity.class);
			  startActivity(intent);
		  }  	
	  });

  }
	

  @Override
  protected void prepareUninstallButton() {
	  
	/* Arno, 2012-03-05: Moved from onCreate, such that we only launch the
	 * service when Python is installed.
	 */
	Log.w("QMediaPython","prepareUninstallButton");
    if (Constants.ACTION_LAUNCH_SCRIPT_FOR_RESULT.equals(getIntent().getAction())) {
    	
      // Arno: layout moved up
      //setTheme(android.R.style.Theme_Dialog);
      //setContentView(R.layout.dialog);
      ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          ScriptService scriptService = ((ScriptService.LocalBinder) service).getService();
          try {
            RpcReceiverManager manager = scriptService.getRpcReceiverManager();
            ActivityResultFacade resultFacade = manager.getReceiver(ActivityResultFacade.class);
            resultFacade.setActivity(IntroActivity.this);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          // Ignore.
        }
      };
      bindService(new Intent(this, ScriptService.class), connection, Context.BIND_AUTO_CREATE);
      startService(new Intent(this, ScriptService.class));
    } else {
    	
    	
      ScriptApplication application = (ScriptApplication) getApplication();
      if (application.readyToStart()) {
        startService(new Intent(this, ScriptService.class));
      }
      // Arno, 2012-02-15: Hack to keep this activity alive.
      // finish();
    }
//    Raul, 2012-03-09: This doesn't get executed. Why?
    // instalation done, make button visible
    b_continue = (Button) findViewById(R.id.b_continue);
    b_continue.setVisibility(View.VISIBLE);
  }

  
	public void stopP2PEngine()
	{
		IntroActivity.globalP2Prunning = Boolean.FALSE;
		stopService(new Intent(getBaseContext(), ScriptService.class));
		
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
		Log.w("Swift","Checking IntroActivity" );
		if (IntroActivity.allActPaused() > 0)
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
  }

  public void onDestroy()
  {
		super.onDestroy();
		
		IntroActivity.delAct(this);	
  }
}
