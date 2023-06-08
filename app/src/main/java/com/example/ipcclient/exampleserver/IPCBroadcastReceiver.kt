package com.example.ipcclient.exampleserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ipcclient.Constants

class IPCBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        RecentClient.client = Client(
            p1?.getStringExtra(Constants.PACKAGE_NAME),
            p1?.getStringExtra(Constants.PID).toString(),
            p1?.getStringExtra(Constants.DATA),
            "Broadcast"
        )
    }
}