package com.example.madcampweek2.makeRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.network.ReceiptItem

class HostMenuAdapter(private val items: List<ReceiptItem>) :
    RecyclerView.Adapter<HostMenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuName: TextView = view.findViewById(R.id.tvMenuName)
        val menuPrice: TextView = view.findViewById(R.id.tvMenuPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]
        holder.menuName.text = "${item.menu} (${item.details})"
        holder.menuPrice.text = item.price
    }

    override fun getItemCount() = items.size
}