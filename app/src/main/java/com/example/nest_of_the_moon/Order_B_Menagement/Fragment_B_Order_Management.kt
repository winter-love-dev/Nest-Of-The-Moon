package com.example.nest_of_the_moon.Order_B_Menagement


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log.e
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nest_of_the_moon.ApiClient
import com.example.nest_of_the_moon.ApiInterface
import com.example.nest_of_the_moon.Barista.Activity_Barista_Home.Companion.GET_ID
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management
import com.example.nest_of_the_moon.Notification_Tab_Client.Item_NotiList_Client
import com.example.nest_of_the_moon.Order_B_Menagement.Adapter_Order_BOP.Companion.checkCount

import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.Service.nestService
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE
import com.example.nest_of_the_moon.Service.nestService.Companion.START_CODE
import com.example.nest_of_the_moon.Service.nestService.Companion.orderRequestHandler
import com.example.nest_of_the_moon.TCP_Manager
import com.example.nest_of_the_moon.TCP_Manager.Companion.RoomNo
import com.example.nest_of_the_moon.TCP_Manager.Companion.TCPSendUerID
import com.example.nest_of_the_moon.TCP_Manager.Companion.UserType
import com.example.nest_of_the_moon.TCP_Manager.Companion.client
import com.example.nest_of_the_moon.TCP_Manager.Companion.msgFilter
import com.example.nest_of_the_moon.TCP_Manager.Companion.msgHandler
import com.example.nest_of_the_moon.TCP_Manager.Companion.send
import com.example.recycler_view_multi_view_test.Item_OrderHistory
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_barista_order_progress.*
import kotlinx.android.synthetic.main.item_today_order_list.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 */
class Fragment_B_Order_Management: Fragment()
{
    var TAG = "Fragment_B_Order_Management"

    var Context_Fragment_B_Order_Management: Context? = null

    var joinSerialRoom: String? = null

    // 달력
    lateinit var collapsibleCalendar: CollapsibleCalendar

    // 리사이클러뷰
    var recyclerViewTodayOrderList: RecyclerView? = null
    //    var itemOrderList = arrayListOf<Item_NotiList_Client>()

    var date: String? = null

    companion object
    {
        var countHandler: Handler? = null // 핸들러: 체크박스가 몇 개 체크했는지 확인하기
        var itemOrderList = arrayListOf<Item_NotiList_Client>()
        var OrderDetailInfo = arrayListOf<Item_OrderHistory>()
        var countComp: Boolean = false
    }

    // var OrderDetailInfo = arrayListOf<Item_OrderHistory>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_b_order_management, container, false)

        //        Context_Fragment_B_Order_Management = activity?.applicationContext
        Context_Fragment_B_Order_Management = activity

        // view find
        collapsibleCalendar = view.findViewById(R.id.b_calendarView)

        // todo: 달력 세팅
        CalenarSetting()

        // todo: 리사이클러뷰 세팅
        recyclerViewTodayOrderList = view.findViewById(R.id.b_order_list) as RecyclerView
        recyclerViewTodayOrderList!!.layoutManager = LinearLayoutManager(requireContext())

        // 리사이클러뷰 구분선 넣기
        recyclerViewTodayOrderList!!.addItemDecoration(
                DividerItemDecoration(
                        requireContext(), DividerItemDecoration.VERTICAL
                                     )
                                                      )

