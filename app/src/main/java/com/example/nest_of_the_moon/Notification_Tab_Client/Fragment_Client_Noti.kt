package com.example.nest_of_the_moon.Notification_Tab_Client


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.nest_of_the_moon.ApiClient
import com.example.nest_of_the_moon.ApiInterface
import com.example.nest_of_the_moon.Client.Activity_Client_Home.Companion.GET_ID
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management

import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.SessionManager.Companion.ID
import com.example.recycler_view_multi_view_test.Item_OrderHistory
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_nest_order_noti.view.*
import kotlinx.android.synthetic.main.item_order_history_type_2.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Fragment_Client_Noti: Fragment()
{

    var TAG: String = "Fragment_Client_Noti"
    var Context_Fragment_Client_Noti: Context? = null

    lateinit var collapsibleCalendar: CollapsibleCalendar

    var menu_noti_recycler_view_client: RecyclerView? = null
    var item_Noti = arrayListOf<Item_NotiList_Client>()


    companion object
    {
        var OrderHistory = arrayListOf<Item_OrderHistory>()
    }

    var serialNumber: Int? = null
    var newSerialNumber: Int? = 0
    var serialCount: Int? = 0

    var LastSerialNumber: Int? = 0
    var LastSerialCount: Int? = 0

    var cartTotalCount: Int? = 0
    var cartTotalPrice: Int? = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_client__noti, container, false)

        Context_Fragment_Client_Noti = activity

        // view find
        collapsibleCalendar = view.findViewById(R.id.calendarView)

        // todo: 달력 세팅
        CalenarSetting()

        // todo: 리사이클러뷰 세팅
        menu_noti_recycler_view_client = view.findViewById(R.id.client_notification_tab) as RecyclerView
        menu_noti_recycler_view_client!!.layoutManager = LinearLayoutManager(requireContext())

        // 리사이클러뷰 구분선 넣기
        menu_noti_recycler_view_client!!.addItemDecoration(
                DividerItemDecoration(
                        requireContext(), DividerItemDecoration.VERTICAL
                                     )
                                                          )
        return view
    }

    // todo: 달력 세팅
    fun CalenarSetting()
    {
        var date: String =
            collapsibleCalendar.selectedDay.year.toString() + "-" + collapsibleCalendar.selectedDay.month + "-" + collapsibleCalendar.selectedDay.day

        // todo: 주문목록 불러오기
        getnotiList(date, GET_ID.toString())

        e(
                TAG,
                "onDaySelect: " + collapsibleCalendar.selectedDay.year + "년 " + collapsibleCalendar.selectedDay.month + "월 " + collapsibleCalendar.selectedDay.day + "일"
         )

        e(TAG, "date: $date")
        e(TAG, "ID: $ID")

        collapsibleCalendar.setCalendarListener(object: CollapsibleCalendar.CalendarListener
        {
            override fun onDaySelect()
            {
                // textView.text = collapsibleCalendar.selectedDay?.toUnixTime().toString()
                var month = collapsibleCalendar.selectedDay.month + 1

                e(
                        TAG,
                        "onDaySelect: " + collapsibleCalendar.selectedDay.year + "년 " + month + "월 " + collapsibleCalendar.selectedDay.day + "일"
                 )

                date =
                    collapsibleCalendar.selectedDay.year.toString() + "-" + month + "-" + collapsibleCalendar.selectedDay.day

                e(
                        TAG, "date: $date"
                 )

                // 주문목록 새로고침/
                getnotiList(date, GET_ID.toString())
            }

            override fun onItemClick(v: View)
            {

            }

            override fun onDataUpdate()
            {

            }

            override fun onMonthChange()
            {

            }

            override fun onWeekChange(position: Int)
            {

            }
        })
    }

    // todo: 주문목록 불러오기
    fun getnotiList(date: String, id: String)
    {
        e(Fragment_Menu_Management.TAG, "getnotiList(): 나의 주문 목록 불러오기")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val projectListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = projectListRequest.getNestNoti_Client(date, id, "client")

        listCall.enqueue(object: Callback<List<Item_NotiList_Client>>
        {
            override fun onResponse(call: Call<List<Item_NotiList_Client>>,
                                    response: Response<List<Item_NotiList_Client>>)
            {
                e(Fragment_Menu_Management.TAG, "list call onResponse = 수신 받음")

                item_Noti = response.body() as ArrayList<Item_NotiList_Client>

                for (i in item_Noti.indices)
                {
                    /** todo: 주문 번호가 일치하는 데이터끼리 묶어주기.
                     * 1. 주문 번호가 일치하는 데이터의 갯수 구하기
                     * 2. 주문 번호가 일치하는 데이터의 상품 가격 합산 구하기
                     * 3. 주문 번호가 일치하는 데이터를 하나만 남겨두고 arrayList에서 모두 지우기
                     * 4. 하나만 남겨둔 데이터의 상품명을 다음과 같이 표시함 '상품명 외n개'
                     * 5. 하나만 남겨둔 데이터의 가격을 다음과 같이 표시함 '2번에서 합산한 금액으로 표시'
                     */

                    e(TAG, "nest_Order_Way: $i: " + item_Noti.get(i).nest_Order_Way)
                    e(TAG, "nest_Order_Way: $i: " + item_Noti.get(i).nest_Order_Way)
                    e(TAG, "nest_Order_Menu_Index: $i: " + item_Noti.get(i).nest_Order_Menu_Index)
                }

                menu_noti_recycler_view_client?.adapter = ClientNotiAdapter(Context_Fragment_Client_Noti)
                menu_noti_recycler_view_client?.setAdapter(menu_noti_recycler_view_client?.adapter)
            }

            override fun onFailure(call: Call<List<Item_NotiList_Client>>, t: Throwable)
            {

                Toast.makeText(Fragment_Menu_Management.mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
            }
        })
    }

    // 리사이클러뷰
    inner class ClientNotiAdapter(val context: Context?): RecyclerView.Adapter<ClientNotiAdapter.ViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_nest_order_noti, parent, false))
        }

        override fun getItemCount(): Int
        {
            return item_Noti.size
        }

        @SuppressLint("Range", "ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            // todo: 다이얼로그 준비
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
            val dialog_view = inflater.inflate(R.layout.dialog_order_hostory, null)
            builder.setView(dialog_view)
            var dialog = builder.create()

            // viewFind
            var oht_1_order_date_time: TextView = dialog_view.findViewById(R.id.oht_1_order_date_time)
            var oht_1_order_close_button: ImageView = dialog_view.findViewById(R.id.oht_1_order_close_button)
            var oht_1_order_serial_number: TextView = dialog_view.findViewById(R.id.oht_1_order_serial_number)
            var oht_1_order_noti_1: TextView = dialog_view.findViewById(R.id.oht_1_order_noti_1)

            var oht_1_order_noti_2: TextView = dialog_view.findViewById(R.id.oht_1_order_noti_2)
            var oht_1_order_noti_3: TextView = dialog_view.findViewById(R.id.oht_1_order_noti_3)
            var oht_1_total_price: TextView = dialog_view.findViewById(R.id.oht_1_total_price)


            var oht_1_order_noti_message: TextView = dialog_view.findViewById(R.id.oht_1_order_noti_message)
            var oht_1_order_pick_up_recommend_time: TextView =
                dialog_view.findViewById(R.id.oht_1_order_pick_up_recommend_time)

            var oht_1_order_pick_up_serving_impossible_time: TextView =
                dialog_view.findViewById(R.id.oht_1_order_pick_up_serving_impossible_time)
            var oht_1_order_pick_up_order_count: TextView =
                dialog_view.findViewById(R.id.oht_1_order_pick_up_order_count)

            var oht_1_order_call_button: CircleImageView = dialog_view.findViewById(R.id.oht_1_order_call_button)

            var oht_1_order_anim_1: LottieAnimationView = dialog_view.findViewById(R.id.oht_1_order_anim_1)
            var oht_1_order_anim_2: LottieAnimationView = dialog_view.findViewById(R.id.oht_1_order_anim_2)
            var oht_1_order_anim_3: LottieAnimationView = dialog_view.findViewById(R.id.oht_1_order_anim_3)
            var oht_1_order_anim_4: LottieAnimationView = dialog_view.findViewById(R.id.oht_1_order_anim_4)

            // 전화 애니메이션
            oht_1_order_anim_4.setAnimation("ripples.json")
            oht_1_order_anim_4.repeatCount = LottieDrawable.INFINITE
            oht_1_order_anim_4.playAnimation()

            var oht_1_order_pick_up_noti_area: LinearLayout =
                dialog_view.findViewById(R.id.oht_1_order_pick_up_noti_area)

            var oht_1_order_recycler_view: RecyclerView = dialog_view.findViewById(R.id.oht_1_order_recycler_view) as RecyclerView
            oht_1_order_recycler_view.layoutManager = LinearLayoutManager(requireContext())

            // 썸네일 이미지
            Picasso.get().load(item_Noti.get(position).nest_Menu_Thumb)
                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(holder.noti_c_tumb) // this인 ImageView에 로드

            // todo: 바로 주문일 경우 로직
            if (item_Noti.get(position).nest_Order_Way == "orderNow")
            {

                // 메뉴명, 결제한 가격
                holder.noti_c_name.text = item_Noti.get(position).nest_Menu_Name
                holder.noti_c_price.text = item_Noti.get(position).nest_Order_Price + "원"

                // 픽업 추천시간, 폐기시간, 결제한 시간
                var Pick_Up_Recommend_Time_format =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item_Noti.get(position).nest_Order_Pick_Up_Recommend_Time)
                var sdf = SimpleDateFormat("HH시 mm분")
                var Pick_Up_Recommend_Time = sdf.format(Pick_Up_Recommend_Time_format)

                var Serving_Impossible_Time_format =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item_Noti.get(position).nest_Order_Serving_Impossible_Time)
                var Serving_Impossible_Time = sdf.format(Serving_Impossible_Time_format)

                var Date_of_paymentformat =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item_Noti.get(position).nest_Order_Date_of_payment)
                var Date_of_payment = sdf.format(Date_of_paymentformat)

                holder.noti_c_serving_time.text = Pick_Up_Recommend_Time
                holder.noti_c_serving_impossible_time.text = Serving_Impossible_Time
                holder.noti_c_order_time.text = Date_of_payment + "에 결제함"

                // todo: 제작 완료 되었을 경우...
                if (item_Noti.get(position).nest_Order_Product_Completion_whether == "true")
                {
                    // 제작중 안내 메시지 숨기기
                    holder.noti_c_serving_noti.visibility = View.GONE

                    // 제작중 안내 메시지 숨기고 픽업 추천시간, 폐기시간 안내하기
                    holder.noti_c_serving_time_area.visibility = View.VISIBLE

                    // 썸네일 위에 제작 완료 알림 띄우기
                    holder.noti_c_comp.visibility = View.VISIBLE
                }

                // 주문 번호
                holder.noti_c_order_serial_number.text = "D" + item_Noti.get(position).nest_Order_Serial_Number

                // todo: 주문정보 상세페이지 띄우기 (단일 주문)
                holder.item.setOnClickListener(View.OnClickListener {

                    // 다이얼로그 실행
                    dialog.show()

                    // 다이얼로그 닫기 버튼
                    oht_1_order_close_button.setOnClickListener(View.OnClickListener {
                        dialog.dismiss()
                    })

                    // 매장으로 전화하기
                    oht_1_order_call_button.setOnClickListener(View.OnClickListener {

                        val builder = AlertDialog.Builder(
                                ContextThemeWrapper(
                                        Context_Fragment_Client_Noti, R.style.Theme_AppCompat_Light_Dialog
                                                   )
                                                         )

                        builder.setIcon(R.drawable.logo_1)
                        builder.setTitle("전화 연결")
                        builder.setMessage("매장으로 전화를 연결합니다")

                        builder.setPositiveButton("확인") { _, _ ->
                            e(TAG, "확인")

                            var tt = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0277777777"))
                            startActivity(tt)
                        }

                        builder.setNegativeButton("취소") { _, _ ->
                            e(TAG, "취소")
                        }

                        builder.show()
                    })

                    oht_1_order_date_time.text = "주문시간: $Date_of_payment"

                    oht_1_order_serial_number.text = "D" + item_Noti.get(position).nest_Order_Serial_Number

                    // 애니메이션 세팅
                    oht_1_order_anim_1.setAnimation("coffee_1_3.json")
                    oht_1_order_anim_1.repeatCount = LottieDrawable.INFINITE
                    oht_1_order_anim_1.playAnimation()

                    oht_1_order_noti_1.setTextColor(Color.parseColor("#EE3989")) // 테마 색상
                    //                    oht_1_order_noti_1.setTextColor(Color.parseColor("#828282")) // 회색

                    //                    oht_1_order_anim_1.pauseAnimation() // 정지

                    // 진행 상황 메시지
                    oht_1_order_noti_message.text = "주문을 요청했습니다"

                    oht_1_order_anim_2.setAnimation("coffee_2.json")
                    oht_1_order_anim_2.repeatCount = LottieDrawable.INFINITE
                    oht_1_order_anim_2.playAnimation()

                    //                    oht_1_order_noti_2.setTextColor(Color.parseColor("#EE3989")) // 테마 색상
                    oht_1_order_noti_2.setTextColor(Color.parseColor("#828282")) // 회색

                    //                    oht_1_order_anim_2.pauseAnimation()

                    // 진행 상황 메시지
                    //                    oht_1_order_noti_message.text = "음료를 제작중입니다"

                    oht_1_order_anim_3.setAnimation("coffee_3.json")
                    oht_1_order_anim_3.repeatCount = LottieDrawable.INFINITE
                    oht_1_order_anim_3.playAnimation()

                    //                    oht_1_order_noti_3.setTextColor(Color.parseColor("#EE3989")) // 테마 색상
                    oht_1_order_noti_3.setTextColor(Color.parseColor("#828282")) // 회색

                    //                    oht_1_order_anim_3.pauseAnimation()

                    // 진행 상황 메시지
                    // oht_1_order_noti_message.text = "음료가 완성 되었습니다\n바리스타에게 주문 번호를 제시 후\n음료를 픽업 하세요 :)"

                    // 픽업 시간 알림 영역 활성화
                    oht_1_order_pick_up_noti_area.visibility = View.GONE

                    // 픽업 시간 알림 메시지 세팅
                    oht_1_order_pick_up_recommend_time.text = "00시 00분"
                    oht_1_order_pick_up_serving_impossible_time.text = "00시 00분"

                    oht_1_order_pick_up_order_count.text = "주문내역 1건"

                    oht_1_total_price.text = "총 " + item_Noti.get(position).nest_Order_Price + "원"

                    OrderHistory.add(
                            Item_OrderHistory(
                                    "",
                                    item_Noti.get(position).nest_Menu_Thumb,
                                    item_Noti.get(position).nest_Menu_Name,
                                    item_Noti.get(position).nest_Order_Serving_Way,
                                    item_Noti.get(position).nest_Order_Menu_Option,
                                    item_Noti.get(position).nest_Order_User_Request,
                                    item_Noti.get(position).nest_Order_Price,
                                    "none",
                                    false
                                             )
                                    )

                    oht_1_order_recycler_view.adapter = Adapter_OrderHistory(Context_Fragment_Client_Noti)
                    oht_1_order_recycler_view.adapter = oht_1_order_recycler_view.adapter

                    // oht_1_order_recycler_view?.adapter!!.notifyDataSetChanged()

                    // 다이얼로그 종료되면 리스트 비우기
                    dialog.setOnDismissListener {
                        OrderHistory.clear()
                    }
                })
            }

            // todo: 장바구니로 주문했을 경우 로직
            else if (item_Noti.get(position).nest_Order_Way == "addCart")
            {
                // 메뉴명, 결제한 가격
                if (Integer.parseInt(item_Noti.get(position).CartTotalCount) == 0)
                {
                    holder.noti_c_name.text = item_Noti.get(position).nest_Menu_Name
                }
                else
                {
                    holder.noti_c_name.text =
                        item_Noti.get(position).nest_Menu_Name + " (외 " + item_Noti.get(position).CartTotalCount + "개)"
                }

                holder.noti_c_price.text = "총 " + item_Noti.get(position).nest_Order_Price + "원"

                // 픽업 추천시간, 폐기시간, 결제한 시간
                var Pick_Up_Recommend_Time_format =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item_Noti.get(position).nest_Order_Pick_Up_Recommend_Time)
                var sdf = SimpleDateFormat("HH시 mm분")
                var Pick_Up_Recommend_Time = sdf.format(Pick_Up_Recommend_Time_format)

                var Serving_Impossible_Time_format =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item_Noti.get(position).nest_Order_Serving_Impossible_Time)
                var Serving_Impossible_Time = sdf.format(Serving_Impossible_Time_format)

                var Date_of_paymentformat =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item_Noti.get(position).nest_Order_Date_of_payment)
                var Date_of_payment = sdf.format(Date_of_paymentformat)

                holder.noti_c_serving_time.text = Pick_Up_Recommend_Time
                holder.noti_c_serving_impossible_time.text = Serving_Impossible_Time
                holder.noti_c_order_time.text = Date_of_payment + "에 결제함"

                // todo: 제작 완료 되었을 경우...
                if (item_Noti.get(position).nest_Order_Product_Completion_whether == "true")
                {
                    // 제작중 안내 메시지 숨기기
                    holder.noti_c_serving_noti.visibility = View.GONE

                    // 제작중 안내 메시지 숨기고 픽업 추천시간, 폐기시간 안내하기
                    holder.noti_c_serving_time_area.visibility = View.VISIBLE

                    // 썸네일 위에 제작 완료 알림 띄우기
                    holder.noti_c_comp.visibility = View.VISIBLE
                }

                // 주문 번호
                holder.noti_c_order_serial_number.text = "D" + item_Noti.get(position).nest_Order_Serial_Number

                // todo: 주문정보 상세페이지 띄우기 (장바구니)
                holder.item.setOnClickListener(View.OnClickListener {

                    // 다이얼로그 실행
                    dialog.show()

                    // 다이얼로그 닫기 버튼
                    oht_1_order_close_button.setOnClickListener(View.OnClickListener {
                        dialog.dismiss()
                    })

                    // 매장으로 전화하기
                    oht_1_order_call_button.setOnClickListener(View.OnClickListener {

                        val builder = AlertDialog.Builder(
                                ContextThemeWrapper(
                                        Context_Fragment_Client_Noti, R.style.Theme_AppCompat_Light_Dialog
                                                   )
                                                         )

                        builder.setIcon(R.drawable.logo_1)
                        builder.setTitle("전화 연결")
                        builder.setMessage("매장으로 전화를 연결합니다")

                        builder.setPositiveButton("확인") { _, _ ->
                            e(TAG, "확인")

                            var tt = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0277777777"))
                            startActivity(tt)
                        }

                        builder.setNegativeButton("취소") { _, _ ->
                            e(TAG, "취소")
                        }

                        builder.show()
                    })

                    oht_1_order_date_time.text = "주문시간: $Date_of_payment"

                    oht_1_order_serial_number.text = "D" + item_Noti.get(position).nest_Order_Serial_Number

                    // 애니메이션 세팅
                    oht_1_order_anim_1.setAnimation("coffee_1_3.json")
                    oht_1_order_anim_1.setMinAndMaxFrame(15, Int.MAX_VALUE)
                    oht_1_order_anim_1.repeatCount = LottieDrawable.INFINITE
                    oht_1_order_anim_1.playAnimation()

                    oht_1_order_noti_1.setTextColor(Color.parseColor("#EE3989")) // 테마 색상
                    //                    oht_1_order_noti_1.setTextColor(Color.parseColor("#828282")) // 회색

//                                        oht_1_order_anim_1.pauseAnimation() // 정지

                    // 진행 상황 메시지
                    oht_1_order_noti_message.text = "주문을 요청했습니다"

                    oht_1_order_anim_2.setAnimation("coffee_2.json")
                    oht_1_order_anim_2.repeatCount = LottieDrawable.INFINITE
                    oht_1_order_anim_2.playAnimation()

                    //                    oht_1_order_noti_2.setTextColor(Color.parseColor("#EE3989")) // 테마 색상
                    oht_1_order_noti_2.setTextColor(Color.parseColor("#828282")) // 회색

                                        oht_1_order_anim_2.pauseAnimation()

                    // 진행 상황 메시지
                    //                    oht_1_order_noti_message.text = "음료를 제작중입니다"

                    oht_1_order_anim_3.setAnimation("coffee_3.json")
                    oht_1_order_anim_3.repeatCount = LottieDrawable.INFINITE
                    oht_1_order_anim_3.playAnimation()

                    //                    oht_1_order_noti_3.setTextColor(Color.parseColor("#EE3989")) // 테마 색상
                    oht_1_order_noti_3.setTextColor(Color.parseColor("#828282")) // 회색

                                        oht_1_order_anim_3.pauseAnimation()

                    // 진행 상황 메시지
                    // oht_1_order_noti_message.text = "음료가 완성 되었습니다\n바리스타에게 주문 번호를 제시 후\n음료를 픽업 하세요 :)"

                    // 픽업 시간 알림 영역 활성화
                    oht_1_order_pick_up_noti_area.visibility = View.GONE

                    // 픽업 시간 알림 메시지 세팅
                    oht_1_order_pick_up_recommend_time.text = "00시 00분"
                    oht_1_order_pick_up_serving_impossible_time.text = "00시 00분"

                    var count: Int = Integer.parseInt(item_Noti.get(position).CartTotalCount) + 1

                    oht_1_order_pick_up_order_count.text = "주문내역 " + count.toString() + "건"

                    oht_1_total_price.text = "총 " + item_Noti.get(position).nest_Order_Price + "원"

                    e(TAG, "getnotiList(): 주문 상세정보")

                    // todo: 리사이클러뷰 세팅
                    var orderCart = item_Noti.get(position).nest_Order_Menu_Index

                    //building retrofit object
                    val retrofit =
                        Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

                    //Defining retrofit api service
                    val projectListRequest = retrofit.create(ApiInterface::class.java)

                    //defining the call
                    val listCall = projectListRequest.getOrderHistoryList(orderCart)

                    listCall.enqueue(object: Callback<List<Item_OrderHistory>>
                    {
                        override fun onResponse(call: Call<List<Item_OrderHistory>>,
                                                response: Response<List<Item_OrderHistory>>)
                        {
                            e(Fragment_Menu_Management.TAG, "list call onResponse = 수신 받음")

                            OrderHistory = response.body() as ArrayList<Item_OrderHistory>

                            for (i in OrderHistory.indices)
                            {
                                e(TAG, "nest_Order_Way: " + OrderHistory.get(i).nest_Menu_Name)
                            }

                            oht_1_order_recycler_view.adapter = Adapter_OrderHistory(Context_Fragment_Client_Noti)
                            oht_1_order_recycler_view.adapter = oht_1_order_recycler_view.adapter
                        }

                        override fun onFailure(call: Call<List<Item_OrderHistory>>, t: Throwable)
                        {

                            Toast.makeText(Fragment_Menu_Management.mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                            e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
                        }
                    })

                    // 다이얼로그 종료되면 리스트 비우기
                    dialog.setOnDismissListener {
                        OrderHistory.clear()
                    }
                })

                for (i in OrderHistory.indices)
                {
                    e(TAG, "nest_Menu_Thumb: " + item_Noti.get(position).nest_Menu_Thumb)
                    e(TAG, "nest_Menu_Name: " + item_Noti.get(position).nest_Menu_Name)
                    e(TAG, "nest_Order_Serving_Way: " + item_Noti.get(position).nest_Order_Serving_Way)
                    e(TAG, "nest_Order_Menu_Option: " + item_Noti.get(position).nest_Order_Menu_Option)
                    e(TAG, "nest_Order_User_Request: " + item_Noti.get(position).nest_Order_User_Request)
                    e(TAG, "nest_Order_Price: " + item_Noti.get(position).nest_Order_Price)
                }
            }
        }

        // 뷰홀더
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        {
            val item = view

            var noti_c_tumb = view.noti_c_tumb
            var noti_c_comp = view.noti_c_comp
            var noti_c_name = view.noti_c_name
            var noti_c_price = view.noti_c_price
            var noti_c_serving_time = view.noti_c_serving_time
            var noti_c_serving_impossible_time = view.noti_c_serving_impossible_time
            var noti_c_order_time = view.noti_c_order_time
            var noti_c_order_serial_number = view.noti_c_order_serial_number
            var noti_c_serving_noti = view.noti_c_serving_noti
            var noti_c_serving_time_area = view.noti_c_serving_time_area
        }
    } // 리사이클러뷰 끝



