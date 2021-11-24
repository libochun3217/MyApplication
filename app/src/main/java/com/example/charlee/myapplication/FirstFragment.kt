package com.example.charlee.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.TextDelegate
import com.example.myapplication.R

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

    val lottieView = view.findViewById<LottieAnimationView>(R.id.lottie_view)
//    lottieView.setAnimationFromUrl("https://assets3.lottiefiles.com/packages/lf20_xtgwlvho.json")
    val files = context?.assets?.list("tt.json")
    lottieView.setAnimation("tt.json")
    lottieView.repeatCount = LottieDrawable.INFINITE
    val textDelegate = TextDelegate(lottieView)
    textDelegate.setText("NAME", "fucking")
    lottieView.setTextDelegate(textDelegate)
    lottieView.playAnimation()
  }
}