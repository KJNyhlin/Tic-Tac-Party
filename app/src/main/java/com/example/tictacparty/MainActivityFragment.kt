package com.example.tictacparty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivityFragment() : Fragment() {

    lateinit var auth : FirebaseAuth

    override fun onResume() {
        if(!GlobalVariables.loggedIn){
            val intent = Intent(requireActivity(), StartActivity::class.java)
            startActivity(intent)
        }
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mainactivity, container , false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onViewCreated(view, savedInstanceState)

        val playNowButton = view.findViewById<Button>(R.id.playNowButton)
        val matchHistoryButton = view.findViewById<Button>(R.id.matchHistoryButton)
        val challengeAFriendButton = view.findViewById<Button>(R.id.challengeAFriendButton)
        playNowButton.setOnClickListener {
            val matchFragment = MatchMakingFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer,matchFragment, "matchFragment")
            transaction?.commit()
        }
        challengeAFriendButton.setOnClickListener{
            val intent=Intent(context,GameActivity::class.java)
            startActivity(intent)
        }

        val picture = view.findViewById<ImageView>(R.id.imageAvatar)
        if(GlobalVariables.player?.avatarImage!=null) {
            picture.setImageResource(GlobalVariables.player!!.avatarImage)
            Log.d("!!!", "inMainActivity: ${GlobalVariables.player!!.avatarImage}")
        }

        if(GlobalVariables.player?.username != null) {
            Toast.makeText(
                requireContext(),
                "Welcome ${GlobalVariables.player?.username?.capitalize()}",
                Toast.LENGTH_SHORT
            ).show()
        }
        /*f(GlobalVariables.loggedIn == false){
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }*/

    }
}