/*    // 리사이클러뷰 시작
    class Adapter_OrderHistory(val context: Context?): RecyclerView.Adapter<Adapter_OrderHistory.ViewHolder>()
    {
        //    var OrderDetailInfo = arrayListOf<Item_OrderHistory>()

        var TAG = "Adapter_OrderHistory"

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_history_type_2, parent, false))
        }

        override fun getItemCount(): Int
        {
            return OrderHistory.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            e(TAG, "값 들어오냐? 독립된 어댑터 클래스")

            // 썸네일 이미지
            Picasso.get().load(OrderHistory.get(position).nest_Menu_Thumb)
                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(holder.oht_2_thumb) // this인 ImageView에 로드

            var count = position + 1

            holder.oht_2_way.text = OrderHistory.get(position).nest_Order_Serving_Way + "# $count"
            holder.oht_2_name.text = OrderHistory.get(position).nest_Menu_Name
            holder.oht_2_price.text = OrderHistory.get(position).nest_Order_Price + "원"

            holder.oht_2_option.text = OrderHistory.get(position).nest_Order_Menu_Option
            holder.oht_2_request.text = OrderHistory.get(position).nest_Order_User_Request

            //        // todo: 체크박스 처리
            //        if (OrderDetailInfo.get(position).user == "Barista")
            //        {
            //            holder.oht_2_check.visibility = View.VISIBLE
            //
            //            if (OrderDetailInfo.get(position).isChecked == "false")
            //            {
            //                holder.oht_2_check.setImageResource(R.drawable.ic_un_check_all)
            //            }
            //            else
            //            {
            //                holder.oht_2_check.setImageResource(R.drawable.ic_check_all)
            //            }
            //        }

            //        holder.item.setOnClickListener(View.OnClickListener {
            //            // 리사이클러뷰 갱신
            //            Fragment_B_Order_Management.bop_recycler_view?.adapter?.notifyDataSetChanged()
            //        })

        }

        // 뷰홀더
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        {
            val item = view

            var oht_2_thumb = view.oht_2_thumb

            var oht_2_way = view.oht_2_way
            var oht_2_name = view.oht_2_name
            var oht_2_price = view.oht_2_price

            var oht_2_option = view.oht_2_option
            var oht_2_request = view.oht_2_request

            var oht_2_check = view.oht_2_check

        }
    } // 리사이클러뷰 끝

    */
}


/*
    // 리사이클러뷰
    inner class ClientNotiAdapter(val context: Context?): RecyclerView.Adapter<ClientNotiAdapter.ViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout., parent, false))
        }

        override fun getItemCount(): Int
        {
            return item_Noti.size
        }

        @SuppressLint("Range", "ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            // 썸네일 이미지
            Picasso.get().load(item_Noti.get(position).nest_Menu_Thumb)
                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(holder.noti_c_tumb) // this인 ImageView에 로드
        }

        // 뷰홀더
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        {
            val item = view

            var noti_c_tumb = view.noti_c_tumb
        }
    } // 리사이클러뷰 끝

*/