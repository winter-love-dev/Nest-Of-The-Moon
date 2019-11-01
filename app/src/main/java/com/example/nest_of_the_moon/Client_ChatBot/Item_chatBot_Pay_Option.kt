package com.example.nest_of_the_moon.Client_ChatBot

data class Item_chatBot_Pay_Option(
    val menuType: String,
    val menuName: String,
    var menuPrice: Int,
    var selectCount: Int,
    var isSelect: Boolean,
    var isPay: Boolean,         // 구매함, 안 함
    var isPayFinally: Boolean   // 하지만 상품 목록에 있던 이 상품은 구매하지 않았음 (false): spinner gone / 상품 목록에서 구매함 (true): text view visible
                                     )
