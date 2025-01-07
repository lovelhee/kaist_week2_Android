package com.example.madcampweek2.check

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.network.ReceiptItemData

class ReceiptItemAdapter(private val items: List<ReceiptItemData>) :
    RecyclerView.Adapter<ReceiptItemAdapter.ReceiptItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_check, parent, false)
        return ReceiptItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvMenuName.text = item.itemName
        holder.tvMenuPrice.text = "${item.price} 원"
        holder.checkBox.isChecked = false
    }

    override fun getItemCount(): Int = items.size

    class ReceiptItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMenuName: TextView = itemView.findViewById(R.id.tvMenuName)
        val tvMenuPrice: TextView = itemView.findViewById(R.id.tvMenuPrice)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}