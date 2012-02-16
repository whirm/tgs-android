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
   * report progress as a percentage
   * for the moment it's a single download.. so no need for params
   */
  public native int progress();

  /** 
   * stop swift
   * return: success or failure
   */
  public native String stop();
  
  /**
   * Returns Hello World string
   */
  public native String hello();
}