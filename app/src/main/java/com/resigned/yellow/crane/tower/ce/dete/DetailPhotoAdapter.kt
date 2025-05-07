package com.resigned.yellow.crane.tower.ce.dete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.resigned.yellow.crane.tower.R


class DetailPhotoAdapter(private val photoIcons: List<Int>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<DetailPhotoAdapter.ViewHolder>() {

    private var selectedPosition = -1
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgIcon)

        init {
            itemView.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val iconResId = photoIcons[position]
        holder.imageView.setImageResource(iconResId)

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.bg_4_2)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_4)
        }
    }

    override fun getItemCount(): Int = photoIcons.size
}
