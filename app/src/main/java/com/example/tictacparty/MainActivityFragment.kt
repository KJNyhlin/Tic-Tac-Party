package com.example.tictacparty

import android.content.Intent
import android.os.Bundle
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

    /*override fun onResume() {
        super.onResume()
        if (!GlobalVariables.loggedIn) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Finish the activity to prevent going back to it
        }
    }*/
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

        val picture = view.findViewById<ImageView>(R.id.imageAvatar)
        picture.setOnClickListener {
            logout()
        }

        Toast.makeText(requireContext(), "${GlobalVariables.player?.username.toString()}", Toast.LENGTH_SHORT).show()

        /*f(GlobalVariables.loggedIn == false){
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }*/

    }
    fun logout(){
        auth.signOut()
        GlobalVariables.loggedInUser = ""
        GlobalVariables.loggedIn = false
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}