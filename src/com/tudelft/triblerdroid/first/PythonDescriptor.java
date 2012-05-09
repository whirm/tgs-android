/*
 * Copyright (C) 2009 Google Inc.
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

import android.content.Context;

import com.googlecode.android_scripting.interpreter.InterpreterConstants;
import com.googlecode.android_scripting.interpreter.InterpreterUtils;
import com.googlecode.android_scripting.interpreter.Sl4aHostedInterpreter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PythonDescriptor extends Sl4aHostedInterpreter {

  private static final String PYTHON_BIN = "bin/python";
  private static final String ENV_HOME = "PYTHONHOME";
  private static final String ENV_PATH = "PYTHONPATH";
  private static final String ENV_TEMP = "TEMP";
  private static final String ENV_LD = "LD_LIBRARY_PATH";

  @Override
  public String getBaseInstallUrl() { // TODO: Change back to standard path for official release
	  
	// Arno, 2012-03-05: Taken from
	// http://code.google.com/p/python-for-android/source/browse/android/PythonForAndroid/src/com/googlecode/pythonforandroid/PythonDescriptor.java
	// which is the newer?! code for Python4Android. Fork, exec, confusion.
	
	return "file:/mnt/sdcard/python-for-android-files/"; //TODO Raul
//	return "http://python-for-android.googlecode.com/files/";
    // return "http://www.mithril.com.au/android/"; // This is for testing only.
  }

  public String getExtension() {
    return ".py";
  }

  public String getName() {
    return "python";
  }

  public String getNiceName() {
    return "Python 2.6.2";
  }

  public boolean hasInterpreterArchive() {
    return true;
  }

  public boolean hasExtrasArchive() {
    return true;
  }

  public boolean hasScriptsArchive() {
	// Arno, 2012-03-05: Don't DL scripts
    return false;
  }

  public int getVersion() {
	// Arno: In the Python4Android version that is distributed as .apk
	// (http://code.google.com/p/python-for-android/source/browse/android/PythonForAndroid/src/com/googlecode/pythonforandroid/PythonDescriptor.java)
	// the versions available are determined dynamically, by reading the
	// contents of
	// http://python-for-android.googlecode.com/hg/python-build/LATEST_VERSION_*
	//
	// For now, I just hardcode the latest versions here.
    return 16;  
  }

  @Override
  public int getExtrasVersion() {
    return 14;
  }

  @Override
  public int getScriptsVersion() {
    return 13;
  }

  @Override
  public File getBinary(Context context) {
    return new File(getExtrasPath(context), PYTHON_BIN);
  }

  private String getExtrasRoot() {
    return InterpreterConstants.SDCARD_ROOT + getClass().getPackage().getName()
        + InterpreterConstants.INTERPRETER_EXTRAS_ROOT;
  }

  private String getHome(Context context) {
    File file = InterpreterUtils.getInterpreterRoot(context, getName());
    return file.getAbsolutePath();
  }

  public String getExtras() {
    File file = new File(getExtrasRoot(), getName());
    return file.getAbsolutePath();
  }

  private String getTemp() {
    File tmp = new File(getExtrasRoot(), getName() + "/tmp");
    if (!tmp.isDirectory()) {
      tmp.mkdir();
    }
    return tmp.getAbsolutePath();
  }

  @Override
  public Map<String, String> getEnvironmentVariables(Context context) {
    Map<String, String> values = new HashMap<String, String>();
    values.put(ENV_HOME, getHome(context));
    values.put(ENV_LD, getHome(context) + "/lib");
    values.put(ENV_PATH, getExtras() + ":" + getHome(context) + "/lib/python2.6/lib-dynload" + ":"
        + getHome(context) + "/lib/python2.6");
    values.put(ENV_TEMP, getTemp());
    return values;
  }
}
