package com.example.nest_of_the_moon.Menu_C_Management

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nest_of_the_moon.*
import com.example.nest_of_the_moon.Client.Activity_Client_Home.Companion.GET_ID
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.mContext
import com.example.nest_of_the_moon.Menu_B_Management.Item_Nest_Menu
import com.example.nest_of_the_moon.Menu_C_Management.Fragment_C_Order_Management.Companion.menu_c_management_cart
import com.example.nest_of_the_moon.Menu_C_Management.Fragment_C_Order_Management.Companion.menu_c_management_cart_button

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_menu_detail.view.*
import kotlinx.android.synthetic.main.item_nest_menu_c.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 */

class Fragment_Menu_ForC_Beverage: Fragment()
{
    var TAG: String = "Fragment_Menu_ForC_Beverage"

    var dialog_menu_detail_payment_complete: TextView? = null
    var dialog_menu_detail_order_detail_page: TextView? = null

    var nest_Order_Way: String? = null

    var mContext_Fragment_Menu_ForC_Beverage: Context? = null
    var menu_c_beverage_recycler_view: RecyclerView? = null
    var item_Nest_Menu_Client = arrayListOf<Item_Nest_Menu>()

    var dialog_menu_detail_webview: WebView? = null

    companion object
    {
        var cart: String? = null
        var cartCount: Int? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment__menu__for_c__beverage, container, false)

        // mContext_Fragment_Menu_ForC_Beverage = mContext_Fragment_Menu_ForC_Beverage?.applicationContext

        mContext_Fragment_Menu_ForC_Beverage = requireContext()

        // 리사이클러뷰
        menu_c_beverage_recycler_view = view.findViewById(R.id.menu_c_beverage_recycler_view) as RecyclerView
        menu_c_beverage_recycler_view!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

//        // 리사이클러뷰 구분선 넣기 세로
//        menu_c_beverage_recycler_view!!.addItemDecoration(
//                DividerItemDecoration(
//                        requireContext(), DividerItemDecoration.VERTICAL
//                                     )
//                                                         )

        // 리사이클러뷰 구분선 넣기 가로
        menu_c_beverage_recycler_view!!.addItemDecoration(
                DividerItemDecoration(
                        requireContext(), DividerItemDecoration.HORIZONTAL
                                     )
                                                         )

        // todo: 서버로 메뉴 목록 요청 후 리사이클러뷰에 메뉴 세팅하기
        getClientMenuList("beverage") // beverage

