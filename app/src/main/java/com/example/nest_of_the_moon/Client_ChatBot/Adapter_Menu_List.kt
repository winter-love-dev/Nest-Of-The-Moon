package com.example.nest_of_the_moon.Client_ChatBot

import android.content.Context
import android.graphics.Color
import android.os.Message
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.chatBotOrderOptions
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.orderOptionsHandler
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.OrderDetailInfo
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.countComp
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.countHandler
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_order_category.view.*
import kotlinx.android.synthetic.main.item_order_history_type_2.view.*

class Adapter_Menu_List(val context: Context?, private val list: MutableList<Item_chatBot_Order_options>) :
    RecyclerView.Adapter<Adapter_Menu_List.ViewHolder>()
{
    var TAG = "Adapter_Menu_List"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_order_category, parent, false
                                                )
                         )
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        e(TAG, "실행됨: Adapter_Menu_List")
        // 메뉴명 표시
        holder.order_option_text.text = list.get(position).optionText

        // 마지막 경계선은 숨기기
        if (position == list.size - 1)
        {
            holder.order_option_line.visibility = View.GONE
//            e(TAG, "마지막 경계선을 숨김")
        }

        // todo: 클릭 가능, 불가능 상태 만들기
        e(TAG, "isClickEnable: " + list.get(position).isClickEnable)
        if (list.get(position).isClickEnable == false)
        {
            // 클릭 비활성화 하기
            holder.item.isEnabled = false
        }

        // todo: 체크박스 클릭 유지하기
        if (list.get(position).isChecked == true)
        {
            holder.order_check_button.visibility = View.VISIBLE
            holder.order_option_text.setTextColor(Color.parseColor("#FFEA1C77"))
        }

        else
        {
            holder.order_check_button.visibility = View.INVISIBLE
            holder.order_option_text.setTextColor(Color.parseColor("#828282"))
        }

        // todo: 체크박스 클릭 처리
        holder.item.setOnClickListener(View.OnClickListener {

            if (list.get(position).isChecked == true)
            {
                list.get(position).isChecked = false
                holder.order_check_button.visibility = View.INVISIBLE
                holder.order_option_text.setTextColor(Color.parseColor("#828282"))
            }
            else
            {
                list.get(position).isChecked = true
                holder.order_check_button.visibility = View.VISIBLE
                holder.order_option_text.setTextColor(Color.parseColor("#FFEA1C77"))
            }

        })
    }

    // 뷰홀더
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val item = view

        var order_option_text = view.order_option_text
        var order_check_button = view.order_check_button
        var order_option_line = view.order_option_line


    }
} // 리사이클러뷰 끝