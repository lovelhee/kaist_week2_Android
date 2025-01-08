package com.example.madcampweek2.calculate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.ParticipantInMenu

class PeopleAdapter(private val participants: List<ParticipantInMenu>) :
    RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    inner class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val rvMenu: RecyclerView = view.findViewById(R.id.rvMenu)
        val total: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_name_check, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val participant = participants[position]
        holder.tvName.text = participant.name
        // 총 가격에서 ".00" 제거
        val formattedTotal = if (participant.total.endsWith(".00")) {
            participant.total.substringBefore(".00") // ".00" 이전의 값만 가져옴
        } else {
            participant.total // ".00"이 없으면 그대로 사용
        }
        holder.total.text = formattedTotal

        // 메뉴 RecyclerView 설정
        holder.rvMenu.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvMenu.adapter = MenuAdapter(participant.items)
    }

    override fun getItemCount(): Int = participants.size
}