package com.example.blueleaf.contentsList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef

class ContentRVAdapter(val context : Context,
                       val items:MutableList<ContentModel>,
                       val keyList : MutableList<String>,
                       val bookmarkIdList : MutableList<String>)
    : RecyclerView.Adapter<ContentRVAdapter.ViewHolder>() {
    // content_rv_item.xml에 viewholder에 넣는다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item,parent,false)
        return ViewHolder(view) // 뷰홀더에 conent_rv_item.xml 바인딩
    }
    // 아이템의 요소들을 하나 하나 뷰홀더에 할당한다.
    override fun onBindViewHolder(holder: ContentRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position], keyList[position]) //22 키를 하나 하나 씩 뽑아냄
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        // item은 ContentModel, key는 FB 키값
        fun bindItems(item:ContentModel, key:String){

            // 아이템 클릭 시 ContentShowActivity로 이동
            // 이동하면서 item의 weburl 정보 전달
            itemView.setOnClickListener() {
                val intent = Intent(context, ContentShowActivity::class.java)
                itemView.context.startActivity(Intent(context, ContentShowActivity::class.java))
                intent.putExtra("url",item.webUrl)
                itemView.context.startActivity(intent)
            }

            // content_rv_item 영역의 제목, 이미지, 북마크 요소를 가져옴
            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            val imageViewArea = itemView.findViewById<ImageView>(R.id.imageArea)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmarkArea)
            val infoTitle = itemView.findViewById<TextView>(R.id.infoTitle)

            // 지금 item 키값이 bookmarkIdList에 포함?
            // 포함되어 있다면 북마크 이미지 영역의 이미지를 bookmark_color로 바꾼다.
            if(bookmarkIdList.contains(key)) {
                bookmarkArea.setImageResource(R.drawable.bookmark_color)
            }
            // 표시되어 있지 않다면 북마크 이미지 영역의 이미지를 bookmark_white로 바꾼다.
            else {
                bookmarkArea.setImageResource(R.drawable.bookmark_white)
            }

            // 북마크 영역의  클릭 리스너
            bookmarkArea.setOnClickListener {
                Log.d("ContentRVAdapter", FBAuth.getUid())
                Toast.makeText(context, key, Toast.LENGTH_LONG).show()

                // 만약 지금 클릭된 아이템의 키값이 북마크 리스트에 포함되어 있다면 그 key를 제거해라
                if(bookmarkIdList.contains(key)) {
                    // 북마크가 있을 때 삭제
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(key)
                        .removeValue()

                } else {
                    // 북마크가 추가
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(key)
                        .setValue(BookmarkModel(true))

                }

            }

            // 이미지 영역에 item.image_url 할당
            contentTitle.text = item.title
            Glide.with(context)
                .load(item.imageUrl)
                .into(imageViewArea)

        }
    }
}