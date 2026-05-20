package com.studdy.mystuddybuddy.presentation.screens.home.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studdy.mystudybuddy.R
import com.studdy.mystuddybuddy.presentation.home.DashboardItem
import com.studdy.mystudybuddy.databinding.ItemDashboardBinding

class DashboardAdapter(
    private val menuList: List<DashboardItem>,
    private val onItemClick: (DashboardItem) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemDashboardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemDashboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = menuList[position]

        holder.binding.imgMenu.setImageResource(
            item.icon
        )

        holder.binding.tvTitle.text =
            item.title

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}