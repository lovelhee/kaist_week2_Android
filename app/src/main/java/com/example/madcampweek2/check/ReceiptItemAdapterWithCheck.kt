package com.example.madcampweek2.check

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.network.ReceiptItemDataWithCheck

class ReceiptItemAdapterWithCheck(
    private val items: List<ReceiptItemDataWithCheck>,
    private val onCheckedChange: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<ReceiptItemAdapterWithCheck.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_check, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvMenuName.text = item.itemName
        holder.tvMenuPrice.text = "${item.price} 원"
        holder.checkBox.isChecked = item.checked == 1
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(item.id, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMenuName: TextView = itemView.findViewById(R.id.tvMenuName)
        val tvMenuPrice: TextView = itemView.findViewById(R.id.tvMenuPrice)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}