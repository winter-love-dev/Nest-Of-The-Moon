package com.example.nest_of_the_moon.Notification_Tab_Client

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.Notification_Tab_Client.Fragment_Client_Noti.Companion.OrderHistory
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management
import com.example.nest_of_the_moon.R
import com.example.recycler_view_multi_view_test.Item_OrderHistory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_nest_order_noti.view.*
import kotlinx.android.synthetic.main.item_order_history_type_2.view.*

// 리사이클러뷰
class Adapter_OrderHistory(val context: Context?): RecyclerView.Adapter<Adapter_OrderHistory.ViewHolder>()
{
    //    var OrderDetailInfo = arrayListOf<Item_OrderHistory>()

    var TAG = "Adapter_OrderHistory"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_history_type_2, parent, false))
    }

    override fun getItemCount(): Int
    {
        return OrderHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        e(TAG, "값 들어오냐?")

        // 썸네일 이미지
        Picasso.get().load(OrderHistory.get(position).nest_Menu_Thumb)
            .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
            //.resize(300,300)       // 300x300 픽셀
            .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
            .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
            .into(holder.oht_2_thumb) // this인 ImageView에 로드

        var count = position + 1

        holder.oht_2_way.text = OrderHistory.get(position).nest_Order_Serving_Way + "# $count"
        holder.oht_2_name.text = OrderHistory.get(position).nest_Menu_Name
        holder.oht_2_price.text = OrderHistory.get(position).nest_Order_Price + "원"

        holder.oht_2_option.text = OrderHistory.get(position).nest_Order_Menu_Option
        holder.oht_2_request.text = OrderHistory.get(position).nest_Order_User_Request

//        // todo: 체크박스 처리
//        if (OrderDetailInfo.get(position).user == "Barista")
//        {
//            holder.oht_2_check.visibility = View.VISIBLE
//
//            if (OrderDetailInfo.get(position).isChecked == "false")
//            {
//                holder.oht_2_check.setImageResource(R.drawable.ic_un_check_all)
//            }
//            else
//            {
//                holder.oht_2_check.setImageResource(R.drawable.ic_check_all)
//            }
//        }

//        holder.item.setOnClickListener(View.OnClickListener {
//            // 리사이클러뷰 갱신
//            Fragment_B_Order_Management.bop_recycler_view?.adapter?.notifyDataSetChanged()
//        })

    }

    // 뷰홀더
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val item = view

        var oht_2_thumb = view.oht_2_thumb

        var oht_2_way = view.oht_2_way
        var oht_2_name = view.oht_2_name
        var oht_2_price = view.oht_2_price

        var oht_2_option = view.oht_2_option
        var oht_2_request = view.oht_2_request

        var oht_2_check = view.oht_2_check

    }
} // 리사이클러뷰 끝

