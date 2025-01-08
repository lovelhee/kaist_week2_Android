package com.example.madcampweek2.start

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.ReceiptItemInStart

class ReceiptItemAdapter(
    private val context: Context,
    private val items: List<ReceiptItemInStart>
) : RecyclerView.Adapter<ReceiptItemAdapter.ReceiptItemViewHolder>() {

    class ReceiptItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMenuName: TextView = view.findViewById(R.id.tvMenuName)
        val tvMenuPrice: TextView = view.findViewById(R.id.tvMenuPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false)
        return ReceiptItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvMenuName.text = item.itemName

        holder.tvMenuPrice.text = if (item.price.endsWith(".00")) {
            item.price.substringBefore(".00") + "원" // .00 제거
        } else {
            item.price + "원"
        }
    }

    override fun getItemCount(): Int = items.size
}