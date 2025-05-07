package com.resigned.yellow.crane.tower.ce.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.resigned.yellow.crane.tower.R

class ItemAdapter(
    var context: Context,
    var itemList: List<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private val glideOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.icon_wall_0)
        .error(R.drawable.icon_wall_0)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: AppCompatImageView = itemView.findViewById(R.id.recy_phot)
    }
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(context).clear(holder.imageView) // 清理 Glide 的加载任务
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wall, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(itemList[position])
            .apply(glideOptions)
            .thumbnail(0.1f)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            onItemClick(itemList[position])
        }
    }

    override fun getItemCount(): Int = itemList.size
}
