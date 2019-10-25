package com.example.nest_of_the_moon.Notification_Tab_Client

import com.google.gson.annotations.SerializedName

data class Item_NotiList_Client
(
    @SerializedName("nest_Order_Index") var nest_Order_Index: String,
    @SerializedName("nest_Order_User_Index") var nest_Order_User_Index: String,
    @SerializedName("nest_Order_Way") var nest_Order_Way: String,
    @SerializedName("nest_Order_Menu_Index") var nest_Order_Menu_Index: String,
    @SerializedName("nest_Order_Serving_Way") var nest_Order_Serving_Way: String,

    @SerializedName("nest_Order_Menu_Option") var nest_Order_Menu_Option: String,
    @SerializedName("nest_Order_User_Request") var nest_Order_User_Request: String,
    @SerializedName("nest_Order_Date_of_payment") var nest_Order_Date_of_payment: String,
    @SerializedName("nest_Order_Serial_Number") var nest_Order_Serial_Number: String,
    @SerializedName("nest_Order_Check_Whether") var nest_Order_Check_Whether: String,
    @SerializedName("nest_Order_Product_Completion") var nest_Order_Product_Completion: String,

    @SerializedName("nest_Order_Pick_Up_Recommend_Time") var nest_Order_Pick_Up_Recommend_Time: String,
    @SerializedName("nest_Order_Serving_Impossible_Time") var nest_Order_Serving_Impossible_Time: String,
    @SerializedName("nest_Order_Point_Accumulate") var nest_Order_Point_Accumulate: String,
    @SerializedName("nest_Order_Abandon_whether") var nest_Order_Abandon_whether: String,
    @SerializedName("nest_Order_refund_whether") var nest_Order_refund_whether: String,
    @SerializedName("nest_Order_Price") var nest_Order_Price: String,
    @SerializedName("nest_Order_Product_Completion_whether") var nest_Order_Product_Completion_whether: String,

    @SerializedName("CartTotalCount") var CartTotalCount: String, // 장바구니 상품 총 합산 개수
    @SerializedName("nest_Order_refund_date") var nest_Order_refund_date: String,

    @SerializedName("nest_Menu_Pick_Up_Recommend_Time") var nest_Menu_Pick_Up_Recommend_Time: String,
    @SerializedName("nest_Menu_Serving_Impossible_Time") var nest_Menu_Serving_Impossible_Time: String,

    @SerializedName("nest_Menu_Thumb") var nest_Menu_Thumb: String,
    @SerializedName("nest_Menu_Name") var nest_Menu_Name: String
)
