
package com.tudelft.triblerdroid.first;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.util.Log;

import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.FileUtils;
import com.googlecode.android_scripting.InterpreterInstaller;
import com.googlecode.android_scripting.InterpreterUninstaller;
import com.googlecode.android_scripting.activity.Main;
import com.googlecode.android_scripting.exception.Sl4aException;
import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;

import java.io.File;

public class PythonInstallIntegration extends Main {
	
	  Button mButtonModules;
	  File mDownloads;

	  private Dialog mDialog;
	  protected String mModule;
	  private CharSequence[] mList;
	  private ProgressDialog mProgress;
	  private boolean mPromptResult;
	  private Button mButtonBrowse;
	  private File mFrom;
	  private File mSoPath;
	  private File mPythonPath;

	  
	  
	  @Override
	  protected void prepareInstallButton() {
		  
		  String pythonFolder = PythonInstallIntegration.class.getPackage().getName();
		  //String pythonFolder = "com.tudelft.triblerdroid.first"; // FAXME
		  String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		  File myPythonFolder = new File(extStorageDirectory + pythonFolder);
		  myPythonFolder.mkdir();
		  Log.w("QMediaPython","Made directory for extracted Python");
		  
		  // Arno, 2012-03-06: If installation fails on a testing phone,
		  // you may need to delete /mnt/com.tudelft.triblerdroid.first
		  // which is where the files are extracted. The actual working
		  // Python is installed in 
		  // /data/data/com.tudelft.triblerdroid.first/files/python/bin/python
		  
		  install();
	  }

	  /*
	   *  Arno: From PythonMain.java
	   */
	  @Override
	  protected InterpreterDescriptor getDescriptor() {
	    return new PythonDescriptor();
	  }

	  @Override
	  protected InterpreterInstaller getInterpreterInstaller(InterpreterDescriptor descriptor,
	      Context context, AsyncTaskListener<Boolean> listener) throws Sl4aException {
	    return new PythonInstaller(descriptor, context, listener);
	  }

	  @Override
	  protected InterpreterUninstaller getInterpreterUninstaller(InterpreterDescriptor descriptor,
	      Context context, AsyncTaskListener<Boolean> listener) throws Sl4aException {
	    return new PythonUninstaller(descriptor, context, listener);
	  }

	  @Override
	  protected void initializeViews() {
		  
		  Log.w("QMediaPython","initializeViews");
	      // Arno, 2012-02-15: Need to figure out these Intents/launch modes,
	      // for now, hack an Activity with the Python running as a ForegroundService
	      setTheme(android.R.style.Theme_Light);
	      setContentView(R.layout.main);
		  
	  }	  
}