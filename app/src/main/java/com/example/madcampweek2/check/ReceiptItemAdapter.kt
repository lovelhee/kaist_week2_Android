package com.example.madcampweek2.check

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.network.ReceiptItemData

class ReceiptItemAdapter(private val items: List<ReceiptItemData>, private val onCheckedChange: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<ReceiptItemAdapter.ReceiptItemViewHolder>() {

    private val checkedStates = mutableMapOf<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_check, parent, false)
        return ReceiptItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvMenuName.text = item.itemName
        holder.tvMenuPrice.text = "${item.price} 원"
        val isChecked = checkedStates[item.id] ?: false
        holder.checkBox.isChecked = isChecked
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // 체크 상태 변경 시 상태 저장
            checkedStates[item.id] = isChecked
            onCheckedChange(item.id, isChecked)  // 외부 콜백 호출
        }
    }

    override fun getItemCount(): Int = items.size

    fun setInitialCheckedStates(states: Map<Int, Boolean>) {
        checkedStates.clear()
        checkedStates.putAll(states)
        notifyDataSetChanged()
    }

    class ReceiptItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMenuName: TextView = itemView.findViewById(R.id.tvMenuName)
        val tvMenuPrice: TextView = itemView.findViewById(R.id.tvMenuPrice)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}