package com.example.okhttp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_first, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    view.findViewById<Button>(R.id.button_first).setOnClickListener {
      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }
    val textView = view.findViewById<TextView>(R.id.textview_first)
    internetSurfing(textView)
  }

  private fun internetSurfing(textView: TextView) {
    val client = OkHttpClient()

    val request = Request.Builder().url("https://www.baidu.com").build()
    client.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        textView.text = e.toString()
      }

      override fun onResponse(call: Call, response: Response) {
        val result = response.body?.string()
        activity?.runOnUiThread {
          textView.text = result
        }
      }

    })
  }
}