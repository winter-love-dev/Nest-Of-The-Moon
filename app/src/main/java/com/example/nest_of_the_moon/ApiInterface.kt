package com.example.nest_of_the_moon

import com.example.nest_of_the_moon.Menu_B_Management.Item_Nest_Menu
import com.example.nest_of_the_moon.Menu_C_Management.Item_Cart
import com.example.nest_of_the_moon.Notification_Tab_Client.Item_NotiList_Client
import com.example.recycler_view_multi_view_test.Item_OrderHistory
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

    // 오늘 주문한 상품 목록 불러오기 (클라이언트 계정)
    @FormUrlEncoded
    @POST("getNestOrderList_Client.php")
    fun getNestNoti_Client(@Field("date") date: String, @Field("id") id: String, @Field("requester") requester: String): Call<List<Item_NotiList_Client>>

    // 오늘의 주문 목록 불러오기 (바리스타 계정)
    @FormUrlEncoded
    @POST("getNestOrderList_Client.php")
    fun getTodayOrderList(@Field("date") id: String, @Field("requester") requester: String): Call<List<Item_NotiList_Client>>

    // 카트에 담긴 상품목록 불러오기
    @FormUrlEncoded
    @POST("getNestCart.php")
    fun getCartList_Client(@Field("id") id: String): Call<List<Item_Cart>>

    // 상품 상세정보 불러오기, 장바구니에서 주문한 상품목록 불러오기
    @FormUrlEncoded
    @POST("getNestOrderCartList.php")
    fun getOrderHistoryList(@Field("orderCartIndex") id: String): Call<List<Item_OrderHistory>>


}
