package com.example.recycler_view_multi_view_test

import com.google.gson.annotations.SerializedName

data class Item_OrderHistory(

    @SerializedName("nest_Order_Index") var nest_Order_Index: String,
    @SerializedName("nest_Menu_Thumb") var nest_Menu_Thumb: String,

    @SerializedName("nest_Menu_Name") var nest_Menu_Name: String,

    @SerializedName("nest_Order_Serving_Way") var nest_Order_Serving_Way: String,
    @SerializedName("nest_Order_Menu_Option") var nest_Order_Menu_Option: String,
    @SerializedName("nest_Order_User_Request") var nest_Order_User_Request: String,
    @SerializedName("nest_Order_Price") var nest_Order_Price: String,

    @SerializedName("user") var user: String, // 유저: 고객 or 관리자
    @SerializedName("isChecked") var isChecked: Boolean  // 관리자의 체크박스 표시
)

{
    companion object
    {
        const val TEXT_TYPE = 0
        const val IMAGE_TYPE = 1
        const val IMAGE_TYPE_2 = 2
    }
}