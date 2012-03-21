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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;

import com.tudelft.triblerdroid.first.R;
import com.googlecode.android_scripting.AndroidProxy;
import com.googlecode.android_scripting.BaseApplication;
import com.googlecode.android_scripting.Constants;
import com.googlecode.android_scripting.FeaturedInterpreters;
import com.googlecode.android_scripting.FileUtils;
import com.googlecode.android_scripting.ForegroundService;
import com.googlecode.android_scripting.Log;
import com.googlecode.android_scripting.NotificationIdFactory;
import com.googlecode.android_scripting.ScriptLauncher;
import com.googlecode.android_scripting.interpreter.Interpreter;
import com.googlecode.android_scripting.interpreter.InterpreterConfiguration;
import com.googlecode.android_scripting.interpreter.InterpreterUtils;
import com.googlecode.android_scripting.interpreter.html.HtmlActivityTask;
import com.googlecode.android_scripting.interpreter.html.HtmlInterpreter;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/**
 * A service that allows scripts and the RPC server to run in the background.
 * 
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 * @author Manuel Naranjo (manuel@aircable.net)
 */
public class ScriptService extends ForegroundService {
	private final static int NOTIFICATION_ID = NotificationIdFactory.create();
	private final CountDownLatch mLatch = new CountDownLatch(1);
	private final IBinder mBinder;

	private InterpreterConfiguration mInterpreterConfiguration;
	private RpcReceiverManager mFacadeManager;
    private AndroidProxy mProxy;
    
	public class LocalBinder extends Binder {
		public ScriptService getService() {
			return ScriptService.this;
		}
	}
	
	public ScriptService() {
		super(NOTIFICATION_ID);
		mBinder = new LocalBinder();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInterpreterConfiguration = ((BaseApplication) getApplication())
				.getInterpreterConfiguration();
	}

	@Override
	public void onStart(Intent intent, final int startId) {
		//Arno, 2012-02-16: keep swift part alive when scripting goes wrong 
		try
		{
			doOnStart(intent,startId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(ExceptionInInitializerError e)
		{
			// Arno: When the moons are not properly aligned, 
			// Executing /data/data/com.googlecode.pythonforandroid/files/python/bin/python with arguments [/data/data/com.tudelf
			// throws an 02-16 13:54:41.877 W/dalvikvm(27642): Exception Ljava/lang/UnsatisfiedLinkError;
			// thrown while initializing Lcom/googlecode/android_scripting/Exec;
			//
			// I catch that Error here so swift part stays alive until moons
			// are aligned again.
			e.printStackTrace();
		}
	}
		
	private void doOnStart(Intent intent, final int startId) {
		super.onStart(intent, startId);
		String fileName = Script.getFileName(this);
		
		Log.w("Arno: Looking for interpreter for script " + fileName );
		
		Interpreter interpreter = null;
		for (int i=0; i<10; i++) {
			// Arno, 2012-03-06: Sometimes the interpreter detection stuff
			// doesn't appear to be ready when this is called. Calling it
			// multiple times seems to help?
			
			interpreter = mInterpreterConfiguration
				.getInterpreterForScript(fileName);
		
			/* if (interpreter == null)
				Log.w("Arno: Interpreter not found ");
			else
				Log.w("Arno: Found interpreter, installed is " + interpreter.isInstalled() ); */
		}
		
		if (interpreter == null || !interpreter.isInstalled()) {
			return;
			
			// Arno, 2012-03-05: Doesn't show "Install Python 2.6.2 Yes/No dialog
			// we now download ourselves.
			/* mLatch.countDown();
			if (FeaturedInterpreters.isSupported(fileName)) {
				Intent i = new Intent(this, DialogActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(Constants.EXTRA_SCRIPT_PATH, fileName);
				startActivity(i);
			} else {
				Log
						.e(this, "Cannot find an interpreter for script "
								+ fileName);
			}
			stopSelf(startId);
			return; */
		}

		// Copies script to internal memory.
		fileName = InterpreterUtils.getInterpreterRoot(this).getAbsolutePath()
				+ "/" + fileName;
		File script = new File(fileName);
		// TODO(raaar): Check size here!
		if (!script.exists()) {
			script = FileUtils.copyFromStream(fileName, getResources()
					.openRawResource(Script.ID));
		}
		copyResourcesToLocal(); // Copy all resources

		if (Script.getFileExtension(this)
				.equals(HtmlInterpreter.HTML_EXTENSION)) {
			HtmlActivityTask htmlTask = ScriptLauncher.launchHtmlScript(script,
					this, intent, mInterpreterConfiguration);
			mFacadeManager = htmlTask.getRpcReceiverManager();
			mLatch.countDown();
			stopSelf(startId);
		} else {
			mProxy = new AndroidProxy(this, null, true);
			mProxy.startLocal();
			mLatch.countDown();
//			2012-03-20, Raul: this line crashes
//			03-20 13:25:23.815: E/sl4a.StreamGobbler:108(3875): java.io.FileNotFoundException: /mnt/sdcard/sl4a/script.py.log: open failed: ENOENT (No such file or directory)

			ScriptLauncher.launchScript(script, mInterpreterConfiguration,
					mProxy, new Runnable() {
						@Override
						public void run() {
							mProxy.shutdown();
							stopSelf(startId);
						}
					});
		}
	}

	RpcReceiverManager getRpcReceiverManager() throws InterruptedException {
		mLatch.await();
		if (mFacadeManager==null) { // Facade manage may not be available on startup.
		mFacadeManager = mProxy.getRpcReceiverManagerFactory()
		.getRpcReceiverManagers().get(0);
		}
		return mFacadeManager;
	}

	@Override
	protected Notification createNotification() {
	    Notification notification =
	        new Notification(R.drawable.vodo_logo, this.getString(R.string.loading), System.currentTimeMillis());
	    // This contentIntent is a noop.
	    PendingIntent contentIntent = PendingIntent.getService(this, 0, new Intent(), 0);
	    notification.setLatestEventInfo(this, this.getString(R.string.app_name), this.getString(R.string.loading), contentIntent);
	    notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}

	private boolean needsToBeUpdated(String filename, InputStream content) {
		File script = new File(filename);
		FileInputStream fin;
		Log.d("Checking if " + filename + " exists");

		if (!script.exists()) {
			Log.d("not found");
			return true;
		}

		Log.d("Comparing file with content");
		try {
			fin = new FileInputStream(filename);
			int c;
			while ((c = fin.read()) != -1) {
				if (c != content.read()) {
					Log.d("Something changed replacing");
					return true;
				}
			}
		} catch (Exception e) {
			Log.d("Something failed during comparing");
			Log.e(e);
			return true;
		}
		Log.d("No need to update " + filename);
		return false;
	}

	private void copyResourcesToLocal() {
		String name, sFileName;
		InputStream content;
		R.raw a = new R.raw();
		java.lang.reflect.Field[] t = R.raw.class.getFields();
		Resources resources = getResources();
		for (int i = 0; i < t.length; i++) {
			try {
				name = resources.getText(t[i].getInt(a)).toString();
				sFileName = name.substring(name.lastIndexOf('/') + 1, name
						.length());
				content = getResources().openRawResource(t[i].getInt(a));

				// Copies script to internal memory only if changes were made
				sFileName = InterpreterUtils.getInterpreterRoot(this)
						.getAbsolutePath()
						+ "/" + sFileName;
				if (needsToBeUpdated(sFileName, content)) {
					Log.d("Copying from stream " + sFileName);
					content.reset();
					FileUtils.copyFromStream(sFileName, content);
				}
				FileUtils.chmod(new File(sFileName), 0755);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
