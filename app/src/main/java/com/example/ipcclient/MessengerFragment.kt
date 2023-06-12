package com.example.ipcclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.provider.ContactsContract.Contacts.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ipcclient.Constants.CONNECTION_COUNT
import com.example.ipcclient.Constants.PID
import com.example.ipcclient.databinding.FragmentMessengerBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessengerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessengerFragment : Fragment(),View.OnClickListener,ServiceConnection{
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentMessengerBinding? = null
    private val viewBinding get() = _binding!!
    private var isBound = false
    // Messenger on the server
    private var serverMessenger: Messenger? = null

    // Messenger on the client
    private var clientMessenger: Messenger? = null

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
        // Inflate the layout for this fragment
        _binding = FragmentMessengerBinding.inflate(inflater,container,false)
        return viewBinding.root
    }

    // Handle messages from the remote service
    private val handler: Handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            viewBinding.linearLayoutClientInfo.visibility = View.VISIBLE
            viewBinding.btnConnect.text = getString(R.string.disconnect)
            viewBinding.txtServerPid.text = bundle.getInt(PID).toString()
            viewBinding.txtServerConnectionCount.text = bundle.getInt(CONNECTION_COUNT).toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnConnect.setOnClickListener(this)
    }

    override fun onDestroyView() {
        doUnBindService()
        super.onDestroyView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessengerFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MessengerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        if (isBound) {
            doUnBindService()
        } else {
            doBindService()
        }
    }

    private fun doBindService() {
        clientMessenger = Messenger(handler)
        Intent("messengerexample").also { intent ->
            intent.`package` = "com.example.ipcclient"
            activity?.applicationContext?.bindService(intent,this,Context.BIND_AUTO_CREATE)
        }
        isBound = true
    }

    private fun doUnBindService() {
        if (isBound) {
            activity?.applicationContext?.unbindService(this)
            isBound = false
        }
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        serverMessenger = Messenger(p1)
        Toast.makeText(requireContext(),"SERVICE CONNECT",Toast.LENGTH_SHORT).show()
        // Ready to send messages to remote service
        sendMessageToServer()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        clearUI()
        serverMessenger = null
    }

    private fun sendMessageToServer() {
        if(!isBound) return
        val message = Message.obtain(handler)
        val bundle = Bundle()
        bundle.putString(Constants.DATA,viewBinding.edtClientData.text.toString())
        bundle.putString(Constants.PACKAGE_NAME,context?.packageName)
        bundle.putInt(PID,Process.myPid())
        message.data = bundle
        message.replyTo = clientMessenger // we offer our Messenger object for communication to be two-way
        try {
            serverMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
//        finally {
//            message.recycle()
//        }
    }
    private fun clearUI(){
        viewBinding.txtServerPid.text = ""
        viewBinding.txtServerConnectionCount.text = ""
        viewBinding.btnConnect.text = getString(R.string.connect)
        viewBinding.linearLayoutClientInfo.visibility = View.INVISIBLE
    }
}