package com.example.blueleaf.contentsList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blueleaf.R
import com.example.blueleaf.utils.FBAuth
import com.example.blueleaf.utils.FBRef

class ContentRVAdapter(val context : Context,
                       val items:MutableList<ContentModel>,
                       val keyList : MutableList<String>,
                       val bookmarkIdList : MutableList<String>,
                       var category:String)
    : RecyclerView.Adapter<ContentRVAdapter.ViewHolder>() {
    // content_rv_item.xml에 viewholder에 넣는다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item,parent,false)
        return ViewHolder(view) // 뷰홀더에 conent_rv_item.xml 바인딩
    }
    // 아이템의 요소들을 하나 하나 뷰홀더에 할당한다.
    override fun onBindViewHolder(holder: ContentRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position], keyList[position],category,position)
        // keyList는 컨턴츠의 key값을 담고있음. position은 인덱스, 그래서 bindItems가 받는 key는 각 컨텐츠의 key값이라고 생각하면 됨
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun linearSearchForBookMark(key:String, myList:MutableList<String>):Boolean {
        for (i in myList){
            if(key.equals(i)) {
                return true
            }
        }
        return false
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        // item은 ContentModel, key는 FB 키값
        // keyList는 컨턴츠의 key값을 담고있음. position은 인덱스, 그래서 bindItems가 받는 key는 각 컨텐츠의 key값이라고 생각하면 됨
        fun bindItems(item:ContentModel, key:String,category: String,position: Int){

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
            val scoreArea = itemView.findViewById<TextView>(R.id.scoreArea)
            var recommendArea = itemView.findViewById<ImageView>(R.id.recommendArea)
            val categories:MutableList<String> = mutableListOf("category1","category2","category3","category4","category5","category6")
            var isMarked:Boolean = false
            // 지금 item 키값이 bookmarkIdList에 포함?
            // 포함되어 있다면 북마크 이미지 영역의 이미지를 bookmark_color로 바꾼다.
            // 표시되어 있지 않다면 북마크 이미지 영역의 이미지를 bookmark_white로 바꾼다.

            if(linearSearchForBookMark(key,bookmarkIdList)){
                bookmarkArea.setImageResource(R.drawable.light_start)
            } else{
                bookmarkArea.setImageResource(R.drawable.star)
            }
            isMarked=false;

            recommendArea.setOnClickListener{
                // Toast.makeText(context, key, Toast.LENGTH_LONG).show()
                linearSearchForBookMark(category,categories)

                if(category == "category1") {
                    FBRef.category1.child(key).child("score").setValue(item.score+1)
                    items[position].score += 1
                } else if(category == "category2") {
                    FBRef.category2.child(key).child("score").setValue(item.score+1)
                    items[position].score += 1
                } else if(category == "category3") {
                    FBRef.category3.child(key).child("score").setValue(item.score+1)
                    items[position].score += 1
                } else if(category == "category4") {
                    FBRef.category4.child(key).child("score").setValue(item.score+1)
                    items[position].score += 1
                } else if(category == "category5") {
                    FBRef.category5.child(key).child("score").setValue(item.score+1)
                    items[position].score += 1
                } else if(category == "category6") {
                    FBRef.category6.child(key).child("score").setValue(item.score+1)
                    items[position].score += 1
                }

            }

            // 북마크 영역의 클릭 이벤트 리 스너
            bookmarkArea.setOnClickListener {
                Log.d("ContentRVAdapter", FBAuth.getUid())
                // Toast.makeText(context, key, Toast.LENGTH_LONG).show() // 선택된 아이템의 키를 toast

                // 만약 지금 클릭된 아이템의 키값이 북마크 리스트에 포함되어 있다면 그 key를 제거해라
                // bookmarkIdList.contains(key)

                if(linearSearchForBookMark(key,bookmarkIdList)) {
                    // 북마크가 있을 때 삭제
                    // bookmarkRef -> bookmark_list
                    // 계획환 fb 구조를 명확히 따름.
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(key)
                        .removeValue()

                } else {
                    // 북마크가 추가 setValue
                    FBRef.bookmarkRef
                        .child(FBAuth.getUid())
                        .child(key)
                        .setValue(BookmarkModel(true))

                }

            }
            // 이미지 영역에 item.image_url 할당
            contentTitle.text = item.title
            scoreArea.text = item.score.toString()
            Glide.with(context)
                .load(item.imageUrl)
                .into(imageViewArea)

        }
    }
}