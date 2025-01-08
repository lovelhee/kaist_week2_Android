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
        // 숫자 포맷으로 ".00" 제거
        val priceValue = menuItem.price.toDoubleOrNull() ?: 0.0
        val formattedPrice = if (priceValue == priceValue.toInt().toDouble()) {
            priceValue.toInt().toString() // 정수로 변환
        } else {
            priceValue.toString() // 소수점 유지
        }
        holder.tvMenuPrice.text = formattedPrice
    }

    override fun getItemCount(): Int = menuList.size
}