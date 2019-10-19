package com.example.nest_of_the_moon

import com.example.nest_of_the_moon.Menu_B_Management.Item_Nest_Menu
import com.example.nest_of_the_moon.Menu_C_Management.Item_Cart
import com.example.nest_of_the_moon.Notification_Tab_Client.Item_NotiList_Client
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface
{
    // 달둥 메뉴 불러오기 (바리스타 계정)
    @FormUrlEncoded
    @POST("getCoffeeMenu.php")
    fun getNestMenuList(@Field("SortRequest") id: String): Call<List<Item_Nest_Menu>>

    // 달둥 메뉴 불러오기 (클라이언트 계정)
    @FormUrlEncoded
    @POST("getCoffeeMenuClient.php")
    fun getNestMenuList_Client(@Field("getNestMenuRequest") MenuRequest: String, @Field("id") id: String): Call<List<Item_Nest_Menu>>

    // 주문한 메뉴 목록 불러오기 (클라이언트 계정)
    @FormUrlEncoded
    @POST("getNestOrderList_Client.php")
    fun getNestNoti_Client(@Field("date") date: String, @Field("id") id: String): Call<List<Item_NotiList_Client>>

    // 카트에 담긴 상품목록 불러오기
    @FormUrlEncoded
    @POST("getNestCart.php")
    fun getCartList_Client(@Field("id") id: String): Call<List<Item_Cart>>
}
