package com.example.nest_of_the_moon.Order_B_Menagement

import android.content.Context
import android.os.Message
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.OrderDetailInfo
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.countComp
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.countHandler
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management.Companion.itemOrderList
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_order_history_type_2.view.*

class Adapter_Order_BOP(val context: Context?): RecyclerView.Adapter<Adapter_Order_BOP.ViewHolder>()
{
    var TAG = "Adapter_Order_BOP"

    companion object
    {
        var checkCount: Int = 0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_order_history_type_2, parent, false
                                                    )
                         )
    }

    override fun getItemCount(): Int
    {
        return OrderDetailInfo.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        e(TAG, "값 들어오냐?")

        // 썸네일 이미지
        Picasso.get().load(OrderDetailInfo.get(position).nest_Menu_Thumb)
            .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
            //.resize(300,300)       // 300x300 픽셀
            .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
            .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
            .into(holder.oht_2_thumb) // this인 ImageView에 로드

        var count = position + 1

        holder.oht_2_way.text = OrderDetailInfo.get(position).nest_Order_Serving_Way + " #$count"
        holder.oht_2_name.text = OrderDetailInfo.get(position).nest_Menu_Name
        holder.oht_2_price.text = OrderDetailInfo.get(position).nest_Order_Price + "원"

        holder.oht_2_option.text = OrderDetailInfo.get(position).nest_Order_Menu_Option
        holder.oht_2_request.text = OrderDetailInfo.get(position).nest_Order_User_Request

        /** todo: 체크박스 활성화, 비활성화 흐름

        1. 관리자가 '제작 시작'버튼을 클릭하면 체크박스 활성화 (user: Barista로 설정 후 리사이클러뷰 새로고침 하기)
        2. '제작 완료'버튼 누르면 체크박스 비활성화 (user: none으로 설정 후 리사이클러뷰 새로고침 하기)

         */

        // todo: 체크박스 처리
        if (OrderDetailInfo.get(position).user == "Barista")
        {
            holder.oht_2_check.visibility = View.VISIBLE
            holder.item.isEnabled = true

            if (OrderDetailInfo.get(position).isChecked == false)
            {
                holder.oht_2_check.setImageResource(R.drawable.ic_un_check_all)
                OrderDetailInfo.get(position).isChecked = true
            }
            else
            {
                holder.oht_2_check.setImageResource(R.drawable.ic_check_all)
                OrderDetailInfo.get(position).isChecked = false
            }
        }
        else
        {
            holder.oht_2_check.visibility = View.INVISIBLE
            holder.item.isEnabled = false
        }

        // todo: 체크박스 클릭 처리
        holder.item.setOnClickListener(View.OnClickListener {
//            message.arg1 = 1

            if (OrderDetailInfo.get(position).isChecked == true)
            {
                OrderDetailInfo.get(position).isChecked = false

                checkCount++

                if (OrderDetailInfo.size == checkCount)
                {
                    e(TAG, "OrderDetailInfo.size == checkCount")
                    countComp = true

                    val message: Message = countHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1
                    countHandler?.sendMessage(message)
                }

                else
                {
                    e(TAG, "OrderDetailInfo.size != checkCount")
                    countComp = false

                    val message: Message = countHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 2
                    countHandler?.sendMessage(message)
                }

                e(TAG, "OrderDetailInfo.size:  " + OrderDetailInfo.size)
                e(TAG, "checkCount: $checkCount")

                holder.oht_2_check.setImageResource(R.drawable.ic_check_all)

                //                    bop_recycler_view?.adapter?.notifyDataSetChanged()
            }

            // todo: 클릭 취소처리
            else
            {
                OrderDetailInfo.get(position).isChecked = true

                holder.oht_2_check.setImageResource(R.drawable.ic_un_check_all)

                checkCount--

                if (OrderDetailInfo.size == checkCount)
                {
                    e(TAG, "else OrderDetailInfo.size == checkCount")
                    countComp = true

                    val message: Message = countHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1
                    countHandler?.sendMessage(message)
                }

                else
                {
                    e(TAG, "else OrderDetailInfo.size != checkCount")
                    countComp = false

                    val message: Message = countHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 2
                    countHandler?.sendMessage(message)
                }

                e(TAG, "OrderDetailInfo.size:  " + OrderDetailInfo.size)
                e(TAG, "checkCount: $checkCount")

                //                    bop_recycler_view?.adapter?.notifyDataSetChanged()
            }
        })
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