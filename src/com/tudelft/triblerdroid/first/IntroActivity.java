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

	
    
}
