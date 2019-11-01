package com.example.recycler_view_multi_view_test

import com.example.nest_of_the_moon.Client_ChatBot.Item_chatBot_Order_options
import com.example.nest_of_the_moon.Client_ChatBot.Item_chatBot_Pay_Option

data class Item_chatBot_Message(
    val viewType: Int,
    val textContent: String,
    val time: String,
    val optionList: MutableList<Item_chatBot_Order_options>?, // 쇼핑 말풍선 (카테고리, 메뉴선택) 두 가지 말 풍선
    val payList: MutableList<Item_chatBot_Pay_Option>?, // 주문 목록 말풍선
    var cancel: Boolean,
    var accecpt: Boolean,
    var isPay: Boolean
)

{
    companion object
    {
        const val MESSAGE_BOT = 0
        const val MESSAGE_USER = 1
        const val ORDER_CATEGORY = 2
        const val ORDER_MENU = 3
        const val ORDER_CONFIRM = 4
        const val ORDER_PAY = 5
        const val ORDER_SERIAL = 6
    }
}