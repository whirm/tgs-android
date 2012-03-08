package com.tudelft.triblerdroid.first;

import android.content.UriMatcher;
import android.util.Log;

import com.googlecode.android_scripting.interpreter.InterpreterConstants;
import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;
import com.googlecode.android_scripting.interpreter.InterpreterProvider;

public class PythonProvider extends InterpreterProvider {
  @Override
  protected InterpreterDescriptor getDescriptor() {
    return new PythonDescriptor();
  }
}
