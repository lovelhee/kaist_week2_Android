package com.example.madcampweek2.calculate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.ParticipantInMenu

class ForgotAdapter(private val participants: List<ParticipantInMenu>) :
    RecyclerView.Adapter<ForgotAdapter.ForgotViewHolder>() {

    inner class ForgotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val rvMenu: RecyclerView = view.findViewById(R.id.rvMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForgotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_people_forgot, parent, false)
        return ForgotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForgotViewHolder, position: Int) {
        val participant = participants[position]
        holder.tvName.text = participant.name

        // 메뉴 RecyclerView 설정
        holder.rvMenu.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvMenu.adapter = MenuAdapter(participant.items)
    }

    override fun getItemCount(): Int = participants.size
}