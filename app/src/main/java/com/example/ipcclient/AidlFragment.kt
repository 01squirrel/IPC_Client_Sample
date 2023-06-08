package com.example.ipcclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ipcclient.databinding.FragmentAidlBinding

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
            intent.setPackage(pack.name)
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
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Toast.makeText(context, "IPC server has disconnected expectedly ", Toast.LENGTH_SHORT)
            .show()
        iRemoteServe = null
        connected = false
    }
}