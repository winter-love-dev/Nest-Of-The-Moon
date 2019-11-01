package com.example.nest_of_the_moon.Client_ChatBot

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.Log.e
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.confirmClickReceiveHandler
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.menuClickReceiveHandler
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.orderOptionsHandler
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.orderOptionsReceiveHandler
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.payClickReceiveHandler
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot.Companion.payConfirmClickReceiveHandler
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE
import com.example.recycler_view_multi_view_test.Item_chatBot_Message

class Adapter_ChatBot_Message_MultiView(val context: Context?, private val list: MutableList<Item_chatBot_Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var TAG = "Adapter_ChatBot_Message_MultiView"

    // getItemViewType의 리턴값 Int가 viewType으로 넘어온다.
    // viewType으로 넘어오는 값에 따라 viewHolder를 알맞게 처리해주면 된다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val view: View?

        return when (viewType)
        {
            Item_chatBot_Message.MESSAGE_BOT ->
            {
                e(TAG, "MESSAGE_BOT: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_message_bot, parent, false)
                ViewHolder_Message_Bot(view)
            }

            Item_chatBot_Message.MESSAGE_USER ->
            {
                e(TAG, "MESSAGE_USER: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_message_user, parent, false)
                ViewHolder_Message_User(view)
            }

            Item_chatBot_Message.ORDER_CATEGORY ->
            {
                e(TAG, "ORDER_CATEGORY: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_order_category, parent, false)
                ViewHolder_Order_Category(view)
            }

            Item_chatBot_Message.ORDER_MENU ->
            {
                e(TAG, "ORDER_MENU: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_order_menu, parent, false)
                ViewHolder_Menu_List(view)
            }

            Item_chatBot_Message.ORDER_CONFIRM ->
            {
                e(TAG, "ORDER_CONFIRM: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_confirm, parent, false)
                ViewHolder_Confirm(view)
            }

            Item_chatBot_Message.ORDER_PAY ->
            {
                e(TAG, "ORDER_PAY: inflate 됨")
                view = LayoutInflater.from(parent.context).inflate(R.layout.type_order_pay, parent, false)
                ViewHolder_Pay(view)
            }

            else ->
            {
                e(TAG, "알 수 없는 뷰 타입 에러")
                throw RuntimeException("알 수 없는 뷰 타입 에러")
            }
        }
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    @SuppressLint("LongLogTag", "ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        e("Adapter_ChatBot_Message_MultiView", "Hi, onBindViewHolder")
//        val currentItem = chatList[position]

        var index = list.size - 1
        e(TAG, "index: $index")
        e(TAG, "position: $position")

        when (list[position].viewType)
        {
            // todo: 챗봇 말풍선
            Item_chatBot_Message.MESSAGE_BOT ->
            {
                (holder as ViewHolder_Message_Bot).message_bot_content.text = list[position].textContent
                e(TAG, "MESSAGE_USER: textContent: " + list[position].textContent)
            }

            // -----------------------

            // todo: 유저 말풍선
            Item_chatBot_Message.MESSAGE_USER ->
            {

                (holder as ViewHolder_Message_User).message_user_content.text = list[position].textContent
                holder.message_user_send_time.text = list[position].time

                e(TAG, "MESSAGE_USER: textContent: " + list[position].textContent)
                e(TAG, "MESSAGE_USER: time: " + list[position].time)
            }

            // -----------------------

            // todo: 카테고리 말풍선
            Item_chatBot_Message.ORDER_CATEGORY ->
            {
                (holder as ViewHolder_Order_Category).order_category_message.text = list[position].textContent

                holder.recylcer_view_order_option.layoutManager = LinearLayoutManager(context)
                holder.recylcer_view_order_option.adapter = Adapter_Order_Option(context, list[position].optionList!!)

//                e(TAG, "position: " + list[position])
//                e(TAG, "optionList: " + list[position].optionList?.get(position)?.isChecked)

/*                if (index != position)
                {
                    e(TAG, "index: $index")
                    e(TAG, "position: $position")

                    for (i in list[position].optionList!!.indices)
                    {
                        list[position].optionList!![i].isClickEnable = false
                        e(TAG, "position[$position]isCheck_order_category[$i]: " + list[position].optionList!![i].isChecked)
                        e(TAG, "position[$position]isClickEnable_order_category[$i]: " + list[position].optionList!![i].isClickEnable)
                    }

                    holder.recylcer_view_order_option.adapter!!.notifyDataSetChanged()

                    val message: Message = orderOptionsReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1124
                    message.obj = "선택 취소"

                    orderOptionsReceiveHandler!!.sendMessage(message)
                }*/

                // 카테고리 한 개 클릭하면 나머지 아이템의 enable은 false로 만들기
                orderOptionsHandler = @SuppressLint("HandlerLeak") object : Handler()
                {
                    override fun handleMessage(msg: Message)
                    {
                        if (msg.what == 101)
                        {
                            if (msg.arg1 == 1122)
                            {
                                e(TAG, "클릭 알림 전달받음. 'Adapter_Order_Option' 클래스에서 받음")

                                for (i in list[index].optionList!!.indices)
                                {
                                    list[index].optionList!![i].isClickEnable = false
                                    e(
                                        TAG,
                                        "position[$index]isCheck_order_category[$i]: " + list[index].optionList!![i].isChecked
                                     )
                                    e(
                                        TAG,
                                        "position[$index]isClickEnable_order_category[$i]: " + list[index].optionList!![i].isClickEnable
                                     )
                                }
                                e(TAG, "isClickEnable을 false로 변경 완료")

                                // 카테고리 리사이클러뷰 갱신해서 클릭 비활성화 시키기
                                holder.recylcer_view_order_option.adapter!!.notifyDataSetChanged()

                                // 챗봇 채팅방으로 선택한 카테고리 전달하기 (option_name)
                                var option_name: String = msg.obj.toString()
                                e(TAG, "option_name: $option_name")

                                val message: Message = orderOptionsReceiveHandler?.obtainMessage()!!
                                message.what = PROGRESS_CODE
                                message.arg1 = 1123
                                message.obj = option_name

                                orderOptionsReceiveHandler!!.sendMessage(message)
                            }
                        }
                    }
                }
            }

            // -----------------------
            // todo: 메뉴 선택 말풍선
            Item_chatBot_Message.ORDER_MENU ->
            {
                e(TAG, "실행 됨: Item_chatBot_Message.ORDER_MENU")

                (holder as ViewHolder_Menu_List).menu_message.text = list[position].textContent

                holder.recylcer_view_menu.layoutManager = LinearLayoutManager(context)
                holder.recylcer_view_menu.adapter = Adapter_Menu_List(context, list[position].optionList!!)

                e(TAG, "menu_cancel.isEnabled: " + holder.menu_cancel.isEnabled)
                e(TAG, "menu_accept.isEnabled: " + holder.menu_accept.isEnabled)

                // todo: 취소, 확인 클릭 막기
                if (list[position].cancel == true || list[position].accecpt == true)
                {
                    holder.menu_cancel.isEnabled = false
                    holder.menu_accept.isEnabled = false
                }
                else
                {
                    holder.menu_cancel.isEnabled = true
                    holder.menu_accept.isEnabled = true
                }

                // todo: 취소, 확인 클릭 유지하기
                if (list[position].cancel == true) // 취소
                {
                    holder.menu_cancel.setTextColor(Color.parseColor("#FFEA1C77"))
                }
                else
                {
                    holder.menu_cancel.setTextColor(Color.parseColor("#828282"))
                }

                if (list[position].accecpt == true) // 확인
                {
                    holder.menu_accept.setTextColor(Color.parseColor("#FFEA1C77"))
                }
                else
                {
                    holder.menu_accept.setTextColor(Color.parseColor("#828282"))
                }

                // todo: cancel 버튼 클릭 처리
                holder.menu_cancel.setOnClickListener(View.OnClickListener {

                    // 클릭 비활성화 하기
                    for (i in list[index].optionList!!.indices)
                    {
                        list[index].optionList!![i].isClickEnable = false
                    }

                    // 리스트에 cancel 버튼 클릭여부를 true로 등록하기
                    list[index].cancel = true
                    holder.menu_cancel.setTextColor(Color.parseColor("#FFEA1C77"))

                    e(TAG, "취소 버튼 클릭")

                    // 메뉴목록 갱신
                    (holder.recylcer_view_menu.adapter as Adapter_Menu_List).notifyDataSetChanged()

                    // 채팅 방으로 취소 사실 알리기
                    val message: Message = menuClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1155
                    message.obj = "menu_cancel"

                    menuClickReceiveHandler!!.sendMessage(message)
                })

                // todo: accept 버튼 클릭 처리
                holder.menu_accept.setOnClickListener(View.OnClickListener {

                    var selectList: String? = null

                    // 클릭 비활성화 하기
                    for (i in list[index].optionList!!.indices)
                    {
                        list[index].optionList!![i].isClickEnable = false

                        if (list[index].optionList!![i].isChecked == true)
                        {
                            // 체크박스로 선택한 메뉴 담기
                            if (selectList == null)
                            {
                                selectList = list[index].optionList!![i].optionText
                            }
                            else
                            {
                                selectList = selectList + ", " + list[index].optionList!![i].optionText
                            }
                        }
                    }

                    // 리스트에 accecpt 버튼 클릭여부를 true로 등록하기
                    list[index].accecpt = true
                    holder.menu_accept.setTextColor(Color.parseColor("#FFEA1C77"))

                    e(TAG, "확인 버튼 클릭")

                    // 메뉴목록 갱신
                    (holder.recylcer_view_menu.adapter as Adapter_Menu_List).notifyDataSetChanged()

                    // 채팅방으로 취소 사실 알리기
                    val message: Message = menuClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1155
                    message.obj = "menu_accept, $selectList"

                    menuClickReceiveHandler!!.sendMessage(message)
                })
            }

            // -----------------------
            // todo: 승인 여부 선택 말풍선
            Item_chatBot_Message.ORDER_CONFIRM ->
            {
                (holder as ViewHolder_Confirm).confirm_content.text = list[position].textContent

                // todo: 취소, 확인 클릭 유지하기
                if (list[position].cancel == true) // 취소
                {
                    holder.confirm_cancel.setTextColor(Color.parseColor("#FFEA1C77"))
                }
                else
                {
                    holder.confirm_cancel.setTextColor(Color.parseColor("#828282"))
                }

                if (list[position].accecpt == true) // 확인
                {
                    holder.confirm_accept.setTextColor(Color.parseColor("#FFEA1C77"))
                }
                else
                {
                    holder.confirm_accept.setTextColor(Color.parseColor("#828282"))
                }

                // todo: 취소, 확인 클릭 막기
                if (list[position].cancel == true || list[position].accecpt == true)
                {
                    holder.confirm_cancel.isEnabled = false
                    holder.confirm_accept.isEnabled = false
                }
                else
                {
                    holder.confirm_cancel.isEnabled = true
                    holder.confirm_accept.isEnabled = true
                }

                // cancel 버튼 클릭처리
                holder.confirm_cancel.setOnClickListener(View.OnClickListener {

                    holder.confirm_accept.setTextColor(Color.parseColor("#828282"))
                    list[index].cancel = true

                    val message: Message = confirmClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1156
                    message.obj = "confirm_cancel"

                    confirmClickReceiveHandler!!.sendMessage(message)
                })

                // accept 버튼 클릭처리
                holder.confirm_accept.setOnClickListener(View.OnClickListener {

                    holder.confirm_accept.setTextColor(Color.parseColor("#FFEA1C77"))
                    list[index].accecpt = true

                    val message: Message = confirmClickReceiveHandler?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 1156
                    message.obj = "confirm_accept"

                    confirmClickReceiveHandler!!.sendMessage(message)
                })
            }

            // -----------------------
            // todo: 결제 말풍선
            Item_chatBot_Message.ORDER_PAY ->
            {
                (holder as ViewHolder_Pay).pay_message.text = list[position].textContent

                var total_price: Int? = null

                // 메뉴 총 가격 구하기
                for (i in list[position].payList!!.indices)
                {
                    if (list[position].payList!![i].isSelect == true)
                    {
                        if (total_price == null)
                        {
                            total_price = list[position].payList!![i].menuPrice
                        }
                        else
                        {
                            total_price = total_price + list[position].payList!![i].menuPrice
                        }
                    }
                }

                holder.recylcer_pay.layoutManager = LinearLayoutManager(context)
                holder.recylcer_pay.adapter = Adapter_Pay_List(context, list[position].payList!!) // pay 리스트 연결

                // todo: 결제 완료버튼 상태 유지하기
                if (list[position].accecpt == true)
                {
                    holder.pay_accept.text = "결제완료"
                    holder.pay_accept.setTextColor(Color.parseColor("#828282"))

                    holder.pay_price.text = "총 " + total_price.toString() + "원"

                    // 결제 완료했으면 클릭 막기
                    holder.pay_accept.isEnabled = false
                }
                else
                {
                    holder.pay_accept.text = "결제하기"
                    holder.pay_accept.setTextColor(Color.parseColor("#FFEA1C77"))

                    holder.pay_price.text = "총 " + total_price.toString() + "원"

                    // 결제 안했으면 결제버튼 활성화
                    holder.pay_accept.isEnabled = true

                    // todo: 결제버튼 클릭
                    holder.pay_accept.setOnClickListener(View.OnClickListener {

                        // 다이얼로그
                        val builder = AlertDialog.Builder(
                            ContextThemeWrapper(
                                context,
                                R.style.Theme_AppCompat_Light_Dialog
                                               )
                                                         )

                        builder.setIcon(R.drawable.logo_1)
                        builder.setTitle("${total_price}원")
                        builder.setMessage("주문 하시겠습니까?")

                        // todo: 확인버튼 처리
                        builder.setPositiveButton("확인") { _, _ ->
                            e(TAG, "확인")

                            // 결제 완료 후 핸들러로 결제 완료알림 보내기 (채팅방으로)
                            for (i in list[position].payList!!.indices)
                            {
                                // 모든 항목의 isPay를 true로 등록해서 결제 사실 알리기
                                list[position].payList!![i].isPay = false

                                // 선택, 선택 안 함 구분하기.
                                // 결제한 상품만 isPayFinally에 true로 등록하기 false인 값은 삭제 처리 유지하기 / isSelect = false로 유지하기
                                if (list[position].payList!![i].isSelect == true)
                                {
                                    list[position].payList!![i].isPayFinally = true
                                }

                                else
                                {
                                    list[position].payList!![i].isPayFinally = false
                                }
                            }

                            val message: Message = payConfirmClickReceiveHandler?.obtainMessage()!!
                            message.what = PROGRESS_CODE
                            message.arg1 = 11676
                            message.obj = total_price

                            payConfirmClickReceiveHandler!!.sendMessage(message)
                        }

                        builder.setNegativeButton("취소") { _, _ ->
                            e(TAG, "취소")
                        }
                        builder.show()

                    })
                }

                // 결제 목록에서 '삭제, 삭제 취소, 수량 수정, 총 가격 계산 요청' 신호 받기
                payClickReceiveHandler = @SuppressLint("HandlerLeak") object : Handler()
                {
                    override fun handleMessage(msg: Message)
                    {
                        if (msg.what == PROGRESS_CODE)
                        {
                            if (msg.arg1 == 11557)
                            {
                                var receive: String = msg.obj.toString()

                                // 가격 갱신하기
                                if (receive == "notifySetPrice")
                                {

                                    total_price = null
                                    for (i in list[position].payList!!.indices)
                                    {
                                        if (list[position].payList!![i].isSelect == true)
                                        {
                                            if (total_price == null)
                                            {
                                                total_price = list[position].payList!![i].menuPrice
                                            }
                                            else
                                            {
                                                total_price = total_price!! + list[position].payList!![i].menuPrice
                                            }
                                        }
                                    }

                                    if (total_price == null)
                                    {
                                        holder.pay_price.text = "총 0원"
                                        holder.pay_accept.setTextColor(Color.parseColor("#828282"))
                                        holder.pay_accept.isEnabled = false
                                    }
                                    else
                                    {
                                        holder.pay_price.text = "총 " + total_price.toString() + "원"
                                        holder.pay_accept.setTextColor(Color.parseColor("#FFEA1C77"))
                                        holder.pay_accept.isEnabled = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 여기서 받는 position은 데이터의 index다.
    @SuppressLint("LongLogTag")
    override fun getItemViewType(position: Int): Int
    {
        e("Adapter_ChatBot_Message_MultiView", "Hi, getItemViewType")
        return list[position].viewType
    }

    // todo: 말풍선 챗봇 viewFind
    inner class ViewHolder_Message_Bot(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val message_bot_content: TextView = itemView.findViewById(R.id.message_bot_content)
    }

    // todo: 말풍선 유저 viewFind
    inner class ViewHolder_Message_User(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val message_user_send_time: TextView = itemView.findViewById(R.id.message_user_send_time)
        val message_user_content: TextView = itemView.findViewById(R.id.message_user_content)
    }

    // todo: 말풍선 카테고리 viewFind
    inner class ViewHolder_Order_Category(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val order_category_message: TextView = itemView.findViewById(R.id.order_category_message)
        val recylcer_view_order_option: RecyclerView = itemView.findViewById(R.id.recylcer_view_order_option)
    }

    // todo: 말풍선 메뉴목록 viewFind
    inner class ViewHolder_Menu_List(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val menu_message: TextView = itemView.findViewById(R.id.menu_message)
        val recylcer_view_menu: RecyclerView = itemView.findViewById(R.id.recylcer_view_menu)
        val menu_cancel: TextView = itemView.findViewById(R.id.menu_cancel)
        val menu_accept: TextView = itemView.findViewById(R.id.menu_accept)
    }

    // todo: 승인 여부 선택 말풍선
    inner class ViewHolder_Confirm(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val confirm_content: TextView = itemView.findViewById(R.id.confirm_content)
        val confirm_cancel: TextView = itemView.findViewById(R.id.confirm_cancel)
        val confirm_accept: TextView = itemView.findViewById(R.id.confirm_accept)
    }

    // todo: 결제 말풍선
    inner class ViewHolder_Pay(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val pay_message: TextView = itemView.findViewById(R.id.pay_message)
        val recylcer_pay: RecyclerView = itemView.findViewById(R.id.recylcer_pay)
        val pay_price: TextView = itemView.findViewById(R.id.pay_price)
        val pay_accept: TextView = itemView.findViewById(R.id.pay_accept)
    }


}