package com.example.blueleaf.contentsList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blueleaf.R

class ContentRVAdapter(val context : Context, val items:MutableList<ContentModel>): RecyclerView.Adapter<ContentRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view:View, position: Int)
    }

    var itemClick:ItemClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentRVAdapter.ViewHolder, position: Int) {
        if(itemClick != null) {
            holder.itemView.setOnClickListener{v->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        fun bindItems(item:ContentModel){
            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            val imageViewArea = itemView.findViewById<ImageView>(R.id.imageArea)

            contentTitle.text = item.title
            Glide.with(context)
                .load(item.imageUrl)
                .into(imageViewArea)

        }
    }
}