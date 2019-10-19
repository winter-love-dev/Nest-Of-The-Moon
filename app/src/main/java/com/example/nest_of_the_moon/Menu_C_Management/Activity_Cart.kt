package com.example.nest_of_the_moon.Menu_C_Management

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.ApiClient
import com.example.nest_of_the_moon.ApiInterface
import com.example.nest_of_the_moon.Client.Activity_Client_Home.Companion.GET_ID
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management
import com.example.nest_of_the_moon.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.item_nest_cart.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Activity_Cart: AppCompatActivity()
{

    var TAG: String = "Activity_Cart"
    var item_Nest_Cart = arrayListOf<Item_Cart>()
    var item_Cart_Check = ArrayList<Item_Cart_Check>()
    var totalPrice: Int = 0
    var totalCount: Int = 0
    var index: Int = 0
    var price: Int = 0
    var check: Boolean = true
    var switch0: Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // 장바구니의 선택한 상품 삭제하기
        client_check_delete_forever.setOnClickListener(View.OnClickListener {

        })

        // 선택한 상품 결제하기
        client_cart_payment_button.setOnClickListener(View.OnClickListener {

        })

        // 리사이클러뷰 세팅
        client_cart_list as RecyclerView
        client_cart_list.layoutManager = LinearLayoutManager(applicationContext)

        // 리사이클러뷰 구분선 넣기 가로
        client_cart_list.addItemDecoration(
                DividerItemDecoration(
                        applicationContext, DividerItemDecoration.VERTICAL
                                     )
                                          )

        getCartList(GET_ID.toString())
    }

    fun getCartList(id: String)
    {
        Log.e(Fragment_Menu_Management.TAG, "getClientMenuList(): 카트 목록 불러오기")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val projectListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = projectListRequest.getCartList_Client(id)

        listCall.enqueue(object: Callback<List<Item_Cart>>
        {
            override fun onResponse(call: Call<List<Item_Cart>>, response: Response<List<Item_Cart>>)
            {
                Log.e(Fragment_Menu_Management.TAG, "list call onResponse = 수신 받음")

                item_Nest_Cart = response.body() as ArrayList<Item_Cart>

                // 체크박스로 선택된 상품의 DB 인덱스와 상품 가격 담기
                for (i in item_Nest_Cart.indices)
                {
                    item_Cart_Check.add(
                            Item_Cart_Check(
                                    item_Nest_Cart.get(i).nest_Order_Index,
                                    Integer.parseInt(item_Nest_Cart.get(i).nest_Order_Price),
                                    true
                                           )
                                       )

                    totalPrice = totalPrice + Integer.parseInt(item_Nest_Cart.get(i).nest_Order_Price)
                }

                // 선택한 상품 갯수
                totalCount = item_Cart_Check.size

                // 값이 잘 들어갔는지 확인하기
                for (i in item_Nest_Cart.indices)
                {
                    Log.e(
                            TAG,
                            "nest_Order_Index: " + item_Nest_Cart.get(i).nest_Order_Index + " / nest_Order_Price: " + item_Nest_Cart.get(i).nest_Order_Price
                         )
                }

                Log.e(TAG, "totalPrice: " + totalPrice)
                Log.e(TAG, "totalCount: " + totalCount)

                // todo: 화면에 선택한 상품 갯수 표시하기
                client_cart_item_count.text = totalCount.toString() + " 개"

                // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                client_cart_total_price.text = totalPrice.toString() + " 원"

                // 리사이클러뷰 어댑터 세팅
                client_cart_list?.adapter = CartListAdapter(applicationContext)
                client_cart_list?.setAdapter(client_cart_list?.adapter)
                client_check_all.visibility = View.GONE

                // 모두 선택 && 모두 선택 취소 버튼
                // todo: 장바구니의 상품 모두 선택
                client_check_all.setOnClickListener(View.OnClickListener {

                    for (i in item_Nest_Cart.indices)
                    {
                        item_Cart_Check.get(i).isChecked = true

                        Log.e(
                                Fragment_Menu_Management.TAG,
                                "item_Nest_Cart.get(position).isChecked = true: " + item_Cart_Check.get(i).isChecked
                             )
                    }

                    // 리사이클러뷰 갱신
                    client_cart_list?.adapter!!.notifyDataSetChanged()

                    // 선택한 상품 갯수
                    totalCount = item_Cart_Check.size

                    // 총 가격 계산하기
                    if (switch0 == 1)
                    {
                        for (i in item_Nest_Cart.indices)
                        {
                            totalPrice = totalPrice + Integer.parseInt(item_Nest_Cart.get(i).nest_Order_Price)
                        }
                    }


                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"

                    switch0 = 0
                    client_check_all.visibility = View.GONE
                    client_un_check_all.visibility = View.VISIBLE
                })


                // todo: 장바구니의 상품 모두 선택 해제
                client_un_check_all.setOnClickListener(View.OnClickListener {

                    for (i in item_Nest_Cart.indices)
                    {
                        item_Cart_Check.get(i).isChecked = false

                        Log.e(
                                Fragment_Menu_Management.TAG,
                                "item_Cart_Check.get(" + i + ").isChecked: " + item_Cart_Check.get(i).isChecked
                             )
                    }

                    // 리스트 모두 비우기
//                    item_Cart_Check.clear()

                    totalCount = 0
                    totalPrice = 0

                    // 리사이클러뷰 갱신
                    client_cart_list?.adapter!!.notifyDataSetChanged()

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"

                    // 다시 모두 선택했을 때 합산한 가격을 계산할 수 있게 만들기
                    switch0 = 1
                    client_check_all.visibility = View.VISIBLE
                    client_un_check_all.visibility = View.GONE
                })
            }

            override fun onFailure(call: Call<List<Item_Cart>>, t: Throwable)
            {
                Toast.makeText(Fragment_Menu_Management.mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                Log.e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
            }
        })
    }


    // todo: 리사이클러뷰
    inner class CartListAdapter(val context: Context?): RecyclerView.Adapter<CartListAdapter.ViewHolder>()
    {
        var i = true

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_nest_cart, parent, false))
        }

        override fun getItemCount(): Int
        {
            return item_Nest_Cart.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            // 썸네일 이미지
            Picasso.get().load(item_Nest_Cart.get(position).nest_Menu_Thumb)
                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(holder.cart_c_tumb) // this인 ImageView에 로드

            holder.cart_c_name.text = item_Nest_Cart.get(position).nest_Menu_Name
            holder.cart_c_option.text = item_Nest_Cart.get(position).nest_Order_Menu_Option

            holder.cart_c_request.text = item_Nest_Cart.get(position).nest_Order_User_Request
            holder.noti_c_price.text = item_Nest_Cart.get(position).nest_Order_Price + "원"
            holder.cart_c_way.text = item_Nest_Cart.get(position).nest_Order_Serving_Way


            // 체크박스 처리
            holder.cart_c_check_box.setOnCheckedChangeListener(null)

            // 체크박스 세팅 (기본 true로 표시함)
            if (item_Cart_Check.get(position).isChecked)
            {
                holder.cart_c_check_box.isChecked = true
            }

            else /*if (!item_Cart_Check.get(position).isChecked)*/
            {
                holder.cart_c_check_box.isChecked = false
            }

            // todo: 하나씩 선택하기 (목록 클릭)
            holder.item.setOnClickListener(View.OnClickListener {

                // 선택 취소
                if (holder.cart_c_check_box.isChecked)
                {
                    holder.cart_c_check_box.isChecked = false
                    item_Cart_Check.get(position).isChecked = false

                    Log.e(TAG, "holder.cart_c_check_box.isChecked: " + holder.cart_c_check_box.isChecked)
                    Log.e(
                            TAG,
                            "item_Nest_Cart.get(position).isChecked = false: " + item_Cart_Check.get(position).isChecked
                         )

                    // todo" 변경사항을 화면에 표시하기 (상품 선택, 추가)
                    index = position
                    price = item_Cart_Check.get(position).nest_Order_Price

                    item_Cart_Check.get(position).isChecked = false
                    check = item_Cart_Check.get(position).isChecked
                    Log.e(TAG, "unChecked check: " + check)

                    Log.e(TAG, "unChecked index: " + index)
                    Log.e(TAG, "unChecked price: " + price)

                    //                     체크박스로 선택 취소된 상품의 DB 인덱스와 상품 가격
                    //                    item_Cart_Check.removeAt(index)

                    //                    for (i in item_Cart_Check.indices)
                    //                    {
                    //                        Log.e(
                    //                                TAG,
                    //                                "index: " + item_Cart_Check.get(i).nest_Order_Index + " / price: " + item_Cart_Check.get(i).nest_Order_Price
                    //                             )
                    //                    }

                    totalPrice = totalPrice - price
                    totalCount = totalCount - 1

                    Log.e(TAG, "totalPrice: " + totalPrice)
                    Log.e(TAG, "totalCount: " + totalCount)

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"
                }

                // 선택하기
                else if (!holder.cart_c_check_box.isChecked)
                {
                    holder.cart_c_check_box.isChecked = true
                    item_Cart_Check.get(position).isChecked = true

                    Log.e(TAG, "holder.cart_c_check_box.isChecked: " + holder.cart_c_check_box.isChecked)
                    Log.e(
                            TAG,
                            "item_Nest_Cart.get(position).isChecked = true: " + item_Cart_Check.get(position).isChecked
                         )

                    // todo" 변경사항을 화면에 표시하기 (상품 선택, 추가)
                    //                    index = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Index)
                    //                    price = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Price)
                    index = position
                    price = item_Cart_Check.get(position).nest_Order_Price

                    item_Cart_Check.get(position).isChecked = false
                    check = item_Cart_Check.get(position).isChecked
                    Log.e(TAG, "unChecked check: " + check)

                    // 체크박스로 체크된 상품의 DB 인덱스와 상품 가격 담기
                    item_Cart_Check.add(Item_Cart_Check(index.toString(), price, true))

                    //                    for (i in item_Nest_Cart.indices)
                    //                    {
                    //                        Log.e(
                    //                                TAG,
                    //                                "index: " + item_Nest_Cart.get(i).nest_Order_Index + " / price: " + item_Nest_Cart.get(i).nest_Order_Price
                    //                             )
                    //                    }

                    Log.e(TAG, "add index: $index")
                    Log.e(TAG, "add price: $price")

                    totalPrice = totalPrice + price
                    totalCount = totalCount + 1

                    Log.e(TAG, "totalPrice: $totalPrice")
                    Log.e(TAG, "totalCount: $totalCount")

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"
                }
            })

            // todo: 하나씩 선택하기 (체크박스 클릭)
            holder.cart_c_check_box.setOnClickListener(View.OnClickListener {

                // 선택하기
                if (holder.cart_c_check_box.isChecked)
                {
                    item_Cart_Check.get(position).isChecked = true

                    Log.e(TAG, "holder.cart_c_check_box.isChecked: " + holder.cart_c_check_box.isChecked)

                    Log.e(
                            TAG,
                            "item_Nest_Cart.get(position).isChecked = false: " + item_Cart_Check.get(position).isChecked
                         )

                    // todo" 변경사항을 화면에 표시하기 (상품 선택, 추가)
                    index = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Index)
                    price = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Price)

                    // 체크박스로 체크된 상품의 DB 인덱스와 상품 가격 담기
                    item_Cart_Check.add(Item_Cart_Check(index.toString(), price, false))

                    for (i in item_Nest_Cart.indices)
                    {
                        Log.e(
                                TAG,
                                "index: " + item_Cart_Check.get(i).nest_Order_Index + " / price: " + item_Cart_Check.get(
                                        i
                                                                                                                        ).nest_Order_Price
                             )
                    }

                    Log.e(TAG, "add index: $index")
                    Log.e(TAG, "add price: $price")

                    totalPrice = totalPrice + price
                    totalCount = item_Cart_Check.size

                    Log.e(TAG, "totalPrice: $totalPrice")
                    Log.e(TAG, "totalCount: $totalCount")

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"

                }

                // 선택 취소
                else if (!holder.cart_c_check_box.isChecked)
                {
                    item_Cart_Check.get(position).isChecked = false

                    Log.e(TAG, "holder.cart_c_check_box.isChecked: " + holder.cart_c_check_box.isChecked)

                    Log.e(
                            TAG,
                            "item_Nest_Cart.get(position).isChecked = true: " + item_Cart_Check.get(position).isChecked
                         )

                    // todo" 변경사항을 화면에 표시하기 (상품 선택, 추가)
                    index = position
                    price = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Price)
                    Log.e(TAG, "remove index: " + index)
                    Log.e(TAG, "remove price: " + price)

                    // 체크박스로 선택 취소된 상품의 DB 인덱스와 상품 가격
                    item_Cart_Check.removeAt(index)

                    for (i in item_Cart_Check.indices)
                    {
                        Log.e(
                                TAG,
                                "index: " + item_Cart_Check.get(i).nest_Order_Index + " / price: " + item_Cart_Check.get(
                                        i
                                                                                                                        ).nest_Order_Price
                             )
                    }

                    totalPrice = totalPrice - price
                    totalCount = item_Cart_Check.size

                    Log.e(TAG, "totalPrice: " + totalPrice)
                    Log.e(TAG, "totalCount: " + totalCount)

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"
                }
            })
        }

        // 뷰홀더
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        {
            val item = view

            val cart_c_tumb = view.cart_c_tumb
            val cart_c_name = view.cart_c_name
            val cart_c_option = view.cart_c_option

            val cart_c_request = view.cart_c_request
            val noti_c_price = view.noti_c_price
            val cart_c_way = view.cart_c_way

            val cart_c_check_box = view.cart_c_check_box


        }
    } // 리사이클러뷰 끝
}
