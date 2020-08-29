package com.moamen.icpcassuit

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

public class Connection_Network(_context: Context) {
    var context:Context?=_context
    var connectivity: ConnectivityManager?=null
    var info: NetworkInfo?=null
    public fun Isconnected():Boolean{
        connectivity= context!!.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(connectivity!=null){
            info= connectivity!!.activeNetworkInfo
            if(info!=null){
                if(info!!.state==NetworkInfo.State.CONNECTED){
                    return true
                }
            }
        }
        return false
    }
}