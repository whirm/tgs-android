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

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.googlecode.android_scripting.Constants;
import com.googlecode.android_scripting.facade.ActivityResultFacade;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;

import java.io.File;

/**
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 */
public class IntroActivity extends PythonInstallIntegration {

	/*
	 * Arno: From Riccardo's original SwiftBeta
	 */
	NativeLib nativelib = null;
//	protected TextView _text;
//    protected SwiftMainThread _swiftMainThread;
    protected StatsTask _statsTask;
	private VideoView mVideoView = null;
	protected ProgressDialog _dialog;
    protected Integer _seqCompInt;

	boolean inmainloop = false;
	
	Button b_continue;
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

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
  }

	
    
    
	/**
	* sub-class of AsyncTask. Retrieves stats from Swift via JNI and
	* updates the progress dialog.
	*/
	private class StatsTask extends AsyncTask<String, Integer, String> {
		
	  protected String doInBackground(String... args) {
	  	
	  	String ret = "hello";
	  	if (args.length != 3) {
	  		ret = "Received wrong number of parameters during initialization!";
	  	}
	  	else {
	  		try {
	
	  			NativeLib nativelib =  new NativeLib();
	  			mVideoView = (VideoView) findViewById(R.id.surface_view);
	  			boolean play = false, pause=false;
	  			while(true) {
	  				String progstr = nativelib.hello();
	  				String[] elems = progstr.split("/");
	  				long seqcomp = Long.parseLong(elems[0]);
	  				long asize = Long.parseLong(elems[1]);
	
	  				if (asize == 0)
	  					_dialog.setMax(1024);
	  				else
	  					_dialog.setMax((int)(asize/1024));
	  				
	  				_seqCompInt = new Integer((int)(seqcomp/1024));
	  				
	  				Log.w("SwiftStats", "SeqComp   " + seqcomp );
	  				
	  	    		runOnUiThread(new Runnable(){
	  	    			public void run() {
	          				_dialog.setProgress(_seqCompInt.intValue() );
	
	  	    			}
	  	    			
	  	    		});
	
	  				if (asize > 0 && seqcomp == asize)
	  				{
	  					Log.w("SwiftStats", "*** COMPLETE, STOP MONITOR ***");
	  					break;
	  				}
	  	    		
						Thread.sleep( 1000 );
	  			}
	  		}
	  		catch (Exception e ) {
	  			//System.out.println("Stacktrace "+e.toString());
	  			e.printStackTrace();
	  			ret = "error occurred during initialization!";
	  		}
	  	}
	      return ret;
	  }
	}

	

}
