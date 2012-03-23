package com.tudelft.triblerdroid.first;

public class NativeLib {

  static {
    //System.loadLibrary("swift");
	  System.loadLibrary("event");
  }
  
  /** 
   * start swift
   */
  public native String start( String hash, String tracker, String filename );

  /** 
   * swift mainloop. Does not exit till stop is called!
   *
   */
  public native int mainloop();

  /** 
   * stop swift
   * return: success or failure
   */
  public native String stop();

  /**
   * Returns progress string
   */
  public native String httpprogress(String hash);

  /**
   * Returns Hello World string
   */
  public native String hello();
}