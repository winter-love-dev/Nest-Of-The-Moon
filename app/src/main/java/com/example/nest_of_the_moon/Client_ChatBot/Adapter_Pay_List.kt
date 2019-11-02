package com.example.nest_of_the_moon.Client_ChatBot

import android.content.Context
import android.graphics.Color
import android.os.Message
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.payClickReceiveHandler
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.Service.nestService
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE
import kotlinx.android.synthetic.main.item_pay_list.view.*

class Adapter_Pay_List(var context: Context?, private val list: MutableList<Item_chatBot_Pay_Option>) :
    RecyclerView.Adapter<Adapter_Pay_List.ViewHolder>()
{
    var TAG = "Adapter_Pay_List"

    companion object
    {
        var paySelectCount: String? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_pay_list, parent, false
                                                )
                         )
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        // 메뉴명
        holder.pay_text.text = list.get(position).menuName

        // 마지막 경계선은 숨기기
        if (position == list.size - 1)
        {
            holder.pay_line.visibility = View.GONE
//            e(TAG, "마지막 경계선을 숨김")
        }

        // 모든 항목 클릭 가능, 불가능 상태 유지하기
        if (list.get(position).isPay == true)
        {
            holder.item.isEnabled = false
            holder.item.pay_delete.isEnabled = false // 삭제버튼 비활성화

            holder.pay_count_spinner.visibility = View.GONE
            holder.pay_count_finally.visibility = View.VISIBLE
            holder.pay_count_finally.text = list[position].selectCount.toString()
            holder.pay_price.text = list.get(position).menuPrice.toString() + "원"
        }

        else
        {
            holder.item.isEnabled = true
        }


        var savePrice0 = 0
        var savePrice1 = 0
        var savePrice2 = 0
        var savePrice3 = 0
/* savePrice1 = list.get(position).menuPrice
        savePrice2 = list.get(position).menuPrice * 2
        savePrice3 = list.get(position).menuPrice * 3 */

        savePrice0 = list.get(position).menuPrice
        savePrice1 = 4000
        savePrice2 = 4000 * 2
        savePrice3 = 4000 * 3

        e(TAG, "savePrice2: $savePrice2")

        // 스피너로 잔 수 선택
        // todo: 스피너 세팅: 주문옵션
        val paySpinnerList = ArrayList<Any>()
        paySpinnerList.add("1") // 0
        paySpinnerList.add("2") // 1
        paySpinnerList.add("3") // 2

        val paySpinnerAdapter= ArrayAdapter(
            context, R.layout.spinner_item, paySpinnerList
                                            )

        holder.pay_count_spinner.adapter = paySpinnerAdapter as SpinnerAdapter?

        // 가격 유지하기
        if (list.get(position).selectCount == 1)
        {
            list.get(position).selectCount = 1
            holder.pay_count_spinner.setSelection(0)
//            holder.pay_price.text = savePrice1.toString() + "원"
        }

        else if (list.get(position).selectCount == 2)
        {
            list.get(position).selectCount = 2
            holder.pay_count_spinner.setSelection(1)
//            holder.pay_price.text = savePrice2.toString() + "원"
        }

        else if (list.get(position).selectCount == 3)
        {
            list.get(position).selectCount = 3
            holder.pay_count_spinner.setSelection(2)
//            holder.pay_price.text = savePrice3.toString() + "원"
        }

        holder.pay_count_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                e(TAG, "onNothingSelected: $position")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, positions: Int, id: Long)
            {
                if (positions == 0)
                {
//                    paySelectCount = "1"
//                    e(TAG, "paySelectCount: $paySelectCount")
                    list.get(position).selectCount = 1 // 리스트에 선택한 수량 표시

                    holder.pay_price.text = "${savePrice1}원"
                    list.get(position).menuPrice = savePrice1

                    val message: Message = payClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 11557
                    message.obj = "notifySetPrice"

                    payClickReceiveHandler!!.sendMessage(message)

                    e(TAG, "menuPrice: selectCount: $positions, $position:" + list.get(position).selectCount)
                    e(TAG, "menuPrice: positions: $positions, $position:" + list.get(position).menuPrice)
                }

                else if (positions == 1)
                {
//                    paySelectCount = "2"
//                    e(TAG, "paySelectCount: $paySelectCount")
                    list.get(position).selectCount = 2 // 리스트에 선택한 수량 표시

                    holder.pay_price.text = "${savePrice2}원"
                    list.get(position).menuPrice = savePrice2

                    val message: Message = payClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 11557
                    message.obj = "notifySetPrice"

                    payClickReceiveHandler!!.sendMessage(message)
                    e(TAG, "menuPrice: selectCount: $positions, $position:" + list.get(position).selectCount)
                    e(TAG, "menuPrice: positions: $positions, $position:" + list.get(position).menuPrice)
                }

                else if (positions == 2)
                {
//                    paySelectCount = "3"
//                    e(TAG, "paySelectCount: $paySelectCount")
                    list.get(position).selectCount = 3 // 리스트에 선택한 수량 표시

                    holder.pay_price.text = "${savePrice3}원"
                    list.get(position).menuPrice = savePrice3

                    val message: Message = payClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 11557
                    message.obj = "notifySetPrice"

                    payClickReceiveHandler!!.sendMessage(message)

                    e(TAG, "menuPrice: selectCount: $positions, $position:" + list.get(position).selectCount)
                    e(TAG, "menuPrice: positions: $positions, $position:" + list.get(position).menuPrice)
                }
            }
        }

