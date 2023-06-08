// IIPCExample.aidl
package com.example.ipcclient;

// Declare any non-default types here with import statements

interface IIPCExample {
    int getPid();

    int getConnectionCount();

    void setDisplayedValue(String packagename,int pid,String data);
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     * int long boolean float double String
     */
}