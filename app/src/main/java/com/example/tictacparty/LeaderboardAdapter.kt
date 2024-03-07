package com.example.tictacparty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(private val leaderboardData: List<Any>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImageView: ImageView = view.findViewById(R.id.profile_image_view)
        val rankingTextView: TextView = view.findViewById(R.id.ranking_text_view)
        val usernameTextView: TextView = view.findViewById(R.id.username_text_view)
        val mmrTextView: TextView = view.findViewById(R.id.mmr_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboardrecycler, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val data = leaderboardData[position]

        if (data is Player) {
            holder.usernameTextView.text = data.username.capitalize()
            holder.mmrTextView.text = data.mmrScore.toString()
            holder.profileImageView.setImageResource(data.avatarImage)
        } else if (data is LeaderboardPlayer) {
            holder.usernameTextView.text = data.username.capitalize()
            holder.mmrTextView.text = data.mmrScore.toString()
            holder.profileImageView.setImageResource(data.avatarImage)
        }

        holder.rankingTextView.text = (position + 1).toString()
    }

    override fun getItemCount() = leaderboardData.size
}