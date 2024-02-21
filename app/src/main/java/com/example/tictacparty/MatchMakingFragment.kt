package com.example.tictacparty

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class MatchMakingFragment() : Fragment(){

    var animationSpinning = AnimationDrawable()
    lateinit var spinningWheel : ImageView
    lateinit var searchingOpponent : ImageView
    lateinit var searchingUsername : TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)


        searchingOpponent = view.findViewById<ImageView>(R.id.searchingOpponent)
        spinningWheel = view.findViewById<ImageView>(R.id.spinningWheel)
        searchingUsername = view.findViewById<TextView>(R.id.searchingUsername)


        updateMatchMakingFragment()


        return view
    }

    fun updateMatchMakingFragment(){

        spinningWheel.setBackgroundResource(R.drawable.animation_spinningwheel)
        val animationSpinning = spinningWheel.background as? AnimationDrawable
        animationSpinning?.start()

        if(GlobalVariables.player?.avatarImage!=null) {
            searchingOpponent.setImageResource(GlobalVariables.player!!.avatarImage)
            Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
        }
        //capitalize() - Skriver ut användarnamnet så att första bokstaven blir stor och resten blir små.
        searchingUsername.text = GlobalVariables.player?.username?.capitalize()

    }
}