        return view
    }

    // todo: 서버로 메뉴 목록 요청 후 리사이클러뷰에 메뉴 세팅하기
    fun getClientMenuList(getNestMenuRequest: String)
    {
        mContext_Fragment_Menu_ForC_Beverage = mContext_Fragment_Menu_ForC_Beverage!!.applicationContext

        Log.e(Fragment_Menu_Management.TAG, "getClientMenuList(): 메뉴 목록 불러오기")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val projectListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = projectListRequest.getNestMenuList_Client(getNestMenuRequest, GET_ID.toString())

        listCall.enqueue(object: Callback<List<Item_Nest_Menu>>
        {
            override fun onResponse(call: Call<List<Item_Nest_Menu>>, response: Response<List<Item_Nest_Menu>>)
            {
                Log.e(Fragment_Menu_Management.TAG, "list call onResponse = 수신 받음")

                item_Nest_Menu_Client = response.body() as ArrayList<Item_Nest_Menu>

                // 넘어온 값 확인하기
                for (i in item_Nest_Menu_Client.indices)
                {
                    //                    Log.e(
                    //                            Fragment_Menu_Management.TAG,
                    //                            "onResponse: BroadCastTitle: " + item_Noti.get(i).Menu_Name
                    //                         )

                    Log.e(
                            Fragment_Menu_Management.TAG,
                            "onResponse: Menu_Serving_Size: " + item_Nest_Menu_Client.get(i).Menu_Serving_Size
                         )
                }

                cart = item_Nest_Menu_Client.get(0).cartCount
                menu_c_management_cart?.text = "(" + cart + ")"

                // 장바구니 페이지로 이동하기
                menu_c_management_cart_button?.setOnClickListener(View.OnClickListener {
                    val i = Intent(mContext_Fragment_Menu_ForC_Beverage, Activity_Cart::class.java)
                    mContext_Fragment_Menu_ForC_Beverage?.startActivity(i)
                })

                menu_c_beverage_recycler_view?.adapter = NestClientMenuAdapter(mContext_Fragment_Menu_ForC_Beverage)
                menu_c_beverage_recycler_view?.setAdapter(menu_c_beverage_recycler_view?.adapter)
            }

            override fun onFailure(call: Call<List<Item_Nest_Menu>>, t: Throwable)
            {
                Toast.makeText(mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                Log.e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
            }
        })
    }

    inner class NestClientMenuAdapter(val context: Context?): RecyclerView.Adapter<NestClientMenuAdapter.ViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_nest_menu_c, parent, false))
        }

        override fun getItemCount(): Int
        {
            return item_Nest_Menu_Client.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            // 썸네일 이미지
            Picasso.get().load(item_Nest_Menu_Client.get(position).Menu_Thumb)
                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(holder.menu_c_thumb) // this인 ImageView에 로드

            // 메뉴 이름
            holder.menu_c_name.text = item_Nest_Menu_Client.get(position).Menu_Name

            // 메뉴 가격
            holder.menu_c_price.text = item_Nest_Menu_Client.get(position).Menu_Price + "원"

            // 메뉴 가격
            holder.menu_c_serving_size.text = item_Nest_Menu_Client.get(position).Menu_Price + "ml"

            // 제공 방법
            holder.menu_c_serving_way.text = item_Nest_Menu_Client.get(position).Menu_Serving_Way + "\t"

            // 상품 용량 표시
            if (item_Nest_Menu_Client.get(position).Menu_Serving_Size.equals("none"))
            {
                holder.menu_c_serving_size.visibility = View.GONE
            }
            else if (item_Nest_Menu_Client.get(position).Menu_Serving_Size.equals(""))
            {
                holder.menu_c_serving_size.visibility = View.GONE
            }
            else
            {
                holder.menu_c_serving_size.visibility = View.VISIBLE
                holder.menu_c_serving_size.text = item_Nest_Menu_Client.get(position).Menu_Price + "ml"
            }

            // 상품 정보 상세보기와 구매를 선택할 수 있는 다이얼로그
            holder.item.setOnClickListener(View.OnClickListener {


                // todo: 주문 선택 다이얼로그
                val builder = AlertDialog.Builder(requireContext())
                val inflater = LayoutInflater.from(requireContext())
                val dialog_view = inflater.inflate(R.layout.dialog_menu_detail, null)
                builder.setView(dialog_view)
                var dialog = builder.create()

                // 다이얼로그 실행
                dialog.show()

                var dialog_menu_detail_thumb: ImageView = dialog_view.findViewById(R.id.dialog_menu_detail_thumb)
                var dialog_menu_detail_name: TextView = dialog_view.findViewById(R.id.dialog_menu_detail_name)
                var dialog_menu_detail_price: TextView = dialog_view.findViewById(R.id.dialog_menu_detail_price)

                var dialog_menu_detail_serving_size_noti: TextView =
                    dialog_view.findViewById(R.id.dialog_menu_detail_serving_size_noti)

                var dialog_menu_detail_serving_size: TextView =
                    dialog_view.findViewById(R.id.dialog_menu_detail_serving_size)
                var dialog_menu_detail_spinner: Spinner = dialog_view.findViewById(R.id.dialog_menu_detail_spinner)
                var dialog_menu_detail_hot_and_ice: Spinner =
                    dialog_view.findViewById(R.id.dialog_menu_detail_hot_and_ice)
                var dialog_menu_detail_order_option: Spinner =
                    dialog_view.findViewById(R.id.dialog_menu_detail_order_option)

                var dialog_menu_detail_pick_up_recommend_time: TextView =
                    dialog_view.findViewById(R.id.dialog_menu_detail_pick_up_recommend_time)

                var dialog_menu_detail_serving_impossible_time: TextView =
                    dialog_view.findViewById(R.id.dialog_menu_detail_serving_impossible_time)

                var dialog_menu_detail_produce: TextView = dialog_view.findViewById(R.id.dialog_menu_detail_produce)
                var dialog_menu_detail_order_option_list: TextView =
                    dialog_view.findViewById(R.id.dialog_menu_detail_order_option_list)
                var dialog_menu_detail_order_option_total_price: TextView =
                    dialog_view.findViewById(R.id.dialog_menu_detail_order_option_total_price)

                var dialog_menu_detail_add_cart: TextView = dialog_view.findViewById(R.id.dialog_menu_detail_add_cart)

                var dialog_menu_detail_order_now: TextView = dialog_view.findViewById(R.id.dialog_menu_detail_order_now)
                var dialog_menu_detail_option_request: EditText =
                    dialog_view.findViewById(R.id.dialog_menu_detail_option_request)

                // 결제 진행시 다이얼로그에서 진행함
                dialog_menu_detail_webview = dialog_view.findViewById(R.id.dialog_menu_detail_webview)
                var dialog_menu_detail_bottom_button_area: LinearLayout = dialog_view.findViewById(R.id.dialog_menu_detail_bottom_button_area)
                dialog_menu_detail_payment_complete = dialog_view.findViewById(R.id.dialog_menu_detail_payment_complete)
                dialog_menu_detail_order_detail_page = dialog_view.findViewById(R.id.dialog_menu_detail_order_detail_page)

                Picasso.get().load(item_Nest_Menu_Client.get(position).Menu_Thumb)
                    .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                    //.resize(300,300)       // 300x300 픽셀
                    .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                    .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                    .into(dialog_menu_detail_thumb) // this인 ImageView에 로드

                dialog_menu_detail_name.text = item_Nest_Menu_Client.get(position).Menu_Name
                dialog_menu_detail_price.text = item_Nest_Menu_Client.get(position).Menu_Price + "원"

                // 가격 설정
                var priceOption = item_Nest_Menu_Client.get(position).Menu_Price

                dialog_menu_detail_order_option_total_price.text = "총 결제금액: " + priceOption + "원"

                // 상품 용량 표시
                if (item_Nest_Menu_Client.get(position).Menu_Serving_Size.equals("none"))
                {
                    dialog_menu_detail_serving_size_noti.visibility = View.GONE
                    dialog_menu_detail_serving_size.visibility = View.GONE
                }
                else if (item_Nest_Menu_Client.get(position).Menu_Serving_Size.equals(""))
                {
                    dialog_menu_detail_serving_size_noti.visibility = View.GONE
                    dialog_menu_detail_serving_size.visibility = View.GONE
                }
                else
                {
                    dialog_menu_detail_serving_size.text = item_Nest_Menu_Client.get(position).Menu_Serving_Size + " ml"
                }

                dialog_menu_detail_pick_up_recommend_time.text =
                    item_Nest_Menu_Client.get(position).Menu_Pick_Up_Recommend_Time + "분"

                dialog_menu_detail_serving_impossible_time.text =
                    item_Nest_Menu_Client.get(position).Menu_Serving_Impossible_Time + "분"

                dialog_menu_detail_produce.text = item_Nest_Menu_Client.get(position).Menu_Product

                // todo: 스피너 세팅: 주문옵션
                val orderOption = java.util.ArrayList<Any>()
                orderOption.add("옵션 없음")
                orderOption.add("시럽 추가")
                orderOption.add("휘핑크림 추가")
                orderOption.add("샷 추가 ( + 500원)")

                dialog_menu_detail_order_option_list.text = "옵션 없음"

                var Menu_Option: String? = null

                val orderOptionAdapter = ArrayAdapter(
                        requireContext(), R.layout.spinner_item, orderOption
                                                     )
                // dialog_menu_detail_price.text = priceOption.toString()

                var menu: String? = null

                dialog_menu_detail_order_option.adapter = orderOptionAdapter
                dialog_menu_detail_order_option.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
                {
                    override fun onNothingSelected(parent: AdapterView<*>?)
                    {
                        Log.e(TAG, "onNothingSelected: $position")
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                    {
                        if (position == 0)
                        {
                            priceOption = item_Nest_Menu_Client.get(position).Menu_Price
                            dialog_menu_detail_order_option_total_price.text = "총 결제금액: " + priceOption + "원"
                            Log.e(TAG, "priceOption: $priceOption")

                            dialog_menu_detail_order_option_list.text = "옵션 없음"

                            // 서버로 전송할 메뉴 옵션
                            Menu_Option = "옵션 없음"
                            Log.e(TAG, "Menu_Option: $Menu_Option")

                            menu = ""
                        }
                        else if (position == 1)
                        {
                            if (menu!!.contains("시럽 추가"))
                            {
                                // 이미 선택한 메뉴는 더 추가되지 않게 막았음
                            }
                            else
                            {
                                priceOption = item_Nest_Menu_Client.get(position).Menu_Price
                                dialog_menu_detail_order_option_total_price.text = "총 결제금액: " + priceOption + "원"
                                Log.e(TAG, "priceOption: $priceOption")

                                menu = menu + "시럽 추가"

                                dialog_menu_detail_order_option_list.text = menu

                                // 서버로 전송할 옵션 내용
                                Menu_Option = menu
                                Log.e(TAG, "Menu_Option: $Menu_Option")

                                menu = menu + "\n"
                            }
                        }
                        else if (position == 2)
                        {
                            if (menu!!.contains("휘핑크림 추가"))
                            {
                                // 이미 선택한 메뉴는 더 추가되지 않게 막았음
                            }
                            else
                            {
                                priceOption = item_Nest_Menu_Client.get(position).Menu_Price
                                dialog_menu_detail_order_option_total_price.text = "총 결제금액: " + priceOption + "원"
                                Log.e(TAG, "priceOption: $priceOption")

                                menu = menu + "휘핑크림 추가"

                                dialog_menu_detail_order_option_list.text = menu

                                // 서버로 전송할 메뉴 옵션
                                Menu_Option = menu
                                Log.e(TAG, "Menu_Option: $Menu_Option")

                                menu = menu + "\n"
                            }
                        }
                        else if (position == 3)
                        {
                            if (menu!!.contains("샷 추가"))
                            {
                                // 이미 선택한 메뉴는 더 추가되지 않게 막았음
                            }
                            else
                            {
                                // 샷 추가시 가격 상향됨
                                var increasePrice: Int =
                                    Integer.parseInt(item_Nest_Menu_Client.get(position).Menu_Price) + 500
                                priceOption = increasePrice.toString()
                                dialog_menu_detail_order_option_total_price.text = "총 결제금액: " + priceOption + "원"
                                Log.e(TAG, "priceOption: $priceOption")

                                menu = menu + "샷 추가"
                                dialog_menu_detail_order_option_list.text = menu

                                // 서버로 전송할 메뉴 옵션
                                Menu_Option = menu
                                Log.e(TAG, "Menu_Option: $Menu_Option")

                                menu = menu + "\n"
                            }
                        }
                    }
                }

                // todo: 스피너 세팅: HOT / ICE 선택
                val servingWay = java.util.ArrayList<Any>()

                var selectServingWay: String? = null

                // 제공 방법에 따라 선택지가 달라짐. HOT / ICE 두 선택지 활성화 혹은 둘 중 하나만 활성화 됨
                if (item_Nest_Menu_Client.get(position).Menu_Serving_Way.equals("HOT / ICE"))
                {
                    servingWay.add("HOT")
                    servingWay.add("ICE")

                    selectServingWay = "HOT"
                    Log.e(TAG, "HOT")
                }
                else if (item_Nest_Menu_Client.get(position).Menu_Serving_Way.equals("HOT"))
                {
                    servingWay.add("해당 메뉴는 HOT만 제공합니다")

                    selectServingWay = "HOT"
                    Log.e(TAG, "HOT")
                }
                else if (item_Nest_Menu_Client.get(position).Menu_Serving_Way.equals("ICE"))
                {
                    servingWay.add("해당 메뉴는 ICE만 제공합니다")

                    selectServingWay = "ICE"
                    Log.e(TAG, "ICE")
                }

                // 스피너에서 요청사항 선택하기
                val servingWayArrayAdapter = ArrayAdapter(
                        requireContext(), R.layout.spinner_item, servingWay
                                                         )
                dialog_menu_detail_hot_and_ice.adapter = servingWayArrayAdapter
                dialog_menu_detail_hot_and_ice.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
                {
                    override fun onNothingSelected(parent: AdapterView<*>?)
                    {
                        Log.e(TAG, "onNothingSelected: $position")
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                    {
                        if (position == 0)
                        {
                            selectServingWay = "HOT"
                            Log.e(TAG, "HOT선택: position: $position")
                        }
                        else if (position == 1)
                        {
                            selectServingWay = "ICE"
                            Log.e(TAG, "ICE 선택: position: $position")
                        }
                    }
                }

                // todo: 스피너 세팅: 요청사항
                val endDatePickList = java.util.ArrayList<Any>()
                endDatePickList.add("요청 없음")
                endDatePickList.add("캐리어 안에 담아주세요")
                endDatePickList.add("너무 뜨겁지 않게 약간 식혀주세요")
                endDatePickList.add("시럽 0.5방울만 넣어주세요")
                endDatePickList.add("직접 입력")

                val arrayAdapter = ArrayAdapter(
                        requireContext(), R.layout.spinner_item, endDatePickList
                                               )

                var User_Request = "요청 없음"

                // 스피너에서 요청사항 선택하기
                dialog_menu_detail_spinner.adapter = arrayAdapter
                dialog_menu_detail_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
                {
                    override fun onNothingSelected(parent: AdapterView<*>?)
                    {
                        Log.e(TAG, "onNothingSelected: $position")
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
                    {
                        if (position == 0)
                        {
                            dialog_menu_detail_option_request.visibility = View.GONE
                            User_Request = "요청 없음"
                            Log.e(TAG, "User_Request: $User_Request")
                        }
                        else if (position == 1)
                        {
                            dialog_menu_detail_option_request.visibility = View.GONE
                            User_Request = "캐리어 안에 담아주세요"
                            Log.e(TAG, "User_Request: $User_Request")
                        }
                        else if (position == 2)
                        {
                            dialog_menu_detail_option_request.visibility = View.GONE
                            User_Request = "너무 뜨겁지 않게 약간 식혀주세요"
                            Log.e(TAG, "User_Request: $User_Request")
                        }
                        else if (position == 3)
                        {
                            dialog_menu_detail_option_request.visibility = View.GONE
                            User_Request = "시럽 0.5방울만 넣어주세요"
                            Log.e(TAG, "User_Request: $User_Request")
                        }
                        else if (position == 4)
                        {
                            dialog_menu_detail_option_request.visibility = View.VISIBLE
                            // 직접입력
                            dialog_menu_detail_option_request.addTextChangedListener(object: TextWatcher
                            {

                                override fun afterTextChanged(s: Editable)
                                {
                                }

                                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                                {
                                }

                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                                {
                                    User_Request = dialog_menu_detail_option_request.text.toString()
                                    Log.e(TAG, "User_Request: $User_Request")
                                }
                            })
                        }
                    }
                }

                // todo: 장바구니에 담기
                dialog_menu_detail_add_cart.setOnClickListener(View.OnClickListener {

                    nest_Order_Way = "addCart"

                    val stringRequest = object: StringRequest(Method.POST,
                            "http://115.68.231.84/getNestOrderInfo.php",
                            com.android.volley.Response.Listener { response ->
                                Log.e(TAG, "onResponse: response = $response")

                                try
                                {
                                    val jsonObject = JSONObject(response)

                                    val success = jsonObject.getString("success")

                                    if (success == "1")
                                    {
                                        Log.e(TAG, "장바구니에 담김")

                                        Toast.makeText(activity,"장바구니에 담김",Toast.LENGTH_SHORT).show();

                                        // 장바구니 갯수 추가
//                                        cartCount = Integer.parseInt(cart) + 1

                                        cart = (Integer.parseInt(cart) + 1).toString()

                                        // 장바구니에 담긴 상품 갯수 표시하기
                                        menu_c_management_cart?.text = "(" + cart + ")"

                                        Log.e(TAG, "cart: $cart")

                                        dialog.dismiss()
                                    }
                                    else
                                    {
                                        Log.e(TAG, "문제 발생")
                                        Toast.makeText(activity,"response: $response",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (e: JSONException)
                                {
                                    e.printStackTrace()
                                    Log.e(TAG, "onResponse: JSONException e: $e")
                                    Toast.makeText(activity,"onResponse: JSONException e: $e",Toast.LENGTH_SHORT).show();
                                }
                            },
                            com.android.volley.Response.ErrorListener { error ->
                                Log.e(TAG, "onErrorResponse: error: $error")
                                Toast.makeText(activity,"onErrorResponse: error: $error",Toast.LENGTH_SHORT).show();
                            })
                    {
                        @Throws(AuthFailureError::class)
                        override fun getParams(): Map<String, String>
                        {
                            val params = HashMap<String, String>()

                            /** todo: 클라에서 전송할 값
                             *
                             * 1. 주문하는 유저 인덱스
                             * 2. 주문 방법 (장바구니(addCart), 단일 메뉴 주문장바구니(orderNow))
                             * 3. 상품 정보 인덱스
                             * 4. 제공 방법 (HOT / ICE / Beans)
                             * 5. 주문 옵션
                             *
                             * 6. 요청사항
                             * 7. 결제할 금액
                             */

                            params.put("nest_Order_User_Index", GET_ID.toString())
                            params.put("nest_Order_Way", nest_Order_Way!!)
                            params.put("nest_Order_Menu_Index", item_Nest_Menu_Client.get(position).Menu_No)
                            params.put("nest_Order_Serving_Way", selectServingWay.toString())
                            params.put("nest_Order_Menu_Option", Menu_Option.toString())

                            params.put("nest_Order_User_Request", User_Request)
                            params.put("nest_Order_Price", priceOption)

//                            var asasd = item_Noti.get(position).Menu_No

//                            Log.e(TAG, "GET_ID: $GET_ID")
//                            Log.e(TAG, "nest_Order_Way: $nest_Order_Way")
//                            Log.e(TAG, "nest_Order_Menu_Index: $asasd")
//                            Log.e(TAG, "nest_Order_Serving_Way: $selectServingWay")
//                            Log.e(TAG, "nest_Order_Menu_Option: $Menu_Option")

//                            Log.e(TAG, "nest_Order_User_Request: $User_Request")
//                            Log.e(TAG, "nest_Order_Price: $priceOption")

                            return params
                        }
                    }

                    val requestQueue = Volley.newRequestQueue(mContext_Fragment_Menu_ForC_Beverage)
                    requestQueue.add(stringRequest)

                })

                // todo: 바로 결제 버튼
                dialog_menu_detail_order_now.setOnClickListener(View.OnClickListener {

                    // 바로 주문
                    nest_Order_Way = "orderNow"

                    dialog_menu_detail_webview?.visibility = View.VISIBLE
                    dialog_menu_detail_bottom_button_area.visibility = View.GONE

                    // todo: 카카오 웹뷰 활성화
                    dialog_menu_detail_webview?.setWebViewClient(object: KakaoWebViewClient(requireActivity())
                    {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
                        {
                            Log.e(TAG, "onPageStarted")
                            Toast.makeText(requireContext(), "결제 화면 불러오는 중", Toast.LENGTH_SHORT).show()
                            super.onPageStarted(view, url, favicon)
                        }

                        // 웹뷰가 종료되면(결제 완료되면) 서버로 값 전송
                        override fun onPageFinished(view: WebView?, url: String?)
                        {

                            Log.e(TAG, "onPageFinished")

//                            Toast.makeText(requireContext(), "카카오페이 로드 완료", Toast.LENGTH_SHORT).show()

                            val stringRequest = object: StringRequest(Method.POST,
                                    "http://115.68.231.84/getNestOrderInfo.php",
                                    com.android.volley.Response.Listener { response ->
                                        Log.e(TAG, "onResponse: response = $response")

                                        try
                                        {
                                            val jsonObject = JSONObject(response)

                                            val success = jsonObject.getString("success")

                                            if (success == "1")
                                            {
                                                Log.e(TAG, "주문 완료")
                                            }

                                            else
                                            {
                                                Log.e(TAG, "문제 발생")
                                                Toast.makeText(activity,"response: $response",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        catch (e: JSONException)
                                        {
                                            e.printStackTrace()
                                            Log.e(TAG, "onResponse: JSONException e: $e")
                                            Toast.makeText(activity,"onResponse: JSONException e: $e",Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    com.android.volley.Response.ErrorListener { error ->
                                        Log.e(TAG, "onErrorResponse: error: $error")
                                        Toast.makeText(activity,"onErrorResponse: error: $error",Toast.LENGTH_SHORT).show();
                                    })
                            {
                                @Throws(AuthFailureError::class)
                                override fun getParams(): Map<String, String>
                                {
                                    val params = HashMap<String, String>()

                                    /** todo: 클라에서 서버로 전송할 값
                                     *
                                     * 1. 주문하는 유저 인덱스
                                     * 2. 주문 방법 (장바구니(addCart), 단일 메뉴 주문장바구니(orderNow))
                                     * 3. 상품 정보 인덱스
                                     * 4. 제공 방법 (HOT / ICE / Beans)
                                     * 5. 주문 옵션
                                     *
                                     * 6. 요청사항
                                     * 7. 결제할 금액
                                     */

                                    params.put("nest_Order_User_Index", GET_ID.toString())
                                    params.put("nest_Order_Way", nest_Order_Way!!)
                                    params.put("nest_Order_Menu_Index", item_Nest_Menu_Client.get(position).Menu_No)
                                    params.put("nest_Order_Serving_Way", selectServingWay.toString())
                                    params.put("nest_Order_Menu_Option", Menu_Option.toString())

                                    params.put("nest_Order_User_Request", User_Request)
                                    params.put("nest_Order_Price", priceOption)

                                    return params
                                }
                            }

                            val requestQueue = Volley.newRequestQueue(mContext_Fragment_Menu_ForC_Beverage)
                            requestQueue.add(stringRequest)

                            super.onPageFinished(view, url)
                        }
                    })

                    val settings = dialog_menu_detail_webview?.getSettings()
                    settings?.setJavaScriptEnabled(true)

                    // 웹뷰에서 카카오페이 실행하기
                    dialog_menu_detail_webview?.loadUrl("http://115.68.231.84/addJoin_kakao.php?amount=" + priceOption) // 카카오페이로 입력값 보내기

                    // 다이얼로그 닫기
                    dialog_menu_detail_order_detail_page?.setOnClickListener(View.OnClickListener {
                        dialog.dismiss()
                    })
                })
            }) // 상세 페이지 (다이얼로그) 끝
        }

        // 뷰홀더
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        {
            val item = view
            val menu_c_thumb = view.menu_c_thumb
            val menu_c_name = view.menu_c_name
            val menu_c_price = view.menu_c_price
            val menu_c_serving_size = view.menu_c_serving_size
            val menu_c_serving_way = view.menu_c_serving_way
            val dialog_menu_detail_hot_and_ice = view.dialog_menu_detail_hot_and_ice
        }
    } // 리사이클러뷰 끝

    override fun onStop()
    {
        Log.e(TAG, "onStop")

        if (!nest_Order_Way.equals(null))
        {
            // 결제 완료 알림화면 활성화
            dialog_menu_detail_payment_complete!!.visibility = View.VISIBLE
            dialog_menu_detail_order_detail_page!!.visibility = View.VISIBLE

            // 웹뷰가 클릭되지 않게 막기
            dialog_menu_detail_webview!!.visibility = View.GONE
        }

        super.onStop()
    }
}

