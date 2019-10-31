package com.example.recycler_view_multi_view_test

import com.example.nest_of_the_moon.Client_ChatBot.Item_chatBot_options

data class Item_chatBot_Message(
    val viewType: Int,
    val textContent: String,
    val time: String,
    val optionList: ArrayList<Item_chatBot_options>?, // 쇼핑 말풍선 클릭용
    val cancel: Boolean,
    val accecpt: Boolean
)

{
    companion object
    {
        const val MESSAGE_BOT = 0
        const val MESSAGE_USER = 1
        const val ORDER_CATEGORY = 2
        const val ORDER_MENU = 2
        const val ORDER_PAY = 2
        const val ORDER_SERIAL = 2
    }
}