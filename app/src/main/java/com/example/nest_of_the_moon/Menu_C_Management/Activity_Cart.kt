package com.example.nest_of_the_moon.Menu_C_Management

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.util.Log.e
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nest_of_the_moon.*
import com.example.nest_of_the_moon.Client.Activity_Client_Home.Companion.GET_ID
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management
import com.example.nest_of_the_moon.Menu_C_Management.Fragment_C_Order_Management.Companion.menu_c_management_cart
import com.example.nest_of_the_moon.Menu_C_Management.Fragment_Menu_ForC_Beverage.Companion.cart
import com.example.nest_of_the_moon.Menu_C_Management.Fragment_Menu_ForC_Beverage.Companion.cartCount
import com.example.nest_of_the_moon.TCP_Manager.Companion.TCPSendUerID
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.item_nest_cart.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class Activity_Cart: AppCompatActivity()
{

    var TAG: String = "Activity_Cart"
    var item_Nest_Cart = arrayListOf<Item_Cart>()
    //    var item_Cart_Check = ArrayList<Item_Cart_Check>()
    var totalPrice: Int = 0
    var totalCount: Int = 0
    var index: Int = 0
    var price: Int = 0
    var check: Boolean = true
    var switch0: Int = 0
    var selectCartDBIndex: String? = null
    var request: String? = null

    var dialog_cart_payment_complete: TextView? = null
    var dialog_cart_payment_done: TextView? = null
    var dialog: AlertDialog? = null
    var dialog_cart_payment_webview: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        client_check_all.visibility = View.GONE

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
        e(Fragment_Menu_Management.TAG, "getClientMenuList(): 장바구니 목록 불러오기")

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
                e(Fragment_Menu_Management.TAG, "chatList call onResponse = 수신 받음")

                item_Nest_Cart = response.body() as ArrayList<Item_Cart>

                // 체크박스로 선택된 상품의 DB 인덱스와 상품 가격 담기
                for (i in item_Nest_Cart.indices)
                {
                    totalPrice = totalPrice + Integer.parseInt(item_Nest_Cart.get(i).nest_Order_Price)
                }

                // 선택한 상품 갯수
                totalCount = item_Nest_Cart.size

                // 값이 잘 들어갔는지 확인하기
                for (i in item_Nest_Cart.indices)
                {
                    e(
                            TAG,
                            "nest_Order_Index: " + item_Nest_Cart.get(i).nest_Order_Index + " / nest_Order_Price: " + item_Nest_Cart.get(
                                    i
                                                                                                                                        ).nest_Order_Price
                     )
                }

                e(TAG, "totalPrice: " + totalPrice)
                e(TAG, "totalCount: " + totalCount)

                // todo: 화면에 선택한 상품 갯수 표시하기
                client_cart_item_count.text = totalCount.toString() + " 개"

                // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                client_cart_total_price.text = totalPrice.toString() + " 원"

                // 리사이클러뷰 어댑터 세팅
                client_cart_list?.adapter = CartListAdapter(applicationContext)
                client_cart_list?.setAdapter(client_cart_list?.adapter)

                // 모두 선택 && 모두 선택 취소 버튼
                // todo: 장바구니의 상품 모두 선택
                client_check_all.setOnClickListener(View.OnClickListener {
                    for (i in item_Nest_Cart.indices)
                    {
                        item_Nest_Cart.get(i).isChecked = true

                        e(
                                Fragment_Menu_Management.TAG,
                                "item_Nest_Cart.get(position).isChecked = true: " + item_Nest_Cart.get(i).isChecked
                         )
                    }

                    // 리사이클러뷰 갱신
                    client_cart_list?.adapter!!.notifyDataSetChanged()

                    // 선택한 상품 갯수
                    totalCount = item_Nest_Cart.size

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
                        item_Nest_Cart.get(i).isChecked = false

                        e(
                                Fragment_Menu_Management.TAG,
                                "item_Cart_Check.get(" + i + ").isChecked: " + item_Nest_Cart.get(i).isChecked
                         )
                    }

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

                // todo: 장바구니의 선택한 상품 삭제하기
                client_check_delete_forever.setOnClickListener(View.OnClickListener {

                    // 다이얼로그 사용방법 1
                    /* var dialog = AlertDialog.Builder(this@Activity_Cart)
                                        dialog.setTitle("삭제")
                                        dialog.setMessage("친구목록에서 지우시겠습니까?")
                                        dialog.setIcon(R.drawable.logo_1)

                                        fun toast_p()
                                        {

                                        }

                                        fun toast_n()
                                        {

                                        }

                                        var dialog_listener = object: DialogInterface.OnClickListener
                                        {
                                            override fun onClick(dialog: DialogInterface?, which: Int)
                                            {
                                                when (which)
                                                {
                                                    DialogInterface.BUTTON_POSITIVE -> toast_p()
                                                    DialogInterface.BUTTON_NEGATIVE -> toast_n()
                                                }
                                            }
                                        }

                                        dialog.setPositiveButton("삭제", dialog_listener)
                                        dialog.setNegativeButton("취소", dialog_listener)
                                        dialog.show()*/


                    // 다이얼로그 사용방법 2
                    /*

                                        val builder = AlertDialog.Builder(
                            ContextThemeWrapper(
                                    this@Activity_Cart,
                                    R.style.Theme_AppCompat_Light_Dialog
                                               )
                                                     )

                    builder.setIcon(R.drawable.logo_1)
                    builder.setTitle("삭제")
                    builder.setMessage("선택한 메뉴를 삭제합니다")

                    builder.setPositiveButton("확인") { _, _ ->
                        e(TAG, "확인")
                    }

                    builder.setNegativeButton("취소") { _, _ ->
                        e(TAG, "취소")
                    }

                    builder.show()
                    */

                    if (Integer.parseInt(cart) == 0)
                    {

                    }
                    else
                    {


                        val builder = AlertDialog.Builder(
                                ContextThemeWrapper(
                                        this@Activity_Cart, R.style.Theme_AppCompat_Light_Dialog
                                                   )
                                                         )

                        builder.setIcon(R.drawable.logo_1)
                        builder.setTitle("삭제")
                        builder.setMessage("선택한 메뉴를 삭제합니다")

                        builder.setPositiveButton("확인") { _, _ ->
                            e(TAG, "확인")

                            // DB로 전달할 삭제 인덱스
                            for (i in item_Nest_Cart.indices)
                            {
                                if (item_Nest_Cart.get(i).isChecked == true)
                                {
                                    if (selectCartDBIndex == null)
                                    {
                                        selectCartDBIndex = item_Nest_Cart.get(i).nest_Order_Index

                                        cart = (Integer.parseInt(cart) - 1).toString()

                                        e(TAG, "cart 감소: $cart")
                                    }
                                    else
                                    {
                                        selectCartDBIndex =
                                            selectCartDBIndex + ", " + item_Nest_Cart.get(i).nest_Order_Index

                                        cart = (Integer.parseInt(cart) - 1).toString()

                                        e(TAG, "cart 감소: $cart")
                                    }
                                }
                            }

                            // 장바구니에 담긴 상품 갯수 표시하기 (이전 화면의 우측 상당)
                            menu_c_management_cart?.text = "(" + cart + ")"

                            e(TAG, "cart: $cart")

                            // DB로 전달할 인덱스 확인
                            e(TAG, "selectCartDBIndex: " + selectCartDBIndex)

                            request = "remove"

                            // 서버로 삭제요청
                            CartRequest(request!!, selectCartDBIndex.toString())

                            // 선택한 상품 삭제하기
                            val iterator = item_Nest_Cart.iterator()
                            for (i in iterator)
                            {
                                if (i.isChecked == true)
                                {
                                    iterator.remove()
                                }
                            }

                            totalCount = 0
                            totalPrice = 0

                            // todo: 화면에 선택한 상품 갯수 표시하기
                            client_cart_item_count.text = totalCount.toString() + " 개"

                            // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                            client_cart_total_price.text = totalPrice.toString() + " 원"

                            // 리사이클러뷰 갱신하기
                            client_cart_list?.adapter!!.notifyDataSetChanged()
                        }

                        builder.setNegativeButton("취소") { _, _ ->
                            e(TAG, "취소")
                        }

                        builder.show()
                    }
                })

                // todo: 선택한 상품만 결제하기
                client_cart_payment_button.setOnClickListener(View.OnClickListener {
                    if (Integer.parseInt(cart) == 0)
                    {

                    }
                    else
                    {
                        val builderr = AlertDialog.Builder(
                                ContextThemeWrapper(
                                        this@Activity_Cart, R.style.Theme_AppCompat_Light_Dialog
                                                   )
                                                          )

                        builderr.setIcon(R.drawable.logo_1)
                        builderr.setTitle("결제")
                        builderr.setMessage("선택한 메뉴를 결제합니다")

                        builderr.setPositiveButton("확인") { _, _ ->
                            e(TAG, "확인")

                            // DB로 전달할 결제 인덱스
                            for (i in item_Nest_Cart.indices)
                            {
                                if (item_Nest_Cart.get(i).isChecked == true)
                                {
                                    if (selectCartDBIndex == null)
                                    {
                                        selectCartDBIndex = item_Nest_Cart.get(i).nest_Order_Index

                                        cart = (Integer.parseInt(cart) - 1).toString()

                                        e(TAG, "pay: cart 감소: $cart")
                                    }
                                    else
                                    {
                                        selectCartDBIndex =
                                            selectCartDBIndex + ", " + item_Nest_Cart.get(i).nest_Order_Index

                                        cart = (Integer.parseInt(cart) - 1).toString()

                                        e(TAG, "pay: cart 감소: $cart")
                                    }
                                }
                            }

                            // 장바구니에 담긴 상품 갯수 표시하기 (이전 화면의 우측 상당)
                            menu_c_management_cart?.text = "(" + cart + ")"

                            // DB로 전달할 인덱스 확인
                            e(TAG, "selectCartDBIndex 결제: " + selectCartDBIndex)

                            // todo: 결제 다이얼로그 (카카오페이 실행하기)
                            val builder = AlertDialog.Builder(this@Activity_Cart)
                            val inflater = LayoutInflater.from(this@Activity_Cart)
                            val dialog_view = inflater.inflate(R.layout.dialog_cart_payment, null)
                            builder.setView(dialog_view)
                            dialog = builder.create()

                            // 다이얼로그 실행
                            dialog!!.show()

                            dialog_cart_payment_webview = dialog_view.findViewById(R.id.dialog_cart_payment_webview)
                            dialog_cart_payment_complete = dialog_view.findViewById(R.id.dialog_cart_payment_complete)
                            dialog_cart_payment_done = dialog_view.findViewById(R.id.dialog_cart_payment_done)

                            // todo: 카카오 웹뷰 활성화
                            dialog_cart_payment_webview?.setWebViewClient(object: KakaoWebViewClient(this@Activity_Cart)
                            {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
                                {
                                    e(TAG, "onPageStarted")
                                    Toast.makeText(applicationContext, "결제 화면 불러오는 중", Toast.LENGTH_SHORT).show()
                                    super.onPageStarted(view, url, favicon)
                                }

                                // 웹뷰가 종료되면(결제 완료되면) 서버로 값 전송
                                override fun onPageFinished(view: WebView?, url: String?)
                                {
                                    request = "payment"

                                    e(TAG, "onPageFinished")
                                    e(TAG, "카카오페이 로드 완료")
                                    // Toast.makeText(applicationContext, "카카오페이 로드 완료", Toast.LENGTH_SHORT).show()

                                    super.onPageFinished(view, url)
                                }
                            })

                            val settings = dialog_cart_payment_webview?.getSettings()
                            settings?.setJavaScriptEnabled(true)

                            // 웹뷰에서 카카오페이 실행하기
                            dialog_cart_payment_webview?.loadUrl("http://115.68.231.84/addJoin_kakao.php?amount=" + totalPrice) // 카카오페이로 입력값 보내기
                            e(TAG, "결제할 금액: $totalPrice")

                        }

                        builderr.setNegativeButton("취소") { _, _ ->
                            e(TAG, "취소")
                        }

                        builderr.show()
                    }
                })
            }

            override fun onFailure(call: Call<List<Item_Cart>>, t: Throwable)
            {
                Toast.makeText(Fragment_Menu_Management.mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
            }
        })
    }

    override fun onStop()
    {
        super.onStop()


        if (request == "payment")
        {
            // todo: 서버로 구매요청
            CartRequest(request!!, selectCartDBIndex.toString())
            dialog_cart_payment_webview?.visibility = View.GONE
        }

        // 결제 완료 메시지, 버튼 띄우기
        dialog_cart_payment_complete?.visibility = View.VISIBLE
        dialog_cart_payment_done?.visibility = View.VISIBLE

        // 다이얼로그 닫기 버튼
        dialog_cart_payment_done?.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
        })
    }

    // todo: 삭제 or 구매요청
    fun CartRequest(requestType: String, index: String)
    {
        val stringRequest = object: StringRequest(Request.Method.POST,
                "http://115.68.231.84/getNestCartProcessingRequest.php",
                com.android.volley.Response.Listener { response ->
                    e(TAG, "onResponse: response = $response")

                    try
                    {
                        val jsonObject = JSONObject(response)

                        val success = jsonObject.getString("success")
                        val serial = jsonObject.getString("serial")

                        e(TAG, "serial: $serial")

                        if (success == "1")
                        {
                            e(TAG, "onResponse: success")

                            // todo: 신규 주문 신호 발신
                            var message: String? = "newOrderRequest│" + serial
                            TCP_Manager.send = TCP_Manager.SendThreadd(TCP_Manager.socket!!, message!!)
                            e(TAG, " ")
                            e(TAG, " ===== SendThread ===== ")
                            e(TAG, " message: $message")
                            e(TAG, " socket: ${TCP_Manager.socket}")
                            e(TAG, " ===== ======= ===== ")
                            e(TAG, " ")
                            TCP_Manager.RoomNo = "777"
                            TCP_Manager.UserType = "Client"
                            TCPSendUerID = GET_ID
                            TCP_Manager.send?.start()

                            // 구매 완료 처리
                            if (requestType == "payment")
                            {
                                // 구매한 상품 화면에서 치우기
                                val iterator = item_Nest_Cart.iterator()
                                for (i in iterator)
                                {
                                    if (i.isChecked == true)
                                    {
                                        iterator.remove()
                                    }
                                }

                                totalCount = 0
                                totalPrice = 0

                                // todo: 화면에 선택한 상품 갯수 표시하기
                                client_cart_item_count.text = totalCount.toString() + " 개"

                                // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                                client_cart_total_price.text = totalPrice.toString() + " 원"

                                // 리사이클러뷰 갱신하기
                                client_cart_list?.adapter!!.notifyDataSetChanged()

                                request = null
                            }
                        }
                        else
                        {
                            e(TAG, "onResponse: 문제발생")
                        }
                    }
                    catch (e: JSONException)
                    {
                        e.printStackTrace()
                        e(TAG, "onResponse: JSONException e: $e")
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    e(TAG, "onErrorResponse: error: $error")
                })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()

                params.put("CartRequestCode", requestType)
                params.put("CartDBIndex", index)

                if (requestType == "payment")
                {
                    params.put("CartTotalPrice", totalPrice.toString())
                    params.put("CartTotalCount", totalCount.toString())
                    params.put("nest_Order_User_Index", GET_ID.toString())

                    e(TAG, "CartTotalPrice: $totalPrice")
                    e(TAG, "CartTotalCount: $totalCount")
                    e(TAG, "nest_Order_User_Index: $GET_ID")

                }

                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest) // stringRequest = 바로 위에 회원가입 요청메소드 실행
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
            var count = position + 1

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
            holder.cart_c_way.text = item_Nest_Cart.get(position).nest_Order_Serving_Way + "   #" + count.toString()

            // todo: 체크박스 처리
            // 체크박스 세팅 (기본 true로 표시함)
            if (item_Nest_Cart.get(position).isChecked)
            {
                holder.cart_c_check_box.setImageResource(R.drawable.ic_check_all)
            }
            else /*if (!item_Cart_Check.get(position).isChecked)*/
            {
                holder.cart_c_check_box.setImageResource(R.drawable.ic_un_check_all)
            }

            // todo: 하나씩 선택하기 (목록 클릭)
            holder.item.setOnClickListener(View.OnClickListener {

                // 선택 취소
                if (item_Nest_Cart.get(position).isChecked == true)
                {
                    e(TAG, "선택 취소")

                    item_Nest_Cart.get(position).isChecked = false

                    // todo" 변경사항을 화면에 표시하기 (상품 선택, 추가)
                    index = position
                    price = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Price)

                    item_Nest_Cart.get(position).isChecked = false
                    check = item_Nest_Cart.get(position).isChecked

                    totalPrice = totalPrice - price
                    totalCount = totalCount - 1

                    e(TAG, "unChecked index: " + index)
                    e(TAG, "unChecked price: " + price)

                    e(TAG, "totalPrice: " + totalPrice)
                    e(TAG, "totalCount: " + totalCount)

                    e(TAG, "unChecked check: " + check)

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"

                    // todo: 리사이클러뷰 갱신하기
                    client_cart_list?.adapter!!.notifyDataSetChanged()
                }

                // 선택하기
                else if (item_Nest_Cart.get(position).isChecked == false)
                {
                    e(TAG, "선택 하기")

                    item_Nest_Cart.get(position).isChecked = true

                    // todo" 변경사항을 화면에 표시하기 (상품 선택, 추가)
                    index = position
                    price = Integer.parseInt(item_Nest_Cart.get(position).nest_Order_Price)

                    totalPrice = totalPrice + price
                    totalCount = totalCount + 1

                    e(TAG, "add index: $index")
                    e(TAG, "add price: $price")

                    e(TAG, "add index: " + item_Nest_Cart.get(position).nest_Order_Index)

                    e(TAG, "totalPrice: $totalPrice")
                    e(TAG, "totalCount: $totalCount")

                    e(TAG, "unChecked check: " + item_Nest_Cart.get(position).isChecked)

                    // todo: 화면에 선택한 상품 갯수 표시하기
                    client_cart_item_count.text = totalCount.toString() + " 개"

                    // todo: 화면에 선택한 상품의 총 합산된 가격 표시하기
                    client_cart_total_price.text = totalPrice.toString() + " 원"

                    // todo: 리사이클러뷰 갱신하기
                    client_cart_list?.adapter!!.notifyDataSetChanged()
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
            //            val cart_item_count = view.cart_item_count


            val cart_c_check_box = view.cart_c_check_box


        }
    } // 리사이클러뷰 끝
}