        // todo: 새 주문 감지하기 (서비스 클래스에서 알림 받기)
        // todo: 새 주문 감지 후 시리얼 룸으로 접속하기
        orderRequestHandler = @SuppressLint("HandlerLeak") object: Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == 101)
                {
                    if (msg.arg1 == 7777)
                    {
                        getTodayOrderList(date!!)
                        e(TAG, "새 주문 감지됨 $date")
                        e(TAG, "새 주문 감지코드: " + msg.arg1)
                    }
                }
            }
        }

        countHandler = @SuppressLint("HandlerLeak") object: Handler()
        {
            override fun handleMessage(msg: Message)
            {
                super.handleMessage(msg)

                if (msg.what === START_CODE)
                {
                    e(TAG, "메시지 받음 START_CODE")
                }
                else if (msg.what === PROGRESS_CODE)
                {
                    e(TAG, "메시지 받음 msg.what: " + msg.what)
                    e(TAG, "메시지 받음 msg.arg1: " + msg.arg1)

                    e(TAG, "countHandler countComp: " + countComp)

                    if (msg.arg1 == 1)
                    {
                        e(TAG, "countComp: Enable")
                        bop_order_progress_end_button.setBackgroundColor(Color.parseColor("#FFEA1C77"))
                        bop_order_progress_end_button.isEnabled = true
                        bop_progress_noti_message.text = "클릭하면 고객에게 '제작 완료'알림을 전달합니다"
                    }

                    else /*if (countComp == false)*/
                    {
                        e(TAG, "countComp: Disable")
                        // bop_order_progress_end_button.setBackgroundColor(Color.parseColor("#828282"))

                        bop_order_progress_end_button.setBackgroundColor(Color.GRAY)
                        bop_order_progress_end_button.isEnabled = false
                        bop_progress_noti_message.text = "고객에게 '제작 시작'알림을 전달했습니다"
                    }
                }
            }
        }

        //        // todo: 웨이팅 서버 접속(바리스타) (서비스 클래스로 옮김)
        //        // 방 번호
        //        var waitingRoomNo = "777"
        //        var loginUsertype = "Barista"
        //        var loginUserId: String = GET_ID.toString()
        //        var orderRequest = "imBarista" /*    or "newOrderRequest"    */
        //
        //        // val roomAndUserData: String = "1" /*RoomNo + "@" + loginUserId*/
        //        var waitingRoomAndUserData: String =
        //            waitingRoomNo + "@" + loginUsertype + "@" + loginUserId + "@" + orderRequest
        //        e(TAG, "onResponse: roomAndUserData: $waitingRoomAndUserData")
        //
        //        // 방번호와 유저의 이름으로 서버에 접속한다
        //        e(TAG, "onCreate: roomAndUserData: $waitingRoomAndUserData")
        //        client = TCP_Manager.SocketClient(waitingRoomAndUserData)
        //        client?.start()
        //
        //        // 웨이팅 서버 접속 완료

        //        // 통신 유형: 주문 요청
        //        // todo: 실시간 주문 수신 받기
        //        msgHandler = @SuppressLint("HandlerLeak") object: Handler()
        //        {
        //            override fun handleMessage(msg: Message)
        //            {
        //                if (msg.what == 1111)
        //                {
        //                    e(TAG, " 수신 받음")
        //
        //                    // 메시지 수신받기
        //                    e(TAG, "받은 메시지: " + msg.obj.toString())
        //
        //                    msgFilter = msg.obj.toString().split("│".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        //
        //                    // 주문 감지
        //                    for (i in msgFilter!!.indices)
        //                    {
        //                        e(TAG, "msgFilter[$i]: " + msgFilter?.get(i)!!)
        //                        e(TAG, "msgFilter[$i]: " + msgFilter!![i])
        //
        //                        if (msgFilter!![i].equals("newOrderRequest"))
        //                        {
        //                            // 주문 감지되면 리스트 새로고침
        //                            getTodayOrderList(date!!)
        //                            e(TAG, "목록 불러오기: date: $date")
        //                        }
        //                    }
        //                }
        //            }
        //        }

        return view
    }

    // todo: 달력 세팅
    fun CalenarSetting()
    {
        date =
            collapsibleCalendar.selectedDay.year.toString() + "-" + collapsibleCalendar.selectedDay.month + "-" + collapsibleCalendar.selectedDay.day

        // todo: 오늘의 주문 목록 불러오기
        getTodayOrderList(date!!)

        e(
                TAG,
                "onDaySelect: " + collapsibleCalendar.selectedDay.year + "년 " + collapsibleCalendar.selectedDay.month + "월 " + collapsibleCalendar.selectedDay.day + "일"
         )

        e(TAG, "date: $date")

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

                // todo: 오늘의 주문 목록 불러오기
                getTodayOrderList(date!!)
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

    // todo: 오늘의 주문목록 불러오기
    fun getTodayOrderList(date: String)
    {
        e(TAG, "getTodayOrderList(): 오늘의 주문 목록 불러오기 (관리자)")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val projectListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = projectListRequest.getTodayOrderList(date, "Barista")

        listCall.enqueue(object: Callback<List<Item_NotiList_Client>>
        {
            override fun onResponse(call: Call<List<Item_NotiList_Client>>,
                                    response: Response<List<Item_NotiList_Client>>)
            {
                e(TAG, "chatList call onResponse = 수신 받음")

                itemOrderList = response.body() as ArrayList<Item_NotiList_Client>

                for (i in itemOrderList.indices)
                {
                    /** todo: 주문 번호가 일치하는 데이터끼리 묶어주기.
                     * 1. 주문 번호가 일치하는 데이터의 갯수 구하기
                     * 2. 주문 번호가 일치하는 데이터의 상품 가격 합산 구하기
                     * 3. 주문 번호가 일치하는 데이터를 하나만 남겨두고 arrayList에서 모두 지우기
                     * 4. 하나만 남겨둔 데이터의 상품명을 다음과 같이 표시함 '상품명 외n개'
                     * 5. 하나만 남겨둔 데이터의 가격을 다음과 같이 표시함 '2번에서 합산한 금액으로 표시'
                     */


                    e(TAG, "index: " + itemOrderList.get(i).nest_Order_Index)

                }



                recyclerViewTodayOrderList?.adapter = AdapterTodayOrderList(Context_Fragment_B_Order_Management)
                recyclerViewTodayOrderList?.setAdapter(recyclerViewTodayOrderList?.adapter)
            }

            override fun onFailure(call: Call<List<Item_NotiList_Client>>, t: Throwable)
            {

                Toast.makeText(Context_Fragment_B_Order_Management, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                e(TAG, "onFailure: t: " + t.message)
            }
        })
    }

    // 리사이클러뷰
    inner class AdapterTodayOrderList(val context: Context?): RecyclerView.Adapter<AdapterTodayOrderList.ViewHolder>()
    {
        // var bop_recycler_view: RecyclerView? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_today_order_list, parent, false))
        }

        override fun getItemCount(): Int
        {
            return itemOrderList.size
        }

        var serialRoomNo: String? = null
        var compCode: String? = null

        /*
         serialRoomNo
         compCode
        */

        @TargetApi(Build.VERSION_CODES.O)
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            if (itemOrderList.get(position).nest_Order_Way == "orderCart")
            {
                e(TAG, "orderCart")
                e(TAG, "nest_Order_Index: " + itemOrderList.get(position).nest_Order_Index)
            }
            else if (itemOrderList.get(position).nest_Order_Way == "addCart")
            {
                e(TAG, "addCart")
                e(TAG, "nest_Order_Index: " + itemOrderList.get(position).nest_Order_Index)
            }
            else
            {
                e(TAG, "orderNow")
                e(TAG, "nest_Order_Index: " + itemOrderList.get(position).nest_Order_Index)
            }
            e(TAG, "nest_Order_Menu_Index: " + itemOrderList.get(position).nest_Order_Menu_Index)

            // 썸네일 이미지
            Picasso.get().load(itemOrderList.get(position).nest_Menu_Thumb)
                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(holder.noti_c_tumb) // this인 ImageView에 로드

            // 목록 순서
            var count = position + 1

            // todo: 날짜, 시간 형식 설정
            var Date_of_paymentformat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(itemOrderList.get(position).nest_Order_Date_of_payment)

            var CompletionTimeFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(itemOrderList.get(position).nest_Order_Product_Completion)

            var sdf = SimpleDateFormat("HH시 mm분")
            var sdf2 = SimpleDateFormat("HH:mm")

            var Date_of_payment = sdf.format(Date_of_paymentformat)
            var CompletionTime = sdf2.format(CompletionTimeFormat)

            // 옵션, 요청사항
            holder.bol_option.text = itemOrderList.get(position).nest_Order_Menu_Option
            holder.bol_request.text = itemOrderList.get(position).nest_Order_User_Request

            // hot / ice 여부, 목록 순서, 결제한 시간
            holder.bol_way.text =
                itemOrderList.get(position).nest_Order_Serving_Way + " #" + count.toString() + " 결제시간: " + Date_of_payment

            // 제작 대기중 표시
            if (itemOrderList.get(position).nest_Order_Check_Whether == "false")
            {
                holder.bol_order_status.text = "제작 대기중"
            }

            // 제작 완료와 폐기 해야될 시간 표시
            else
            {
                holder.bol_order_status.text = "제작 완료\n\n$CompletionTime\nclean up"
            }


            if (itemOrderList.get(position).nest_Order_Product_Completion_whether == "true")
            {
                holder.bol_tumb_comp.visibility = View.VISIBLE
            }
            else
            {
                holder.bol_tumb_comp.visibility = View.GONE
            }


            // 주문 번호
            holder.bol_serial_number.text = "D" + itemOrderList.get(position).nest_Order_Serial_Number

            // todo: 다이얼로그 준비
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
            val dialog_view = inflater.inflate(R.layout.dialog_barista_order_progress, null)
            builder.setView(dialog_view)
            var dialog = builder.create()

            // todo: Dialog viewFind
            var bop_serial_number: TextView = dialog_view.findViewById(R.id.bop_serial_number)
            var bop_order_time: TextView = dialog_view.findViewById(R.id.bop_order_time)
            var progress_completion_time: TextView = dialog_view.findViewById(R.id.progress_completion_time)
            var bop_serving_impossible_time_1: TextView = dialog_view.findViewById(R.id.bop_serving_impossible_time_1)

            var bop_serving_impossible_time_2: TextView = dialog_view.findViewById(R.id.bop_serving_impossible_time_2)
            var bop_order_count: TextView = dialog_view.findViewById(R.id.bop_order_count)
            var bop_total_price: TextView = dialog_view.findViewById(R.id.bop_total_price)
            var bop_refund_button: TextView = dialog_view.findViewById(R.id.bop_refund_button)

            var bop_progress_noti_message: TextView = dialog_view.findViewById(R.id.bop_progress_noti_message)
            var bop_order_progress_start_button: TextView =
                dialog_view.findViewById(R.id.bop_order_progress_start_button)
            //            var bop_order_progress_end_button: TextView = dialog_view.findViewById(R.id.bop_order_progress_end_button)
            var bop_order_progress_end_button: TextView = dialog_view.findViewById(R.id.bop_order_progress_end_button)
            var bop_scroll: ScrollView = dialog_view.findViewById(R.id.bop_scroll)
            var bop_go_top: CircleImageView = dialog_view.findViewById(R.id.bop_go_top)

            // todo: 리사이클러뷰
            // bop_recycler_view = dialog_view.findViewById(R.id.bop_recycler_view) as RecyclerView // 전역 변수용
            // todo: 아래 bop_recycler_view 변수는 전역 변수로 두면 안 됨!!
            var bop_recycler_view: RecyclerView = dialog_view.findViewById(R.id.bop_recycler_view) as RecyclerView
            bop_recycler_view?.layoutManager = LinearLayoutManager(requireContext())

            var bop_dialog_close_button: CircleImageView = dialog_view.findViewById(R.id.bop_dialog_close_button)

            // todo: 다이얼로그 코드 시작
            holder.item.setOnClickListener(View.OnClickListener {

                // todo: 다이얼로그 실행
                dialog.show()

                //                // 제작 완료가 안 된 시리얼 서버 접속
                //                if (itemOrderList.get(position).nest_Order_Product_Completion_whether == "false")
                //                {
                //                    // todo: 시리얼 서버 접속 (바리스타)
                //                    // 방 번호
                //                    var waitingRoomNo = itemOrderList.get(position).nest_Order_Serial_Number
                //                    var loginUsertype = "Barista"
                //                    var orderRequest = "serial" /*    or "newOrderRequest"    */
                //
                //                    var serialRoomAndUserData: String =
                //                        waitingRoomNo + "@" + loginUsertype + "@" + GET_ID + "@" + orderRequest
                //
                //                    // 방 번호와 유저의 이름으로 서버에 접속한다
                //                    e(TAG, "serialRoomAndUserData: $serialRoomAndUserData")
                //                    client = TCP_Manager.SocketClient(serialRoomAndUserData)
                //                    client?.start()
                //                }

                // 정보 표시하는 법
                if (itemOrderList.get(position).nest_Order_Way == "orderCart")
                {
                    e(TAG, "orderCart")
                    e(TAG, "nest_Order_Index: " + itemOrderList.get(position).nest_Order_Index)
                }
                else if (itemOrderList.get(position).nest_Order_Way == "addCart")
                {
                    e(TAG, "addCart")
                    e(TAG, "nest_Order_Index: " + itemOrderList.get(position).nest_Order_Index)
                }
                else
                {
                    e(TAG, "orderNow")
                    e(TAG, "nest_Order_Index: " + itemOrderList.get(position).nest_Order_Index)
                }

                // todo: 맨 위로 스크롤하기 버튼
                bop_go_top.setOnClickListener(View.OnClickListener {
                    bop_scroll.scrollTo(
                            View.FOCUS_UP, View.FOCUS_UP
                                       ) // 스크롤 뷰 위로 (0, 0)을 해도 되고 (View.FOCUS_UP, View.FOCUS_UP)도 가능
                    bop_recycler_view.smoothScrollToPosition(0) // 리사이클러뷰 위로
                })

                // todo: 다이얼로그 닫기 버튼 클릭
                bop_dialog_close_button.setOnClickListener(View.OnClickListener {
                    dialog.dismiss()
                })

                // todo: 환불버튼 클릭
                bop_refund_button.setOnClickListener(View.OnClickListener {

                })

                // todo: 제작 시작버튼 클릭
                bop_order_progress_start_button.setOnClickListener(View.OnClickListener {

                    val builderr = AlertDialog.Builder(
                            ContextThemeWrapper(
                                    requireContext(), R.style.Theme_AppCompat_Light_Dialog
                                               )
                                                      )

                    builderr.setIcon(R.drawable.logo_1)
                    builderr.setTitle("음료 제작을 시작합니다")
                    builderr.setMessage("상품의 재고를 확인하세요")

                    builderr.setPositiveButton("확인") { _, _ ->
                        e(TAG, "확인")


                        for (i in OrderDetailInfo.indices)
                        {
                            OrderDetailInfo.get(i).user = "Barista"
                            OrderDetailInfo.get(i).isChecked = false
                            e(TAG, "Barista로 변경: user: " + OrderDetailInfo.get(i).user)
                            e(TAG, "isChecked: " + OrderDetailInfo.get(i).isChecked)
                        }

                        bop_recycler_view.adapter?.notifyDataSetChanged()

                        bop_scroll.scrollTo(0, 0) // 스크롤 뷰 위로
                        bop_recycler_view.smoothScrollToPosition(0) // 리사이클러뷰 위로

                        // todo: 제작 완료버튼 활성화
                        bop_progress_noti_message.text = "고객에게 '제작 시작'알림을 전달했습니다"
                        bop_order_progress_start_button.visibility = View.GONE // 제작 시작버튼 숨기기
                        bop_order_progress_end_button.visibility = View.VISIBLE
                        // bop_order_progress_end_button.isEnabled = false

                        var index = itemOrderList.get(position).nest_Order_Index
                        e(TAG, "index: $index")

                        compCode = "666"
                        serialRoomNo = itemOrderList.get(position).nest_Order_Serial_Number

                        // todo: 관리자의 주문 접수 진행 상태 업데이트
                        updateOrderStatus("start", "none", "none", index)
                    }

                    builderr.setNegativeButton("취소") { _, _ ->
                        e(TAG, "취소")
                    }
                    builderr.show()
                })

                e(TAG, "itemOrderList.size: " + itemOrderList.size)
                e(TAG, "checkCount: " + checkCount.toString())

                // todo: 제작 완료버튼 활성화 조건: 체크박스 모두 클릭
                countHandler = @SuppressLint("HandlerLeak") object: Handler()
                {
                    override fun handleMessage(msg: Message)
                    {
                        super.handleMessage(msg)

                        if (msg.what === START_CODE)
                        {
                            e(TAG, "메시지 받음 START_CODE")
                        }
                        else if (msg.what === PROGRESS_CODE)
                        {
                            e(TAG, "메시지 받음 msg.what: " + msg.what)
                            e(TAG, "메시지 받음 msg.arg1: " + msg.arg1)

                            e(TAG, "countHandler countComp: " + countComp)

                            if (msg.arg1 == 1)
                            {
                                e(TAG, "countComp: Enable")
                                bop_order_progress_end_button.setBackgroundColor(Color.parseColor("#FFEA1C77"))
                                bop_order_progress_end_button.isEnabled = true
                                bop_progress_noti_message.text = "클릭하면 고객에게 '제작 완료'알림을 전달합니다"
                            }
                            else /*if (countComp == false)*/
                            {
                                e(TAG, "countComp: Disable")
                                // bop_order_progress_end_button.setBackgroundColor(Color.parseColor("#828282"))

                                bop_order_progress_end_button.setBackgroundColor(Color.GRAY)
                                bop_order_progress_end_button.isEnabled = false
                                bop_progress_noti_message.text = "고객에게 '제작 시작'알림을 전달했습니다"
                            }
                        }
                    }
                }

                // todo: 제작 완료버튼 클릭
                bop_order_progress_end_button.setOnClickListener(View.OnClickListener {


                    for (i in OrderDetailInfo.indices)
                    {
                        OrderDetailInfo.get(i).user = "none"
                        OrderDetailInfo.get(i).isChecked = false
                        e(TAG, "none으로 변경: user: " + OrderDetailInfo.get(i).user)
                    }

                    // 제작 완료버튼 숨기기
                    bop_order_progress_end_button.visibility = View.GONE

                    bop_recycler_view.adapter?.notifyDataSetChanged()

                    bop_progress_noti_message.text = "고객에게 '제작 완료'알림을 전달했습니다\n\n" + "주문 번호를 확인 후 전달해주세요"

                    // todo: 픽업 추천시간 구하기
                    // 현재시간
                    val current = LocalDateTime.now()
                    val formatter_1 = DateTimeFormatter.ofPattern("HH시 mm분") // 날짜 형식
                    val formatted_1 = current.format(formatter_1)
                    val formatter_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 서버 저장용
                    e(TAG, "formatted_1: $formatted_1")

                    // 픽업 추천시간 (~까지)
                    // 보관시간 (~까지)
                    val recomend =
                        current.plusMinutes(Integer.parseInt(itemOrderList.get(position).nest_Menu_Pick_Up_Recommend_Time).toLong())
                    val impossible =
                        current.plusMinutes(Integer.parseInt(itemOrderList.get(position).nest_Menu_Serving_Impossible_Time).toLong())

                    var recomendTime = recomend.format(formatter_1)
                    val impossibleTime = impossible.format(formatter_1)

                    // 서버 저장용 형식
                    var recomendTimeToServer = recomend.format(formatter_2)
                    val impossibleTimeToServer = impossible.format(formatter_2)

                    e(TAG, "nest_Menu_Pick_Up_Recommend_Time: " + recomendTime)
                    e(TAG, "nest_Menu_Serving_Impossible_Time: " + impossibleTime)

                    e(TAG, "recomendTimeToServer: " + recomendTimeToServer)
                    e(TAG, "impossibleTimeToServer: " + impossibleTimeToServer)

                    // 제작 완료시간 표시
                    progress_completion_time.text = "완성한 시간: " + formatted_1

                    // 보관 시간 표시
                    bop_serving_impossible_time_1.text = "매장 보관시간: " + impossibleTime + "까지"
                    bop_serving_impossible_time_2.text = "매장 보관시간: " + impossibleTime + "까지"

                    progress_completion_time.visibility = View.VISIBLE
                    bop_serving_impossible_time_1.visibility = View.VISIBLE
                    bop_serving_impossible_time_2.visibility = View.VISIBLE

                    var index = itemOrderList.get(position).nest_Order_Index

                    // 배열 순서 바꾸기
                    //                    for (i input itemOrderList.indices)
                    //                    {
                    //                        e(TAG, "OrderIndex: " + itemOrderList.get(i).nest_Order_Index + " i: $i")
                    //                        if(itemOrderList.get(i).nest_Order_Product_Completion_whether == "true")
                    //                        {
                    //                            e(TAG, "break?")
                    //                            Collections.swap(itemOrderList, position, /*itemOrderList.lastIndex*/i-1)
                    //                            recyclerViewTodayOrderList?.adapter?.notifyDataSetChanged()
                    //                            break
                    //                        }
                    //                    }

                    /*
         serialRoomNo
         compCode
        */

                    compCode = "777"
                    serialRoomNo = itemOrderList.get(position).nest_Order_Serial_Number

                    // todo: 관리자의 주문 접수 진행상태 업데이트
                    updateOrderStatus("end", recomendTimeToServer, impossibleTimeToServer, index)
                })

                // todo: 제작 시작버튼 클릭 후 상황 표시
                if (itemOrderList.get(position).nest_Order_Check_Whether == "false")
                {
                    e(TAG, "nest_Order_Check_Whether == false")
                    // 제작 시작 버튼, 제작 완료 버튼
                    bop_progress_noti_message.text = "'제작 시작' 전 상품의 재고를 확인하세요"
                    bop_order_progress_start_button.visibility = View.VISIBLE
                    bop_order_progress_end_button.visibility = View.GONE
                }

                // todo: 제작 완료 처리
                else
                {
                    e(TAG, "nest_Order_Check_Whether == true")
                    bop_progress_noti_message.text = "고객에게 '제작 시작'알림을 전달했습니다"
                    bop_order_progress_start_button.visibility = View.GONE
                    bop_order_progress_end_button.visibility = View.VISIBLE

                    // 제작 완료시간 표시
                    progress_completion_time.text = "완성한 시간: "

                    // 보관 시간 표시
                    bop_serving_impossible_time_1.text = "매장 보관시간: " + " " + "까지"
                    bop_serving_impossible_time_2.text = "매장 보관시간: " + " " + "까지"

                    progress_completion_time.visibility = View.VISIBLE
                    bop_serving_impossible_time_1.visibility = View.VISIBLE
                    bop_serving_impossible_time_2.visibility = View.VISIBLE
                }

                bop_serial_number.text = "D" + itemOrderList.get(position).nest_Order_Serial_Number
                bop_order_time.text = "결제한 시간: " + Date_of_payment


                // todo: 제작 완료했을 경우
                if (itemOrderList.get(position).nest_Order_Product_Completion_whether == "false")
                {
                    e(TAG, "nest_Order_Product_Completion_whether == false")
                    progress_completion_time.visibility = View.GONE
                    bop_serving_impossible_time_1.visibility = View.GONE
                    bop_serving_impossible_time_2.visibility = View.GONE
                }
                else
                {
                    e(TAG, "nest_Order_Product_Completion_whether == true")
                    progress_completion_time.visibility = View.VISIBLE
                    bop_serving_impossible_time_1.visibility = View.VISIBLE
                    bop_serving_impossible_time_2.visibility = View.VISIBLE

                    progress_completion_time.text =
                        "제작 완료: " + itemOrderList.get(position).nest_Order_Product_Completion
                    bop_serving_impossible_time_1.text = "보관 시간: " + CompletionTime + "까지"
                    bop_serving_impossible_time_2.text = "보관 시간: " + CompletionTime + "까지"

                    // todo: 제작 완료 처리
                    bop_order_progress_end_button.visibility = View.GONE
                    bop_progress_noti_message.text = "'제작 완료'알림을 전달했습니다"
                }

                // todo: 가격 표시
                bop_total_price.text = "총: " + itemOrderList.get(position).nest_Order_Price + "원"

                // 리사이클러뷰
                // todo: 단일 주문일 경우 로직
                if (itemOrderList.get(position).nest_Order_Way == "orderNow")
                {
                    e(TAG, "orderNow")
                    bop_order_count.text = "주문내역: 1건"

                    //                    var bop_recycler_view: RecyclerView = dialog_view.findViewById(R.id.bop_recycler_view) as RecyclerView

                    OrderDetailInfo.add(
                            Item_OrderHistory(
                                    "",
                                    itemOrderList.get(position).nest_Menu_Thumb,
                                    itemOrderList.get(position).nest_Menu_Name,
                                    itemOrderList.get(position).nest_Order_Serving_Way,
                                    itemOrderList.get(position).nest_Order_Menu_Option,
                                    itemOrderList.get(position).nest_Order_User_Request,
                                    itemOrderList.get(position).nest_Order_Price,
                                    "none", // '제작 시작' 버튼 누르면 'Barista'로 값 변경하기
                                    false
                                             )
                                       )

                    bop_recycler_view.adapter = Adapter_Order_BOP(Context_Fragment_B_Order_Management)
                    bop_recycler_view.adapter = bop_recycler_view.adapter

                    for (i in OrderDetailInfo.indices)
                    {
                        e(TAG, OrderDetailInfo.get(i).nest_Menu_Name)
                    }

                    bop_recycler_view.adapter?.notifyDataSetChanged()

                    /*
                        // 다이얼로그 종료되면 리스트 비우기
                        dialog.setOnDismissListener {
                            OrderDetailInfo.clear()
                            bop_recycler_view?.adapter?.notifyDataSetChanged()
                            checkCount = 0
                        }
                    */
                }

                // todo: 장바구니로 주문했을 경우 로직
                else if (itemOrderList.get(position).nest_Order_Way == "addCart")
                {
                    e(TAG, "addCart")
                    var orderCount: Int = Integer.parseInt(itemOrderList.get(position).CartTotalCount) + 1
                    bop_order_count.text = "주문내역 " + orderCount.toString() + "건"

                    // todo: 리사이클러뷰 세팅 /////////////
                    var orderCart = itemOrderList.get(position).nest_Order_Menu_Index

                    //building retrofit object
                    val retrofit = Retrofit.Builder().baseUrl(ApiClient.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build()

                    //Defining retrofit api service
                    val projectListRequest = retrofit.create(ApiInterface::class.java)

                    //defining the call
                    val listCall = projectListRequest.getOrderHistoryList(orderCart)

                    listCall.enqueue(object: Callback<List<Item_OrderHistory>>
                    {
                        override fun onResponse(call: Call<List<Item_OrderHistory>>,
                                                response: Response<List<Item_OrderHistory>>)
                        {
                            e(TAG, "chatList call onResponse = 수신 받음")

                            OrderDetailInfo = response.body() as ArrayList<Item_OrderHistory>

                            for (i in OrderDetailInfo.indices)
                            {
                                e(TAG, "nest_Order_Way: " + OrderDetailInfo.get(i).nest_Menu_Name)
                            }

                            //                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                            //
                            //                            }

                            bop_recycler_view.setOverScrollMode(View.OVER_SCROLL_NEVER)
                            bop_recycler_view.setOverScrollMode(View.OVER_SCROLL_NEVER)

                            bop_recycler_view?.adapter = Adapter_Order_BOP(Context_Fragment_B_Order_Management)
                            bop_recycler_view?.adapter = bop_recycler_view?.adapter


                            //                    bop_recycler_view.adapter?.notifyDataSetChanged()

                            //                            bop_recycler_view?.adapter?.notifyDataSetChanged()
                        }

                        override fun onFailure(call: Call<List<Item_OrderHistory>>, t: Throwable)
                        {
                            Toast.makeText(Context_Fragment_B_Order_Management, "리스트 로드 실패", Toast.LENGTH_SHORT).show()
                            e(TAG, "onFailure: t: " + t.message)
                        }
                    })
                }
            })

            // todo: 다이얼로그 종료되면 리스트 비우기
            dialog.setOnDismissListener {
                OrderDetailInfo.clear()
                //                itemOrderList.clear()

                bop_recycler_view?.adapter?.notifyDataSetChanged()

                checkCount = 0
                countHandler = null
            }

            // todo: 메뉴명, 가격 단일 주문일 경우 로직
            if (itemOrderList.get(position).nest_Order_Way == "orderNow")
            {
                // 단일 주문 메뉴명 표시
                holder.bol_name.text = itemOrderList.get(position).nest_Menu_Name

                // 단일 주문 가격 표시
                holder.bol_price.text = itemOrderList.get(position).nest_Order_Price + "원"
            }

            // todo: 메뉴명, 가격 장바구니로 주문했을 경우 로직
            else if (itemOrderList.get(position).nest_Order_Way == "addCart")
            {
                // holder.bol_name.text = itemOrderList.get(position).nest_Menu_Name

                // 장바구니 메뉴명 표시
                // 메뉴명, 결제한 가격
                if (Integer.parseInt(itemOrderList.get(position).CartTotalCount) == 0)
                {
                    holder.bol_name.text = itemOrderList.get(position).nest_Menu_Name
                }

                // 장바구니 가격 표시
                else
                {
                    holder.bol_name.text =
                        itemOrderList.get(position).nest_Menu_Name + " (외 " + itemOrderList.get(position).CartTotalCount + "개)"
                }

                holder.bol_price.text = "총 " + itemOrderList.get(position).nest_Order_Price + "원"
            }
        }

        // 뷰홀더
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
        {
            val item = view

            var noti_c_tumb = view.bol_tumb

            var bol_tumb_comp: TextView = view.bol_tumb_comp
            var bol_way = view.bol_way
            var bol_name = view.bol_name
            var bol_option = view.bol_option

            var bol_request = view.bol_request
            var bol_price = view.bol_price
            var bol_order_status = view.bol_order_status

            var bol_serial_number = view.bol_serial_number
        }

        // todo: 관리자의 주문 접수 진행상태 업데이트
        // updateOrderStatus()
        fun updateOrderStatus(updateType: String, Recommend_Time: String, Impossible_Time: String, index: String)
        {
            val stringRequest = object: StringRequest(Method.POST,
                    "http://115.68.231.84/getNestUpdateOrderStatus.php",
                    com.android.volley.Response.Listener { response ->
                        e(Fragment_Menu_Management.TAG, "onResponse: response = $response")

                        try
                        {
                            val jsonObject = JSONObject(response)

                            val success = jsonObject.getString("success")

                            if (success == "1")
                            {
                                e(Fragment_Menu_Management.TAG, "onResponse: 상태변경 완료")

                                getTodayOrderList(date!!)

                                if (compCode == "666")
                                {
                                    e(TAG, "compCode: $compCode")
                                    e(TAG, "serialRoomNo: $serialRoomNo")

                                    // todo: 제작 시작 신호 발신
                                    var message: String? = "orderStatus_Start"
                                    send = TCP_Manager.SendThreadd(TCP_Manager.socket!!, message!!)
                                    e(TAG, " ")
                                    e(TAG, " ===== SendThread ===== ")
                                    e(TAG, " message: $message")
                                    e(TAG, " socket: ${TCP_Manager.socket}")
                                    e(TAG, " ")

                                    RoomNo = serialRoomNo
                                    UserType = "Barista"

                                    TCPSendUerID = GET_ID
                                    send?.start()

                                    compCode = null
                                    serialRoomNo = null
                                }

                                else if (compCode == "777")
                                {
                                    e(TAG, "compCode: $compCode")
                                    e(TAG, "serialRoomNo: $serialRoomNo")

                                    e(TAG, "compCode: $compCode")
                                    e(TAG, "serialRoomNo: $serialRoomNo")
                                    // todo: 제작 완료신호 발신
                                    var message: String? = "orderStatus_End"
                                    send = TCP_Manager.SendThreadd(TCP_Manager.socket!!, message!!)
                                    e(TAG, " ")
                                    e(TAG, " ===== SendThread ===== ")
                                    e(TAG, " message: $message")
                                    e(TAG, " socket: ${TCP_Manager.socket}")
                                    e(TAG, " ===== ======= ===== ")
                                    e(TAG, " ")

                                    RoomNo = serialRoomNo
                                    UserType = "Barista"
                                    send?.start()

                                    compCode = null
                                    serialRoomNo = null
                                }

                            }
                            else
                            {
                                e(Fragment_Menu_Management.TAG, "onResponse: 문제발생")
                            }
                        }
                        catch (e: JSONException)
                        {
                            e.printStackTrace()
                            e(Fragment_Menu_Management.TAG, "onResponse: JSONException e: $e")
                        }
                    },
                    com.android.volley.Response.ErrorListener { error ->
                        e(Fragment_Menu_Management.TAG, "onErrorResponse: error: $error")
                    })
            {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>
                {
                    val params = HashMap<String, String>()

                    params.put("index", index)
                    params.put("updateType", updateType)

                    if (updateType == "end")
                    {
                        params.put("Recommend_Time", Recommend_Time)
                        params.put("Impossible_Time", Impossible_Time)
                    }

                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(Context_Fragment_B_Order_Management)
            requestQueue.add(stringRequest) // stringRequest = 바로 위에 회원가입 요청메소드 실행
        }

    } // 리사이클러뷰 끝

    //    // todo: 접속 스레드
    //    inner class SocketClient(var roomAndUserData: String): Thread() // 방 정보 ( 방번호 /  접속자 아이디 )): Thread()
    //    {
    //        var input: DataInputStream? = null
    //        var output: DataOutputStream? = null
    //
    //        //        fun run()
    //        override fun run()
    //        {
    //            try
    //            {
    //                // 채팅 서버에 접속 ( 연결 )  ( 서버쪽 ip와 포트 )
    //                socket = Socket(ipad2/*서버 ip*/, port/*포트*/)
    //
    //                e(TAG, "SocketClient run: 접속 시도: " + socket)
    //
    //                // 메세지를 서버에 전달 할 수 있는 통로 ( 만들기 )
    //                output = DataOutputStream(socket?.getOutputStream())
    //                input = DataInputStream(socket?.getInputStream())
    //
    //                output!!.writeUTF(roomAndUserData)
    //
    //                // (메세지 수신용 쓰레드 생성 ) 리시브 쓰레드 시작
    //                recevie = ReceiveThread(socket!!)
    //                recevie?.start()
    //
    //            }
    //            catch (e: Exception)
    //            {
    //                e.printStackTrace()
    //            }
    //
    //        }
    //    } //SocketClient의 끝
    //
    //    // todo: 발신 스레드
    //    inner class SendThread(socket: Socket, message: String?): Thread() // ip와 포트?, var sendmsg: String?): Thread()
    //    {
    //        var output: DataOutputStream? = null
    //        var sendmsg: String? = null
    //        var socket: Socket? = null
    //
    //        //        fun SendThread(socket: Socket, sendmsg: String)
    //        //        {
    //        //            e(TAG, "SendThread: 도달했음")
    //        //
    //        //            this.socket = socket
    //        //            this.sendmsg = sendmsg
    //        //            try
    //        //            {
    //        //                // 채팅 서버로 메세지를 보내기 위한  스트림 생성.
    //        //                output = DataOutputStream(socket.getOutputStream())
    //        //            }
    //        //            catch (e: Exception)
    //        //            {
    //        //                e.printStackTrace()
    //        //            }
    //        //
    //        //        }
    //
    //        init
    //        {
    //            e(TAG, "SendThread: 도달했음")
    //            try
    //            {
    //                // 채팅 서버로 메세지를 보내기 위한  스트림 생성.
    //                output = DataOutputStream(socket?.getOutputStream())
    //            }
    //            catch (e: Exception)
    //            {
    //                e.printStackTrace()
    //            }
    //
    //        }
    //
    //        // 서버로 메세지 전송 ( 이클립스 서버단에서 temp 로 전달이 된다.
    //        //        fun run()
    //        override fun run()
    //        {
    //            e(TAG, "run: SendThread: 실행됨")
    //            try
    //            {
    //                if (output != null)
    //                {
    //                    if (sendmsg != null)
    //                    {
    //                        //                        int roomNo = 1;
    //                        // 여기서 방번호와 상대방 아이디 까지 해서 보내줘야 할거같다 .
    //                        // 서버로 메세지 전송하는 부분
    //
    //                        // 방 번호 @ 유저 인덱스 @ 유저 이름 @ 메시지
    //                        //                        output.writeUTF(roomNo + "@" + targetID + "@" + targetNickName + "@" + sendmsg);
    //
    //                        //                         방 번호 @ 내 아이디 @ 상대 아이디 @ 메시지
    //                        //                        output.writeUTF(RoomNo + "@" + loginUserId + "@" + targetID + "@" + sendmsg + "@" + Type);
    //                        // todo:                 output!!.writeUTF(RoomNo + "│" + loginUserId + "│" + targetID + "│" + sendmsg + "│" + Type + "│" + Message_views)
    //
    //                        /*                      e(TAG, "(SendThread) roomNo: $RoomNo/ loginUserId    : $loginUserId")
    //                                                e(TAG, "(SendThread) targetID: $targetID/ targetNickName: $targetNickName")
    //                                                e(TAG, "(SendThread) sendmsg: " + sendmsg!!)
    //                                                e(TAG, "(SendThread) Type: " + Type + "_ 1: 텍스트 / 2: 사진")*/
    //
    //                        sendmsg = null
    //
    //                        // 리사이클러뷰의 아이템 위치를 아래로 내리기
    //                        //                        message_area.scrollToPosition(messageData.size() - 1);
    //                    }
    //                }
    //
    //            }
    //            catch (e: Exception)
    //            {
    //                e.printStackTrace()
    //            }
    //
    //        }
    //    }
    //
    //    // todo: 수신 스레드
    //    inner class ReceiveThread(socket: Socket): Thread()
    //    {
    //        var input: DataInputStream? = null
    //        //        var socket: Socket? = null
    //
    //        init
    //        {
    //            try
    //            {
    //                // 채팅 서버로부터 메세지를 받기 위한 스트림 생성.
    //                input = DataInputStream(socket.getInputStream())
    //                e(TAG, "ReceiveThread try input: 연결됨 ")
    //            }
    //            catch (e: Exception)
    //            {
    //                e.printStackTrace()
    //            }
    //
    //        }
    //
    //        override fun run()
    //        {
    //            try
    //            {
    //                while (input != null)
    //                {
    //                    // 채팅 서버로 부터 받은 메세지
    //                    val msg = input!!.readUTF()
    //                    e(TAG, "ReceiveThread: run(): String msg = null?: " + msg!!)
    //
    //                    if (msg != null)
    //                    {
    //                        e(TAG, "ReceiveThread: run() = input.readUTF(): $msg")
    //
    //                        // 핸들러에게 전달할 메세지 객체
    //                        val hdmg = msgHandler?.obtainMessage()
    //
    //                        e(TAG, "ReceiveThread: hdmg = msgHandler.obtainMessage(): $hdmg")
    //
    //                        // 핸들러에게 전달할 메세지의 식별자
    //                        hdmg?.what = 1111
    //
    //                        // 메세지의 본문
    //                        hdmg?.obj = msg
    //
    //                        // 핸들러에게 메세지 전달 ( 화면 처리 )
    //                        msgHandler?.sendMessage(hdmg)
    //
    //                        // 방 목록 갱신하기
    //                        //                        ChatRoomList_Request();
    //
    //                        e(TAG, "ReceiveThread: run()에서 onCreate의 핸들러로 메시지 전달함 ")
    //
    //                        // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
    //                        //                        message_area.smoothScrollToPosition(messageData.size());
    //                    }
    //                }
    //            }
    //            catch (e: Exception)
    //            {
    //                e.printStackTrace()
    //                e(TAG, "run: Exception e: $e")
    //            }
    //
    //        }
    //    }


    // 어댑터 주문 목록
    //    inner class Adapter_Order_BOP(val context: Context?): RecyclerView.Adapter<Adapter_Order_BOP.ViewHolder>()
    //    {
    //        var TAG = "Adapter_Order_BOP"
    //
    //        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    //        {
    //            return ViewHolder(
    //                    LayoutInflater.from(context).inflate(
    //                            R.layout.item_order_history_type_2, parent, false
    //                                                        )
    //                             )
    //        }
    //
    //        override fun getItemCount(): Int
    //        {
    //            return OrderDetailInfo.size
    //        }
    //
    //        override fun onBindViewHolder(holder: ViewHolder, position: Int)
    //        {
    //            e(TAG, "값 들어오냐?")
    //
    //            // 썸네일 이미지
    //            Picasso.get().load(OrderDetailInfo.get(position).nest_Menu_Thumb)
    //                .placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
    //                //.resize(300,300)       // 300x300 픽셀
    //                .centerCrop()            // 중앙을 기준으로 잘라내기 (전체 이미지가 약간 잘릴 수 있다)
    //                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
    //                .into(holder.oht_2_thumb) // this인 ImageView에 로드
    //
    //            var count = position + 1
    //
    //            holder.oht_2_way.text = OrderDetailInfo.get(position).nest_Order_Serving_Way + " #$count"
    //            holder.oht_2_name.text = OrderDetailInfo.get(position).nest_Menu_Name
    //            holder.oht_2_price.text = OrderDetailInfo.get(position).nest_Order_Price + "원"
    //
    //            holder.oht_2_option.text = OrderDetailInfo.get(position).nest_Order_Menu_Option
    //            holder.oht_2_request.text = OrderDetailInfo.get(position).nest_Order_User_Request
    //
    //            // todo: 체크박스 처리
    //            if (OrderDetailInfo.get(position).user == "Barista")
    //            {
    //                holder.oht_2_check.visibility = View.VISIBLE
    //
    //                if (OrderDetailInfo.get(position).isChecked == false)
    //                {
    //                    holder.oht_2_check.setImageResource(R.drawable.ic_un_check_all)
    //                }
    //                else
    //                {
    //                    holder.oht_2_check.setImageResource(R.drawable.ic_check_all)
    //                }
    //            }
    //
    //            holder.item.setOnClickListener(View.OnClickListener {
    //
    //                if (OrderDetailInfo.get(position).isChecked == true)
    //                {
    //                    OrderDetailInfo.get(position).isChecked = false
    //
    //                    holder.oht_2_check.setImageResource(R.drawable.ic_check_all)
    //
    //                    bop_recycler_view?.adapter?.notifyDataSetChanged()
    //                }
    //                else
    //                {
    //                    OrderDetailInfo.get(position).isChecked = true
    //
    //                    holder.oht_2_check.setImageResource(R.drawable.ic_un_check_all)
    //
    //                    bop_recycler_view?.adapter?.notifyDataSetChanged()
    //                }
    //            })
    //        }
    //
    //        // 뷰홀더
    //        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    //        {
    //            val item = view
    //
    //            var oht_2_thumb = view.oht_2_thumb
    //
    //            var oht_2_way = view.oht_2_way
    //            var oht_2_name = view.oht_2_name
    //            var oht_2_price = view.oht_2_price
    //
    //            var oht_2_option = view.oht_2_option
    //            var oht_2_request = view.oht_2_request
    //
    //            var oht_2_check = view.oht_2_check
    //
    //        }
    //    } // 리사이클러뷰 끝


}
