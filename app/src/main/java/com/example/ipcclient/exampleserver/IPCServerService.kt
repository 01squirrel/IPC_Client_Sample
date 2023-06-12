package com.example.ipcclient.exampleserver

import android.app.Service
import android.content.Intent
import android.os.*
import android.text.TextUtils
import com.example.ipcclient.Constants.CONNECTION_COUNT
import com.example.ipcclient.Constants.DATA
import com.example.ipcclient.Constants.PACKAGE_NAME
import com.example.ipcclient.Constants.PID
import com.example.ipcclient.IIPCExample

class IPCServerService : Service() {

    companion object {
        var connectCount: Int = 0
        const val NOT_SENT = "not set!!"
    }

    //AIDL
    private val binder = object: IIPCExample.Stub() {
        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun getConnectionCount(): Int = connectCount

        override fun setDisplayedValue(packagename: String?, pid: Int, data: String?) {
            val clientData =
                if(data == null || TextUtils.isEmpty(data)) NOT_SENT
                else data
            RecentClient.client = Client(
                packagename ?: NOT_SENT,
                pid.toString(),
                clientData,
                "AIDL"
            )
        }

    }

    //Messenger
    internal inner class IncomingHandler: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val receivedBundle = msg.data
            RecentClient.client = Client(
                receivedBundle.getString(PACKAGE_NAME),
                receivedBundle.getInt(PID).toString(),
                receivedBundle.getString(DATA),
                "Messenger"
            )
            val message = Message.obtain(this@IncomingHandler,0)
            val bundle = Bundle()
            bundle.putInt(CONNECTION_COUNT, connectCount)
            bundle.putInt(PID,Process.myPid())
            msg.data = bundle
            msg.replyTo.send(message)
        }
    }

    private val mMessenger = Messenger(IncomingHandler())
    override fun onBind(intent: Intent?): IBinder? {
        connectCount++
        return when(intent?.action) {
            "aidlexample" -> binder
            "messengerexample" -> mMessenger.binder
                else -> null
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        RecentClient.client = null
        return super.onUnbind(intent)
    }
}