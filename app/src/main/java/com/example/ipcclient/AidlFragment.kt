package com.example.ipcclient

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ipcclient.databinding.FragmentAidlBinding
import java.text.SimpleDateFormat
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * IPC uses AIDL.
 * A simple [Fragment] subclass.
 * Use the [AidlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AidlFragment : Fragment(), ServiceConnection, View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAidlBinding? = null
    private val binding get() = _binding!!
    var iRemoteServe: IIPCExample? = null
    private var connected = false
    private var manager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAidlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
        manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        connected = if (connected) {
            disconnectedToRemoteService()
            binding.txtServerPid.text = ""
            binding.txtServerConnectionCount.text = ""
            binding.btnConnect.text = getString(R.string.connect)
            binding.linearLayoutClientInfo.visibility = View.INVISIBLE
            false
        } else {
            connectToRemoteService()
            binding.linearLayoutClientInfo.visibility = View.VISIBLE
            binding.btnConnect.text = getString(R.string.disconnect)
            true
        }
    }

    private fun connectToRemoteService() {
        val intent = Intent("aidlexample")
        val pack = IIPCExample::class.java.`package`
        pack?.let {
            intent.setPackage(it.name)
            activity?.applicationContext?.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    private fun disconnectedToRemoteService() {
        if (!connected) {
            activity?.applicationContext?.unbindService(this)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AidlFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AidlFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iRemoteServe = IIPCExample.Stub.asInterface(service)
        binding.txtServerPid.text = iRemoteServe?.pid.toString()
        binding.txtServerConnectionCount.text = iRemoteServe?.connectionCount.toString()
        iRemoteServe?.setDisplayedValue(
            context?.packageName,
            android.os.Process.myPid(),
            binding.edtClientData.text.toString()
        )
        connected = true
        context?.let { makeStatusNotification("Service connected success!", it) }
//        val notice = Notification.Builder(context)
//            .setContentTitle("IPC AIDL")
//            .setContentText("Service connected success!")
//            .setWhen(System.currentTimeMillis())
//            .build()
//        manager?.notify(1,notice)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Toast.makeText(context, "IPC server has disconnected expectedly ", Toast.LENGTH_SHORT)
            .show()
        iRemoteServe = null
        connected = false
    }
    private fun makeStatusNotification(message: String, context:Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = "SERVICE NOTICE"
            val description = "Shows notifications whenever work starts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("VERBOSE_NOTIFICATION",name,importance)
            val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channel.description = description
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context,"VERBOSE_NOTIFICATION")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Service Starting")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0))
        NotificationManagerCompat.from(context).notify(1,notification.build())
    }
}