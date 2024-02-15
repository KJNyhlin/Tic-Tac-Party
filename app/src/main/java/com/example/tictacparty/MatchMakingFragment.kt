package com.example.tictacparty

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class MatchMakingFragment() : Fragment(){

    var animationSpinning = AnimationDrawable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)


        val imageView = view.findViewById<ImageView>(R.id.spinningWheel)
        imageView.setBackgroundResource(R.drawable.animation_spinningwheel)
        val animationSpinning = imageView.background as? AnimationDrawable
        animationSpinning?.start()


        return view
    }

}
