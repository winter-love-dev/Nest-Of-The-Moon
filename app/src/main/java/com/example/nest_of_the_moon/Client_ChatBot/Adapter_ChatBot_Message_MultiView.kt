package com.example.nest_of_the_moon.Client_ChatBot

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.chatList
import com.example.nest_of_the_moon.R
import com.example.recycler_view_multi_view_test.Item_chatBot_Message

class Adapter_ChatBot_Message_MultiView(val context: Context?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var TAG = "Adapter_ChatBot_Message_MultiView"

//    private var totalTypes = list.size

    // getItemViewType의 리턴값 Int가 viewType으로 넘어온다.
    // viewType으로 넘어오는 값에 따라 viewHolder를 알맞게 처리해주면 된다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val view: View?

        return when (viewType)
        {
            Item_chatBot_Message.MESSAGE_BOT    ->
            {
                e(TAG, "MESSAGE_BOT: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_message_bot, parent, false)
                ViewHolder_Message_Bot(view)
            }

            Item_chatBot_Message.MESSAGE_USER   ->
            {
                e(TAG, "MESSAGE_USER: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_message_user, parent, false)
                ViewHolder_Message_User(view)
            }

/*            Item_chatBot_Message.IMAGE_TYPE_2 ->
            {
                view = LayoutInflater.from(parent.context).inflate(R.layout.image_type2, parent, false)
                ImageTypeView2Holder(view)
            }*/

            else ->
            {
                e(TAG, "알 수 없는 뷰 타입 에러")
                throw RuntimeException("알 수 없는 뷰 타입 에러")
            }
        }
    }

    override fun getItemCount(): Int
    {
        return chatList.size
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        e("Adapter_ChatBot_Message_MultiView", "Hi, onBindViewHolder")
//        val currentItem = chatList[position]

        when (chatList[position].viewType)
        {
            // todo: 챗봇 말풍선 표시
            Item_chatBot_Message.MESSAGE_BOT    ->
            {
                (holder as ViewHolder_Message_Bot).message_bot_content.text = chatList[position].textContent
                e(TAG, "MESSAGE_USER: textContent: " + chatList[position].textContent)
            }

            // todo: 유저 말풍선 표시
            Item_chatBot_Message.MESSAGE_USER   ->
            {
                (holder as ViewHolder_Message_User).message_user_content.text = chatList[position].textContent
                holder.message_user_send_time.text = chatList[position].time

                e(TAG, "MESSAGE_USER: textContent: " + chatList[position].textContent)
                e(TAG, "MESSAGE_USER: time: " + chatList[position].time)
            }

/*            Item_chatBot_Message.IMAGE_TYPE_2 ->
            {
                (holder as ImageTypeView2Holder).title.text = obj.text
                holder.content.text = obj.contentString
                holder.image.setImageResource(obj.data)
            }*/

        }
    }

    // 여기서 받는 position은 데이터의 index다.
    @SuppressLint("LongLogTag")
    override fun getItemViewType(position: Int): Int
    {
        e("Adapter_ChatBot_Message_MultiView", "Hi, getItemViewType")
        return chatList[position].viewType
    }

    // todo: 말풍선 챗봇 viewFind
    inner class ViewHolder_Message_Bot(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val message_bot_content: TextView = itemView.findViewById(R.id.message_bot_content)
    }

    // todo: 말풍선 유저 viewFind
    inner class ViewHolder_Message_User(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val message_user_send_time: TextView = itemView.findViewById(R.id.message_user_send_time)
        val message_user_content: TextView = itemView.findViewById(R.id.message_user_content)
    }

    // text Type 3
    inner class ImageTypeView2Holder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        //        val title: TextView = itemView.findViewById(R.id.titleView)
        //        val content: TextView = itemView.findViewById(R.id.contentView)
        //        val image: ImageView = itemView.findViewById(R.id.imageView2)
    }
}