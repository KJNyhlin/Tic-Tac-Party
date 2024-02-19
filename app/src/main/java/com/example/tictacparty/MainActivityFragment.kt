package com.example.tictacparty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class MainActivityFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mainactivity, container , false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
    }
}