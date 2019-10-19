package com.example.nest_of_the_moon.Menu_B_Management

import com.google.gson.annotations.SerializedName

data class Item_Nest_Menu(

    @SerializedName("nest_Menu_No") var Menu_No: String,

    @SerializedName("nest_Menu_Type") var Menu_Type: String,
    @SerializedName("nest_Menu_Name") var Menu_Name: String,
    @SerializedName("nest_Menu_Price") var Menu_Price: String,

    @SerializedName("nest_Menu_Product") var Menu_Product: String,
    @SerializedName("nest_Menu_Serving_Size") var Menu_Serving_Size: String,
    @SerializedName("nest_Menu_Pick_Up_Recommend_Time") var Menu_Pick_Up_Recommend_Time: String,

    @SerializedName("Menu_Serving_Way") var Menu_Serving_Way: String,

    @SerializedName("nest_Menu_Sale") var nest_Menu_Sale: String,
    @SerializedName("nest_Menu_Serving_Impossible_Time") var Menu_Serving_Impossible_Time: String,
    @SerializedName("nest_Menu_Thumb") var Menu_Thumb: String,

    @SerializedName("cartCount") var cartCount: String
    )
