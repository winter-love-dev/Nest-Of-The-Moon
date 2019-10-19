package com.example.nest_of_the_moon.Menu_B_Management


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nest_of_the_moon.ApiClient.BASE_URL
import com.example.nest_of_the_moon.ApiInterface
import com.example.nest_of_the_moon.Client.Activity_Client_Home.Companion.sessionManager

import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.SessionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_menu_management.view.*
import kotlinx.android.synthetic.main.item_nest_menu_b.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import com.example.nest_of_the_moon.R.drawable.item_switch_theme as item_switch_theme1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_Menu_Management: Fragment()
{
    var add_Menu_intent: Intent? = null
    var menu_management_add_menu: TextView? = null
    var menu_management_sort: TextView? = null
    var dialog: Dialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu_management, container, false)

        mContext = context?.applicationContext

        // view Find
        menu_management_add_menu = view.findViewById(R.id.menu_management_add_menu)
        menu_management_sort = view.findViewById(R.id.menu_management_sort)

        // 정렬 다이얼로그 코드
        SortDialog()

        // 다이얼로그 실행
        view.menu_management_sort.setOnClickListener(View.OnClickListener {
            dialog?.show()
        })

        // 메뉴 추가, 수정
        add_Menu_intent = Intent(mContext, Activity_Create_Menu::class.java)
        view.menu_management_add_menu.setOnClickListener(View.OnClickListener {

            // 작업 모드 선택: Create = 새로 생성 / Edit = 기존 정보 수정
            intoTheActivityCreateMode = "Create"

            // 메뉴 추가하기
            mContext?.startActivity(add_Menu_intent)
        })

        // 리사이클러뷰
        menu_recycler_view = view.findViewById(R.id.menu_recycler_view!!) as RecyclerView
        menu_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        // menu_recycler_view.adapter = CartListAdapter(requireContext())

        // todo: 서버로 메뉴 목록 요청 후 리사이클러뷰에 메뉴 세팅하기
        getMenuList(SortRequest!!)

        return view
    }

    // 정렬 다이얼로그
    private fun SortDialog()
    {
        // var dialog: Dialog? = null
        // todo: 메뉴목록 정렬 설정 다이얼로그
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialog_view = inflater.inflate(R.layout.dialog_sort_menu_management, null)
        builder.setView(dialog_view)

        dialog = builder.create()

        // 정렬 다이얼로그 라디오그룹
        var radio_group_menu_management_sort: RadioGroup
        var radio_button_1_menu_management_sort: RadioButton
        var radio_button_2_menu_management_sort: RadioButton
        var radio_button_3_menu_management_sort: RadioButton
        var radio_button_4_menu_management_sort: RadioButton
        var radio_button_5_menu_management_sort: RadioButton
        var radio_button_6_menu_management_sort: RadioButton

        radio_group_menu_management_sort = dialog_view.findViewById(R.id.radio_group_menu_management_sort)
        radio_button_1_menu_management_sort = dialog_view.findViewById(R.id.radio_button_1_menu_management_sort)
        radio_button_2_menu_management_sort = dialog_view.findViewById(R.id.radio_button_2_menu_management_sort)
        radio_button_3_menu_management_sort = dialog_view.findViewById(R.id.radio_button_3_menu_management_sort)
        radio_button_4_menu_management_sort = dialog_view.findViewById(R.id.radio_button_4_menu_management_sort)
        radio_button_5_menu_management_sort = dialog_view.findViewById(R.id.radio_button_5_menu_management_sort)
        radio_button_6_menu_management_sort = dialog_view.findViewById(R.id.radio_button_6_menu_management_sort)


        // 세션 선언
        sessionManager = SessionManager(requireContext())

        /** todo: 정렬 방법 유형 (SortRequest)
         * 1. lately: 메뉴 생성 최신 순서
         * 2. sale_up: 메뉴 생성 최신 순서
         * 3. sold_out_up: 메뉴 생성 최신 순서
         */

        var user = sessionManager!!.userDetail
        SortRequest = user.get(SessionManager.SortRequest)
        Log.e(TAG, "SortRequest: $SortRequest")

        //        if (SortRequest?.length == 0)
        //        {
        //            SortRequest = "lately"
        //
        //            sessionManager!!.saveSortRequest(SortRequest!!)
        //        }

        // 정렬 방법 선택
        var onCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->

            // 최신 생성순
            if (checkedId == R.id.radio_button_1_menu_management_sort)
            {
                SortRequest = "lately"
            }

            // 판매중인 상품 우선 정렬
            else if (checkedId == R.id.radio_button_2_menu_management_sort)
            {
                SortRequest = "sale_up"
            }

            // 품절 상품 우선 정렬
            else if (checkedId == R.id.radio_button_3_menu_management_sort)
            {
                SortRequest = "sold_out_up"
            }

            // 품절 상품 우선 정렬
            else if (checkedId == R.id.radio_button_4_menu_management_sort)
            {
                SortRequest = "name_order"
            }

            // 품절 상품 우선 정렬
            else if (checkedId == R.id.radio_button_5_menu_management_sort)
            {
                SortRequest = "beverage_up"
            }

            // 품절 상품 우선 정렬
            else if (checkedId == R.id.radio_button_6_menu_management_sort)
            {
                SortRequest = "beans_up"
            }
        }

        // 라디오그룹 실행
        radio_group_menu_management_sort.setOnCheckedChangeListener(onCheckedChangeListener)

        // 마지막으로 정렬한 방법을 불러와서 라디오그룹 체크 해두기
        if (SortRequest.equals("lately"))
        {
            radio_group_menu_management_sort.check(radio_button_1_menu_management_sort.id)
        }

        else if (SortRequest.equals("sale_up"))
        {
            radio_group_menu_management_sort.check(radio_button_2_menu_management_sort.id)
        }

        else if (SortRequest.equals("sold_out_up"))
        {
            radio_group_menu_management_sort.check(radio_button_3_menu_management_sort.id)
        }

        else if (SortRequest.equals("name_order"))
        {
            radio_group_menu_management_sort.check(radio_button_4_menu_management_sort.id)
        }

        else if (SortRequest.equals("beverage_up"))
        {
            radio_group_menu_management_sort.check(radio_button_5_menu_management_sort.id)
        }

        else if (SortRequest.equals("beans_up"))
        {
            radio_group_menu_management_sort.check(radio_button_6_menu_management_sort.id)
        }

        // 다이얼로그에서 선택한 정렬방법 적용하기 버튼
        var sort_dialog_done: TextView
        sort_dialog_done = dialog_view.findViewById<TextView>(R.id.menu_management_done)
        sort_dialog_done.setOnClickListener(View.OnClickListener {

            // 정렬한 방법을 쉐어드에 저장하기
            sessionManager!!.saveSortRequest(SortRequest!!)

            // 서버로 정렬 요청
            getMenuList(SortRequest!!)

            // 다이얼로그 닫기
            (dialog as AlertDialog).dismiss()
        })
    }

    companion object
    {
        var item_Nest_Menu = arrayListOf<Item_Nest_Menu>()
        var TAG = "Fragment_Menu_Management"
        var mContext: Context? = null
        lateinit var menu_recycler_view: RecyclerView

        var intoTheActivityCreateMode: String? = null

        var SortRequest: String? = null


        // 수정할 값을 담을 변수
        var GET_MENU_EDIT_TYPE: String? = null
        var GET_MENU_EDIT_NAME: String? = null
        var GET_MENU_EDIT_PRICE: String? = null

        var GET_MENU_EDIT_SERVING_WAY: String? = null

        var GET_MENU_EDIT_SERVING_SIZE: String? = null
        var GET_MENU_EDIT_PICK_UP_RECOMMEND_TIME: String? = null
        var GET_MENU_EDIT_SERVING_IMPOSSIBLE_TIME: String? = null

        var GET_MENU_EDIT_Thumb: String? = null
        var GET_MENU_EDIT_PRODUCE: String? = null
        var GET_MENU_EDIT_INDEX: String? = null

        class NestMenuAdapter(val context: Context?): RecyclerView.Adapter<NestMenuAdapter.ViewHolder>()
        {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            {
                return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_nest_menu_b, parent, false))
            }

            override fun getItemCount(): Int
            {
                return item_Nest_Menu.size
            }

            /*
                GET_MENU_EDIT_TYPE
                GET_MENU_EDIT_NAME
                GET_MENU_EDIT_PRICE

                GET_MENU_EDIT_SERVING_SIZE
                GET_MENU_EDIT_PICK_UP_RECOMMEND_TIME
                GET_MENU_EDIT_SERVING_IMPOSSIBLE_TIME

                GET_MENU_EDIT_Thumb
                GET_MENU_EDIT_PRODUCE
                GET_MENU_EDIT_INDEX
            */

            override fun onBindViewHolder(holder: ViewHolder, position: Int)
            {
                holder.menu_name.text = item_Nest_Menu.get(position).Menu_Name

                holder.menu_price.text = item_Nest_Menu.get(position).Menu_Price + "원"

                if (item_Nest_Menu.get(position).Menu_Serving_Size.equals("none"))
                {
                    holder.menu_serving_size.text = "적지않음"
                }

                // 상품 유형 표시
                if (item_Nest_Menu.get(position).Menu_Type.equals("Beans"))
                {
                    holder.menu_type.text = "원두"
                    holder.menu_pick_up_noti_container.visibility = View.GONE
                    holder.menu_serving_way.visibility = View.GONE


                    holder.menu_serving_size.text = item_Nest_Menu.get(position).Menu_Serving_Size + "g"
                }
                else
                {
                    holder.menu_type.text = "음료"
                    holder.menu_pick_up_noti_container.visibility = View.VISIBLE
                    holder.menu_serving_way.visibility = View.VISIBLE

                    holder.menu_serving_size.text = item_Nest_Menu.get(position).Menu_Serving_Size + "ml"
                    holder.menu_serving_way.text =  item_Nest_Menu.get(position).Menu_Serving_Way

                    // 픽업 추천 시간 안내
                    holder.menu_pick_up_recommend_time.text =
                        item_Nest_Menu.get(position).Menu_Pick_Up_Recommend_Time + "분"
                    holder.menu_serving_impossible_time.text =
                        item_Nest_Menu.get(position).Menu_Serving_Impossible_Time + "분"
                }

                var status = item_Nest_Menu.get(position).nest_Menu_Sale
                Log.e(TAG, "status: $status")

                // 판매 시작, 중단 표시
                if (status.equals("true"))
                {
                    holder.menu_sale_message.text = "판매중"
                    holder.menu_sale_switch_on.setBackgroundColor(Color.parseColor("#FFEA1C77"))
                    holder.menu_sale_switch_off.background = mContext?.getDrawable(R.drawable.item_switch_theme)

//                    item_Nest_Menu.get(position).nest_Menu_Sale = "true"
                } else
                {
                    holder.menu_sale_message.text = "판매 중단"
                    holder.menu_sale_switch_on.background = mContext?.getDrawable(R.drawable.item_switch_theme)
                    holder.menu_sale_switch_off.setBackgroundColor(Color.parseColor("#FFEA1C77"))

//                    holder.menu_sale_switch_off.background = mContext?.getDrawable(R.drawable.item_switch_theme)
//                    item_Nest_Menu.get(position).nest_Menu_Sale = "false"
                }

                // 판매 시작, 중단 버튼
                holder.menu_sale_switch.setOnClickListener(View.OnClickListener {

                    status = item_Nest_Menu.get(position).nest_Menu_Sale

                    if (status.equals("true"))
                    {
                        item_Nest_Menu.get(position).nest_Menu_Sale = "false"

                        holder.menu_sale_message.text = "판매 중단"
                        holder.menu_sale_switch_on.background = mContext?.getDrawable(R.drawable.item_switch_theme)
                        holder.menu_sale_switch_off.setBackgroundColor(Color.parseColor("#FFEA1C77"))

                        // 판매 상태를 '판매 중단'으로 변경하기
                        EditSaleStatus("false", item_Nest_Menu.get(position).Menu_No)
                    }

                    else
                    {
                        item_Nest_Menu.get(position).nest_Menu_Sale = "true"

                        holder.menu_sale_message.text = "판매중"
                        holder.menu_sale_switch_on.setBackgroundColor(Color.parseColor("#FFEA1C77"))
                        holder.menu_sale_switch_off.background = mContext?.getDrawable(R.drawable.item_switch_theme)

                        // 판매 상태를 '판매 시작'으로 변경하기
                        EditSaleStatus("true", item_Nest_Menu.get(position).Menu_No)

                        ///////////////
                    }
                })

                Log.e(TAG, "item_Nest_Menu.get($position).nest_Menu_Sale: " + item_Nest_Menu.get(position).nest_Menu_Sale)

                // 썸네일 이미지
                Picasso.get().load(item_Nest_Menu.get(position).Menu_Thumb)
                    .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                    //.resize(300,300)       // 300x300 픽셀
                    .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                    .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                    .into(holder.menu_thumb) // this인 ImageView에 로드

//                Log.e(TAG, "item_Nest_Menu.get(position).Menu_Thumb: " + item_Nest_Menu.get(position).Menu_Thumb)

                // 수정 버튼
                holder.menu_edit_button.setOnClickListener(View.OnClickListener {

                    intoTheActivityCreateMode = "Edit"

                    GET_MENU_EDIT_TYPE = item_Nest_Menu.get(position).Menu_Type
                    GET_MENU_EDIT_NAME = item_Nest_Menu.get(position).Menu_Name
                    GET_MENU_EDIT_PRICE = item_Nest_Menu.get(position).Menu_Price

                    GET_MENU_EDIT_SERVING_WAY = item_Nest_Menu.get(position).Menu_Serving_Way

                    GET_MENU_EDIT_SERVING_SIZE = item_Nest_Menu.get(position).Menu_Serving_Size
                    GET_MENU_EDIT_PICK_UP_RECOMMEND_TIME = item_Nest_Menu.get(position).Menu_Pick_Up_Recommend_Time
                    GET_MENU_EDIT_SERVING_IMPOSSIBLE_TIME = item_Nest_Menu.get(position).Menu_Serving_Impossible_Time

                    GET_MENU_EDIT_Thumb = item_Nest_Menu.get(position).Menu_Thumb
                    GET_MENU_EDIT_PRODUCE = item_Nest_Menu.get(position).Menu_Product
                    GET_MENU_EDIT_INDEX = item_Nest_Menu.get(position).Menu_No

                    var intent = Intent(mContext, Activity_Create_Menu::class.java)
                    mContext?.startActivity(intent)
                })

                Log.e(TAG, "holder.menu_name.text: " + holder.menu_name.text)
            }

            // 뷰홀더
            inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
            {
                // Holds the TextView that will add each animal to
                val menu_name = view.menu_name
                val menu_price = view.menu_price
                val menu_serving_size = view.menu_serving_size

                val menu_pick_up_recommend_time = view.menu_pick_up_recommend_time
                val menu_serving_impossible_time = view.menu_serving_impossible_time
                val menu_type = view.menu_type

                val menu_serving_way = view.menu_serving_way

                val menu_thumb = view.menu_thumb
                val menu_sale_message = view.menu_sale_message
                val menu_sale_switch = view.menu_sale_switch

                val menu_sale_switch_off = view.menu_sale_switch_off
                val menu_sale_switch_on = view.menu_sale_switch_on
                val menu_edit_button = view.menu_edit_button

                val menu_pick_up_noti_container = view.menu_pick_up_noti_container
            }

            // 판매 상태 변경하기
            private fun EditSaleStatus(SaleSignal: String, GET_MENU_EDIT_INDEX: String)
            {
                val stringRequest = object: StringRequest(Request.Method.POST,
                        "http://115.68.231.84/getCoffeeSaleStatusSignal.php",
                        com.android.volley.Response.Listener { response ->
                            Log.e(TAG, "onResponse: response = $response")

                            try
                            {
                                val jsonObject = JSONObject(response)

                                val success = jsonObject.getString("success")

                                if (success == "1")
                                {
                                    Log.e(TAG, "onResponse: 상태변경 완료")
                                }
                                else
                                {
                                    Log.e(TAG, "onResponse: 문제발생")
                                }
                            }
                            catch (e: JSONException)
                            {
                                e.printStackTrace()
                                Log.e(TAG, "onResponse: JSONException e: $e")
                            }
                        },
                        com.android.volley.Response.ErrorListener { error ->
                            Log.e(TAG, "onErrorResponse: error: $error")
                        })
                {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String>
                    {
                        val params = HashMap<String, String>()

                        params.put("SaleSignal", SaleSignal)
                        params.put("GET_MENU_EDIT_INDEX", GET_MENU_EDIT_INDEX)

                        Log.e(TAG, "SaleSignal: $SaleSignal")
                        Log.e(TAG, "GET_MENU_EDIT_INDEX: $GET_MENU_EDIT_INDEX")

                        return params
                    }
                }

                val requestQueue = Volley.newRequestQueue(mContext)
                requestQueue.add(stringRequest) // stringRequest = 바로 위에 회원가입 요청메소드 실행
            }
        }

        // todo: 서버로 메뉴 목록 요청 후 리사이클러뷰에 메뉴 세팅하기
        fun getMenuList(SortRequest: String)
        {
            Log.e(TAG, "getProjectList(): 메뉴 목록 불러오기")

            //building retrofit object
            val retrofit =
                Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

            //Defining retrofit api service
            val projectListRequest = retrofit.create(ApiInterface::class.java)

            //defining the call
            val listCall = projectListRequest.getNestMenuList(SortRequest)

            listCall.enqueue(object: Callback<List<Item_Nest_Menu>>
            {
                override fun onResponse(call: Call<List<Item_Nest_Menu>>, response: Response<List<Item_Nest_Menu>>)
                {
                    Log.e(TAG, "list call onResponse = 수신 받음")
                    // Log.e(TAG, "list call onResponse = " + response.body()!!)
                    // Log.e(TAG, "list call onResponse = " + response.body()!!.toString())

                    item_Nest_Menu = response.body() as ArrayList<Item_Nest_Menu>

                    // 넘어온 값 확인하기
                    for (i in item_Nest_Menu.indices)
                    {
                        Log.e(TAG, "onResponse: MenuName: " + item_Nest_Menu.get(i).Menu_Name)
                    }

                    menu_recycler_view.adapter = NestMenuAdapter(mContext)
                    menu_recycler_view.setAdapter(menu_recycler_view.adapter)
                }

                override fun onFailure(call: Call<List<Item_Nest_Menu>>, t: Throwable)
                {
                    Toast.makeText(mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "onFailure: t: " + t.message)
                }
            })
        }
    }
}