//        e(TAG, "selectCount: $position:" + list.get(position).selectCount)
//        e(TAG, "menuPrice: $position:" + list.get(position).menuPrice)

        // 스피너로 잔 수 선택 상태 유지하기

        // 삭제, 삭제 취소버튼
        holder.pay_delete.setOnClickListener(View.OnClickListener {

            if (list.get(position).isSelect == true)
            {
                list.get(position).isSelect = false
                holder.pay_text.setTextColor(Color.parseColor("#828282"))
                holder.pay_price.setTextColor(Color.parseColor("#828282"))
                holder.pay_delete.setImageDrawable(context!!.getDrawable(R.drawable.ic_delete_forever_2))

                val message: Message = payClickReceiveHandler?.obtainMessage()!!
                message.what = nestService.PROGRESS_CODE
                message.arg1 = 11557
                message.obj = "notifySetPrice"

                payClickReceiveHandler!!.sendMessage(message)
            }
            else
            {
                list.get(position).isSelect = true
                holder.pay_text.setTextColor(Color.parseColor("#FFEA1C77"))
                holder.pay_price.setTextColor(Color.parseColor("#000000"))
                holder.pay_delete.setImageDrawable(context!!.getDrawable(R.drawable.ic_delete_forever))

                val message: Message = payClickReceiveHandler?.obtainMessage()!!
                message.what = nestService.PROGRESS_CODE
                message.arg1 = 11557
                message.obj = "notifySetPrice"

                payClickReceiveHandler!!.sendMessage(message)
            }
        })

        // 삭제, 삭제 취소버튼 상태 유지하기
        if (list.get(position).isSelect == true)
        {
            holder.pay_text.setTextColor(Color.parseColor("#FFEA1C77"))
            holder.pay_price.setTextColor(Color.parseColor("#000000"))
            holder.pay_delete.setImageDrawable(context!!.getDrawable(R.drawable.ic_delete_forever))
        }
        else
        {
            holder.pay_text.setTextColor(Color.parseColor("#828282"))
            holder.pay_price.setTextColor(Color.parseColor("#828282"))
            holder.pay_delete.setImageDrawable(context!!.getDrawable(R.drawable.ic_delete_forever_2))
        }

        // 핸들러로 잔 수 선택, 삭제, 삭제 취소 상태 전달하기


        // 핸들러로 클릭 상황 전달 ('얼마나 클릭했는지 확인세요'라는 메시지)
/*        if (isOptionCheck == true)
        {
            val message: Message = orderOptionsHandler?.obtainMessage()!!
            message.what = PROGRESS_CODE
            message.arg1 = 1122
            message.obj = list.get(position).optionText

            orderOptionsHandler!!.sendMessage(message)
        }*/
    }

    // 뷰홀더
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val item = view
        val pay_text = view.pay_text
        var pay_count_spinner = view.pay_spinner
        var pay_count_finally = view.pay_count_finally
        var pay_delete = view.pay_delete
        val pay_price = view.pay_price
        val pay_line = view.pay_line


    }
} // 리사이클러뷰 끝