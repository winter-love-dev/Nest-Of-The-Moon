package com.example.nest_of_the_moon.Menu_C_Management

import com.google.gson.annotations.SerializedName

data class Item_Cart
(
    @SerializedName("nest_Order_Index") var nest_Order_Index: String,
    @SerializedName("nest_Order_Way") var nest_Order_Way: String,
    @SerializedName("nest_Menu_Name") var nest_Menu_Name: String,

    @SerializedName("nest_Menu_Thumb") var nest_Menu_Thumb: String,
    @SerializedName("nest_Order_Serving_Way") var nest_Order_Serving_Way: String,
    @SerializedName("nest_Order_Menu_Option") var nest_Order_Menu_Option: String,

    @SerializedName("nest_Order_User_Request") var nest_Order_User_Request: String,
    @SerializedName("nest_Order_Price") var nest_Order_Price: String
//    @SerializedName("isChecked") var isChecked: Boolean
)
