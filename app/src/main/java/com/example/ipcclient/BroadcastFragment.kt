package com.example.ipcclient

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ipcclient.databinding.FragmentBroadcastBinding
import java.util.Calendar

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BroadcastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BroadcastFragment : Fragment(), View.OnClickListener{
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentBroadcastBinding.inflate(inflater,container,false)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BroadcastFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BroadcastFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        sendBroadcast()
        showBroadcastTime()
    }

    private fun sendBroadcast() {
        //Define an explicit intent containing the package name of the server and the class name of the receiver so that only a certain application can listen to the broadcast.
        val intent = Intent()
        intent.putExtra(Constants.PACKAGE_NAME, context?.packageName)
        intent.putExtra(Constants.PID, android.os.Process.myPid().toString())
        intent.putExtra(Constants.DATA, binding.edtClientData.text.toString())
        intent.component = ComponentName("com.example.ipcclient.exampleserver","com.example.ipcclient.exampleserver.IPCBroadcastReceiver")
        activity?.applicationContext?.sendBroadcast(intent)
    }
    private fun showBroadcastTime() {
        val cal = Calendar.getInstance()
        val time = "${cal.get(Calendar.HOUR)} : ${cal.get(Calendar.MINUTE)} : ${cal.get(Calendar.SECOND)}"
        binding.linearLayoutClientInfo.visibility = View.VISIBLE
        binding.txtDate.text = time
    }
}