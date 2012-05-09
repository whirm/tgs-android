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
public class PythonAutoinstallActivity extends PythonInstallIntegration {


	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  
	  setContentView(R.layout.pythonautoinstall);

//	  Raul, 2012-03-09: moved here because pymdht creates files in this directory
	  // create dir for swift
	  String swiftFolder = "/swift";
	  String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	  try
	  {
		  File mySwiftFolder = new File(extStorageDirectory + swiftFolder);
		  mySwiftFolder.mkdir();
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }

	  // ARNO TEST
	  File pythonBin = new File("/data/data/"+getClass().getPackage().getName()+"/files/python/bin/python");
	  if (pythonBin.exists() && pythonBin.canExecute()){
		  setInstalled(true);
	  }
	  else{
		  String pythonPath = extStorageDirectory + "/python-for-android-files/";
		  try
		  {
			  File myPythonFolder =  new File(pythonPath);
			  myPythonFolder.mkdir();
			  
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  Log.w("Swift", "Copy Python interpreter to sdcard");
		  String[] filenames = {"python_extras_r14.zip", "python_r16.zip"};
		  AssetManager assetManager = getAssets();
		  InputStream in = null;
		  OutputStream out = null;
		  for(String filename : filenames) {
			  try {
				  in = assetManager.open(filename);
				  out = new FileOutputStream(pythonPath + filename);
				  copyFile(in, out);
				  in.close();
				  out.close();
			  }catch(Exception e) {
				  Log.e("Swift", e.getMessage());
		      }    
			  
		  }

		  setInstalled(false);
	  }
  }
	
  private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
  @Override
  protected void prepareUninstallButton() {
	  
	/* Arno, 2012-03-05: Moved from onCreate, such that we only launch the
	 * service when Python is installed.
	 */
	Log.w("QMediaPython","prepareUninstallButton");
//    Raul, 2012-03-26: Autoinstall done, show video list (no need for button) 
    Intent intent = new Intent(getBaseContext(), P2PStartActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
  
  public void onPause()
  {
	super.onPause();
	Log.w("Swift","PythonAutoinstallActivity.onPause" );
  }

  public void onResume()
  {
	super.onResume();
	Log.w("Swift","PythonAutoinstallActivity.onResume" );
  }
  
  public void onStart()
  {
	super.onStart();
	Log.w("Swift","PythonAutoinstallActivity.onStart" );
  }
  
  public void onRestart()
  {
	super.onRestart();
	Log.w("Swift","PythonAutoinstallActivity.onRestart" );
  }

  public void onDestroy()
  {
		super.onDestroy();
  }
}
