package com.example.nest_of_the_moon.Notification_Tab_Client


import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nest_of_the_moon.ApiClient
import com.example.nest_of_the_moon.ApiInterface
import com.example.nest_of_the_moon.Client.Activity_Client_Home.Companion.GET_ID
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management

import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.SessionManager.Companion.ID
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_nest_order_noti.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Fragment_Client_Noti: Fragment()
{

    var TAG: String = "Fragment_Client_Noti"
    var Context_Fragment_Client_Noti: Context? = null

    lateinit var collapsibleCalendar: CollapsibleCalendar

    var menu_noti_recycler_view_client: RecyclerView? = null
    var item_Noti = arrayListOf<Item_NotiList_Client>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_client__noti, container, false)

        Context_Fragment_Client_Noti = activity?.applicationContext

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

        Log.e(
                TAG,
                "onDaySelect: " + collapsibleCalendar.selectedDay.year + "년 " + collapsibleCalendar.selectedDay.month + "월 " + collapsibleCalendar.selectedDay.day + "일"
             )

        Log.e(TAG, "date: $date")
        Log.e(TAG, "ID: $ID")

        collapsibleCalendar.setCalendarListener(object: CollapsibleCalendar.CalendarListener
        {
            override fun onDaySelect()
            {
                // textView.text = collapsibleCalendar.selectedDay?.toUnixTime().toString()
                var month = collapsibleCalendar.selectedDay.month + 1

                Log.e(
                        TAG,
                        "onDaySelect: " + collapsibleCalendar.selectedDay.year + "년 " + month + "월 " + collapsibleCalendar.selectedDay.day + "일"
                     )

                date =
                    collapsibleCalendar.selectedDay.year.toString() + "-" + month + "-" + collapsibleCalendar.selectedDay.day

                Log.e(
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
        Log.e(Fragment_Menu_Management.TAG, "getnotiList(): 나의 주문 목록 불러오기")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val projectListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = projectListRequest.getNestNoti_Client(date, id)

        listCall.enqueue(object: Callback<List<Item_NotiList_Client>>
        {
            override fun onResponse(call: Call<List<Item_NotiList_Client>>,
                                    response: Response<List<Item_NotiList_Client>>)
            {
                Log.e(Fragment_Menu_Management.TAG, "list call onResponse = 수신 받음")

                item_Noti = response.body() as ArrayList<Item_NotiList_Client>

                // 넘어온 값 확인하기
                for (i in item_Noti.indices)
                {
                    Log.e(
                            Fragment_Menu_Management.TAG,
                            "onResponse: Menu_Serving_Size: " + item_Noti.get(i).nest_Order_Index
                         )
                }

                menu_noti_recycler_view_client?.adapter = ClientNotiAdapter(Context_Fragment_Client_Noti)
                menu_noti_recycler_view_client?.setAdapter(menu_noti_recycler_view_client?.adapter)
            }

            override fun onFailure(call: Call<List<Item_NotiList_Client>>, t: Throwable)
            {

                Toast.makeText(Fragment_Menu_Management.mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                Log.e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
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
            }

            // todo: 장바구니로 주문했을 경우 로직
            else if (item_Noti.get(position).nest_Order_Way == "addCart")
            {

            }

            // todo: 주문정보 상세페이지 띄우기
            holder.item.setOnClickListener(View.OnClickListener {

            })
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
}
