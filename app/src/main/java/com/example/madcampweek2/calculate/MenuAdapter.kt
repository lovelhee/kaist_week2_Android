package com.example.madcampweek2.calculate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcampweek2.R
import com.example.madcampweek2.data.MenuItem

class MenuAdapter(private val menuList: List<MenuItem>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMenuName: TextView = view.findViewById(R.id.tvMenuName)
        val tvMenuPrice: TextView = view.findViewById(R.id.tvMenuPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        holder.tvMenuName.text = menuItem.name
        holder.tvMenuPrice.text = menuItem.price
    }

    override fun getItemCount(): Int = menuList.size
}