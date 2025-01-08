package com.example.madcampweek2.start

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.ParticipantInStart

class ParticipantAdapter(
    private val context: Context,
    private val participants: List<ParticipantInStart>,
    private val onAlarmClick: (ParticipantInStart) -> Unit,
    private val onAllPaidCheck: (Boolean) -> Unit // 모든 isPaid 상태를 체크하는 콜백
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    class ParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvAlarm: Button = view.findViewById(R.id.btnAlarm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_people_alarm, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = participants[position]

        holder.tvName.text = participant.name
        holder.tvPrice.text = if (participant.amountOfMoney.endsWith(".00")) {
            participant.amountOfMoney.substringBefore(".00") + "원" // .00 제거
        } else {
            participant.amountOfMoney + "원"
        }

        // isPaid 값에 따라 버튼 상태 및 배경색 설정
        if (participant.isPaid == 1) {
            holder.tvAlarm.isEnabled = false // 버튼 비활성화
            holder.tvAlarm.setBackgroundColor(android.graphics.Color.parseColor("#7E7E7E")) // 비활성화 색상
        } else {
            holder.tvAlarm.isEnabled = true // 버튼 활성화
            holder.tvAlarm.setBackgroundColor(context.getColor(R.color.main_purple)) // 기본 배경색
        }

        // 버튼 클릭 리스너 설정
        holder.tvAlarm.setOnClickListener {
            if (participant.isPaid == 0) {
                onAlarmClick(participant) // 알림 버튼 클릭 시 동작 수행
            }
        }
        holder.tvAlarm.setOnClickListener {
            onAlarmClick(participant)
        }
    }

    // 모든 참가자의 isPaid 상태를 확인
    fun checkAllPaid() {
        val allPaid = participants.all { it.isPaid == 1 }
        onAllPaidCheck(allPaid) // 결과를 콜백으로 전달
    }

    override fun getItemCount(): Int = participants.size
}