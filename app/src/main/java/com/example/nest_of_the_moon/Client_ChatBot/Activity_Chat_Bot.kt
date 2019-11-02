package com.example.nest_of_the_moon.Client_ChatBot

import ai.api.AIDataService
import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import ai.api.model.Result
import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log.e
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.example.nest_of_the_moon.R
import com.example.recycler_view_multi_view_test.Item_chatBot_Message
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.MESSAGE_BOT
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.MESSAGE_USER
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_chat_bot.*
import java.lang.Exception
import java.util.*
import android.os.Handler
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.ORDER_CATEGORY
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.ORDER_CONFIRM
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.ORDER_MENU
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.ORDER_PAY
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.ORDER_SERIAL

class Activity_Chat_Bot : AppCompatActivity(), AIListener
{

    private val TAG = "Activity_Chat_Bot"
    private val PERMISSION_REQUEST_AUDIO = 0

    private var config: AIConfiguration? = null
    private var aiService: AIService? = null
    private var dataService: AIDataService? = null

    var response: AIResponse? = null
    var result: Result? = null

    //    val chatList = arrayListOf<Item_chatBot_Message>()

    @RequiresApi(Build.VERSION_CODES.N)
    var mFormat = SimpleDateFormat("hh:mm")

    // 현재 시간 받아오기
    var mNow: Long = 0
    var mDate: Date? = null
    var time: String? = null

    var chatBotReceiveHandler: Handler? = null

    var messagee: String? = null
    var intentNamee: String? = null

    val chatList = arrayListOf<Item_chatBot_Message>()

    companion object
    {
        val chatBotOrderOptions = arrayListOf<Item_chatBot_Order_options>()
        var orderOptionsHandler: Handler? = null
        var orderOptionsReceiveHandler: Handler? = null
        var menuClickReceiveHandler: Handler? = null
        var confirmClickReceiveHandler: Handler? = null
        var payClickReceiveHandler: Handler? = null
        var payConfirmClickReceiveHandler: Handler? = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)
        //TODO: add here your client access token from DialogFlow console
        //TODO: DialogFlow 콘솔에서 클라이언트 액세스 토큰을 여기에 추가하십시오.

        // todo: 다이얼로그 플로우 서버로 연결하기
        config = AIConfiguration(
            "b9def834e7bc4ac1b55142409d1f2bd4",
            ai.api.AIConfiguration.SupportedLanguages.Korean,
            AIConfiguration.RecognitionEngine.System
                                )

        // todo: 입력 대기 (클라이언트의 값 입력 대기)
        aiService = AIService.getService(this, config)
        dataService = AIDataService(config)
        aiService!!.setListener(this)

        // todo: 리사이클러뷰 세팅
        recylcer_view_chat_bot!!.layoutManager = LinearLayoutManager(this)
        recylcer_view_chat_bot!!.adapter = Adapter_ChatBot_Message_MultiView(this, chatList)
        // recylcer_view_chat_bot.setAdapter(recylcer_view_chat_bot?.adapter)

        // todo: 단축키로 메시지 전송: 안녕!
        shortcut_message_1.setOnClickListener(View.OnClickListener {
            shortcutMessage("안녕!")
        })

        // todo: 단축키로 메시지 전송: 메뉴 보여줘!
        shortcut_message_2.setOnClickListener(View.OnClickListener {
            shortcutMessage("메뉴 보여줘")
        })

        // 단축기 숨김 처리
//        openSoftKeyboard(this, shortcut_message_area)
//        if (chat_message_content.isFocused)
//        {
//            shortcut_message_area.visibility = View.GONE
//        }
//        else
//        {
//            shortcut_message_area.visibility = View.VISIBLE
//        }

        // 채팅 메시지 맨 아래로 내리기
        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

        // 음성인식 버튼
        chat_mic_button.setOnClickListener(View.OnClickListener {
            // todo: 음성인식
            listen()
        })

        // todo: 메시지 입력창
        chat_message_content.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(p0: Editable?)
            {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {
                var text: String = chat_message_content.text.toString()

                // 보낼 내용이 없을 땐 전송버튼의 불 끔
                if (TextUtils.isEmpty(text) && text == "")
                {
                    chat_send_button.setImageResource(R.drawable.ic_send_button_1)
                    chat_send_button.setEnabled(false) // 전송버튼 비활성화

                    shortcut_message_area.visibility = View.VISIBLE
                }

                // 작성한 내용이 있다면 전송버튼의 불 켬
                else
                {
                    shortcut_message_area.visibility = View.GONE

                    chat_send_button.setImageResource(R.drawable.ic_send_button_2)
                    chat_send_button.setEnabled(true) // 전송버튼 비활성화

                    // todo: 전송버튼 클릭 / 메시지 전송하기
                    chat_send_button.setOnClickListener(View.OnClickListener {

                        // 입력창 비워주기
                        chat_message_content.text = null

                        // 현재시간 구하기
                        mNow = System.currentTimeMillis()
                        mDate = Date(mNow)
                        time = mFormat.format(mDate)

                        // 리사이클러뷰에 추가하기
                        chatList.add(
                            Item_chatBot_Message(
                                MESSAGE_USER,
                                text,
                                time.toString(),
                                null,
                                null,
                                false,
                                false,
                                false
                                                )
                                    )

                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

                        // todo: 입력한 내용 전송하기 (dialogFlow로)
                        try
                        {
                            object : Thread()
                            {
                                override fun run()
                                {
                                    response = aiService!!.textRequest(AIRequest(text))
                                    result = response!!.result

                                    // todo: 핸들러로 응답 알림 감지
                                    if (result != null)
                                    {
                                        // todo: 입력한 내용 결과값 받기
                                        e(TAG, "result: $result")
                                        e(TAG, "result.resolvedQuery: " + result!!.resolvedQuery) // 내가 입력한 내용
                                        e(TAG, "result.fulfillment.speech: " + result!!.fulfillment.speech) // 챗봇의 응답
                                        e(
                                            TAG,
                                            "result.metadata.intentName: " + result!!.metadata.intentName
                                         ) // 인텐트 Name

                                        // 챗봇의 응답을 변수에 담아 핸들러로 전송
                                        messagee = result!!.fulfillment.speech

                                        e(TAG, "messagee: $messagee")
                                        e(TAG, "intentNamee: $intentNamee")

                                        // 핸들러로 말풍선 추가 요청
                                        val message: Message = chatBotReceiveHandler?.obtainMessage()!!
                                        message.what = PROGRESS_CODE
                                        message.arg1 = 2222

                                        chatBotReceiveHandler!!.sendMessage(message)
                                    }
                                }
                            }.start()
                        }
                        catch (ex: Exception)
                        {
                            ex.printStackTrace()
                            e(TAG, "ex: $ex")
                        }
                    })
                }

            }
        })

        // 핸들러 그룹
        handlerGroup()
    }


    var receiveSplit: List<String>? = null

    // 핸들러 그룹
    fun handlerGroup()
    {
//        chatBotOrderOptions.add(Item_chatBot_Order_options(1, "아메리카노", false))
//        chatBotOrderOptions.add(Item_chatBot_Order_options(1, "라떼", false))
//        chatBotOrderOptions.add(Item_chatBot_Order_options(1, "병음료", false))
//        chatBotOrderOptions.add(Item_chatBot_Order_options(1, "디저트", false))

        // todo: 챗봇의 응답 메시지 수신받기
        chatBotReceiveHandler = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == 101)
                {
                    if (msg.arg1 == 2222)
                    {
                        // 인텐트 Name으로 말풍선 유형 바꾸기
                        intentNamee = result!!.metadata.intentName

                        e(TAG, "intentName: " + intentNamee.toString())
                        e(TAG, "message: $messagee")

                        // todo: 메뉴 카테고리 받기
                        if (intentNamee == "request_coffee_category")
                        {
                            val list = mutableListOf<Item_chatBot_Order_options>().apply {
                                add(Item_chatBot_Order_options(ORDER_CATEGORY, "아메리카노", false, true))
                                add(Item_chatBot_Order_options(ORDER_CATEGORY, "라떼", false, true))
                                add(Item_chatBot_Order_options(ORDER_CATEGORY, "병음료", false, true))
                                add(Item_chatBot_Order_options(ORDER_CATEGORY, "디저트", false, true))
                            }

                            chatList.add(
                                Item_chatBot_Message(
                                    ORDER_CATEGORY,
                                    messagee.toString(),
                                    "none",
                                    list,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                        }
                        else if (intentNamee == "asdasd")
                        {
                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_BOT,
                                    messagee.toString(),
                                    "none",
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )
                        }

                        // todo: 일반 메시지 받기
                        else // intentName: Default Welcome Intent
                        {
                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_BOT,
                                    messagee.toString(),
                                    "none",
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )
                        }

                        // 리사이클러뷰 갱신
                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

                        result = null
                        intentNamee = null
                    }
                }
            }
        }

        //  ---------------------------------------------------
        // todo: 카테고리 말풍선 클릭 감지
        orderOptionsReceiveHandler = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == PROGRESS_CODE)
                {
                    if (msg.arg1 == 1123)
                    {
                        e(TAG, "클릭 알림 전달받음!!. 'Adapter_ChatBot_Message_MultiView' 클래스에서 받음")

                        var option_name: String = msg.obj.toString()
                        e(TAG, "option_name: $option_name")

                        // 현재시간 구하기
                        mNow = System.currentTimeMillis()
                        mDate = Date(mNow)
                        time = mFormat.format(mDate)

                        chatList.add(
                            Item_chatBot_Message(
                                MESSAGE_USER,
                                option_name,
                                time.toString(),
                                null,
                                null,
                                false,
                                false,
                                false
                                                )
                                    )

                        // 리사이클러뷰 갱신
                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

                        // todo: 메뉴 목록 말풍선 불러오기
                        // 병음료, 디저트 선택 응답
                        if (option_name == "병음료" || option_name == "디저트")
                        {
                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_BOT,
                                    "현재 판매중인 ${option_name}입니다.",
                                    "none",
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )
                        }

                        // 커피 선택 응답
                        else
                        {
                            // 메뉴 목록
                            val list = mutableListOf<Item_chatBot_Order_options>().apply {
                                add(Item_chatBot_Order_options(ORDER_MENU, "네로", false, true))
                                add(Item_chatBot_Order_options(ORDER_MENU, "케냐 뚱구리", false, true))
                                add(Item_chatBot_Order_options(ORDER_MENU, "과테말라 와이칸", false, true))
                                add(Item_chatBot_Order_options(ORDER_MENU, "콜롬비아 엘 에크레오", false, true))
                            }

                            chatList.add(
                                Item_chatBot_Message(
                                    ORDER_MENU,
                                    "아래 원두로 ${option_name}를 제공할 수 있습니다.",
                                    "none",
                                    list,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 1초 딜레이 후 챗봇이 대답하기 (클라이언트에서 처리)
//                            Thread.sleep(1000)
                        }

                        // 리사이클러뷰 갱신
                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

                    }


                    // todo: 선택 취소 처리
                    else if (msg.arg1 == 1124)
                    {
                        var option_name: String = msg.obj.toString()
                        e(TAG, "선택 취소: $option_name")
                        if (option_name == "선택 취소")
                        {
                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_BOT,
                                    "선택 취소",
                                    "none",
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                        }
                    }
                }
            }
        }

        //  ---------------------------------------------------
        // todo: 메뉴목록 선택 완료알림 받기
        menuClickReceiveHandler = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == PROGRESS_CODE)
                {
                    if (msg.arg1 == 1155)
                    {
                        // 현재시간 구하기
                        mNow = System.currentTimeMillis()
                        mDate = Date(mNow)
                        time = mFormat.format(mDate)

                        chatList.add(
                            Item_chatBot_Message(
                                MESSAGE_USER,
                                "골랐어",
                                time.toString(),
                                null,
                                null,
                                false,
                                false,
                                false
                                                )
                                    )

                        // 리사이클러뷰 갱신
                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)




                        var receive: String = msg.obj.toString()
                        receiveSplit = receive.split(", ".toRegex())
                        var selectList: String? = null

                        // todo: accept 처리
                        if (receiveSplit!![0] == "menu_accept")
                        {
                            for (i in receiveSplit!!.indices)
                            {
                                if (receiveSplit!![i] == "menu_accept")
                                {
                                    e(TAG, "배열에서 menu_accept 거르기")
                                }
                                else
                                {
                                    // 주문한 목록 담기
                                    if (selectList == null)
                                    {
                                        selectList = i.toString() + ". " + receiveSplit!![i]
                                    }
                                    else
                                    {
                                        selectList = selectList + "\n$i. " + receiveSplit!![i]
                                    }
                                }
                            }

                            chatList.add(
                                Item_chatBot_Message(
                                    ORDER_CONFIRM,
                                    "${selectList}\n\n해당 메뉴를 주문하시겠습니까?",
                                    time.toString(),
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                        }

                        // todo: cancel 처리
                        else
                        {
                            // 현재시간 구하기
                            mNow = System.currentTimeMillis()
                            mDate = Date(mNow)
                            time = mFormat.format(mDate)

                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_USER,
                                    "주문 안 할래",
                                    time.toString(),
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_USER,
                                    "네, 주문 절차를 취소하겠습니다.",
                                    time.toString(),
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                        }


//                        // 리사이클러뷰 갱신
//                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()
//
//                        // 채팅 메시지 맨 아래로 내리기
//                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                    }
                }
            }
        }

        //  ---------------------------------------------------
        // todo: 메뉴목록 선택 완료알림 받기
        confirmClickReceiveHandler = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == PROGRESS_CODE)
                {
                    if (msg.arg1 == 1156)
                    {
                        var receive = msg.obj.toString()

                        if (receive == "confirm_accept")
                        {
                            // 현재시간 구하기
                            mNow = System.currentTimeMillis()
                            mDate = Date(mNow)
                            time = mFormat.format(mDate)

                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_USER,
                                    "주문할게",
                                    time.toString(),
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)


                            // todo: 결제 말풍선 띄워주기

                            // 메뉴 목록
                            var list = mutableListOf<Item_chatBot_Pay_Option>()

                            // 선택한 메뉴를 결제 말풍선에 추가하기
                            for (i in receiveSplit!!.indices)
                            {
                                if(receiveSplit!![i] == "menu_accept")
                                {

                                }
                                else
                                {
                                    e(TAG, "receiveSplit: $i: " + receiveSplit!![i])

                                    list.add(Item_chatBot_Pay_Option("Americano",
                                        receiveSplit!![i],
                                        4000,
                                        1,
                                        true,
                                        false,
                                        false))
                                }
                            }

                            chatList.add(
                                Item_chatBot_Message(
                                    ORDER_PAY,
                                    "주문 목록을 완성했습니다.\n결제 버튼을 눌러 주문할 수 있습니다.",
                                    "none",
                                    null,
                                    list,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                        }

                        // todo: 주문 결정 취소처리
                        else
                        {
                            // 현재시간 구하기
                            mNow = System.currentTimeMillis()
                            mDate = Date(mNow)
                            time = mFormat.format(mDate)

                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_USER,
                                    "아니 안 할래",
                                    time.toString(),
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)



                            chatList.add(
                                Item_chatBot_Message(
                                    MESSAGE_BOT,
                                    "취소했습니다,",
                                    time.toString(),
                                    null,
                                    null,
                                    false,
                                    false,
                                    false
                                                    )
                                        )

                            // 리사이클러뷰 갱신
                            recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                            // 채팅 메시지 맨 아래로 내리기
                            recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                        }
                    }
                }
            }
        }

        //  ---------------------------------------------------
        // todo: 메뉴목록 선택 완료알림 받기
        payConfirmClickReceiveHandler = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == PROGRESS_CODE)
                {
                    if (msg.arg1 == 11676)
                    {
                        var totalPrice: String = msg.obj.toString()

                        chatList.add(
                            Item_chatBot_Message(
                                ORDER_SERIAL,
                                "13",
                                time.toString(),
                                null,
                                null,
                                false,
                                false,
                                false
                                                )
                                    )

                        // 리사이클러뷰 갱신
                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
                    }
                }
            }
        }
    }


    // todo: 단축키로 메시지 전송
    @RequiresApi(Build.VERSION_CODES.N)
    fun shortcutMessage(text: String)
    {
        // 현재시간 구하기
        mNow = System.currentTimeMillis()
        mDate = Date(mNow)
        time = mFormat.format(mDate)

        // 리사이클러뷰에 추가하기
        chatList.add(Item_chatBot_Message(MESSAGE_USER, text, time.toString(), null, null, false, false, false))
        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

        try
        {
            object : Thread()
            {
                override fun run()
                {
                    response = aiService!!.textRequest(AIRequest(text))
                    result = response!!.result

                    // todo: 핸들러로 응답 알림 감지
                    if (result != null)
                    {
                        // todo: 입력한 내용 결과값 받기
                        e(TAG, "result: $result")
                        e(TAG, "result.resolvedQuery: " + result!!.resolvedQuery) // 내가 입력한 내용
                        e(TAG, "result.fulfillment.speech: " + result!!.fulfillment.speech) // 챗봇의 응답
                        e(TAG, "result.metadata.intentName: " + result!!.metadata.intentName) // 인텐트 Name

                        // 챗봇의 응답을 변수에 담아 핸들러로 전송
                        messagee = result!!.fulfillment.speech

                        val message: Message = chatBotReceiveHandler?.obtainMessage()!!
                        message.what = PROGRESS_CODE
                        message.arg1 = 2222

                        chatBotReceiveHandler!!.sendMessage(message)
                    }
                }
            }.start()
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            e(TAG, "ex: $ex")
        }
    }


    // todo: 음성인식 관련 메소드 시작
    private fun listen()
    {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
                                              ) == PackageManager.PERMISSION_GRANTED
        )
        {
            // todo: 음성인식 시작
            aiService!!.startListening()
            e(TAG, "listen(), aiService!!.startListening()")

            //            var listening: String = aiService!!.startListening().toString()
            //            e(TAG, "listening: $listening")
        }
        else
        {
            // Permission is missing and must be requested.
            // 권한이 누락되어 요청해야합니다.
            requestAudioPermission()
        }
    }

    // todo: 다이얼로그 플로우에 전송한 값의 결과 응답받기
    @TargetApi(Build.VERSION_CODES.O)
    override fun onResult(response: AIResponse)
    {
        result = response.result

        e(TAG, "result: $result")
        // inputText.setText(getString(R.string.input, result.resolvedQuery))
        // fulfillmentText.setText(getString(R.string.answer, result.fulfillment.speech))

        // todo: 내가 입력한 값
        //        inputText.setText("당신: " + result.resolvedQuery)

        // todo: 다이얼로그 플로우의 응답
        //        fulfillmentText.setText("챗봇: " + result.fulfillment.speech)

        // todo: 인텐트 유형
        //        actionText.setText("인텐트: " + result.metadata.intentName)

        e(TAG, "당신: " + result!!.resolvedQuery)
        e(TAG, "챗봇: " + result!!.fulfillment.speech)
        e(TAG, "인텐트: " + result!!.metadata.intentName)

        // 현재시간 구하기
        mNow = System.currentTimeMillis()
        mDate = Date(mNow)
        time = mFormat.format(mDate)

        // 유저
        chatList.add(
            Item_chatBot_Message(
                MESSAGE_USER,
                result!!.resolvedQuery,
                time.toString(),
                null,
                null,
                false,
                false,
                false
                                )
                    )

        // 챗봇
        chatList.add(Item_chatBot_Message(MESSAGE_BOT, result!!.fulfillment.speech, "none", null, null, false, false, false))
        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

        // 채팅 메시지 맨 아래로 내리기
        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)
    }

    override fun onError(error: AIError)
    {
        e(TAG, "Listening error: " + error.message)
        lottieMicAnimArea.visibility = View.GONE
        lottieMicAnimView.pauseAnimation()
    }

    override fun onAudioLevel(level: Float)
    {
        //        e(TAG, "onAudioLevel: $level")
    }

    override fun onListeningStarted()
    {
        e(TAG, "onListeningStarted() ")

        // 음성인식 애니메이션 활성화
        lottieMicAnimArea.visibility = View.VISIBLE
        lottieMicAnimView.setAnimation("listen-state.json")
        lottieMicAnimView.repeatCount = LottieDrawable.INFINITE
        lottieMicAnimView.playAnimation()
    }

    override fun onListeningCanceled()
    {
        e(TAG, "onListeningCanceled() ")
        lottieMicAnimArea.visibility = View.GONE
        lottieMicAnimView.pauseAnimation()
    }

    // 음성인식 시작
    override fun onListeningFinished()
    {
        //        listenButton.setText("질문하세요")
        //        e(TAG, "질문 시작")
        lottieMicAnimArea.visibility = View.GONE
        lottieMicAnimView.pauseAnimation()
    }
    // 음성인식 관련 메소드 끝

    // todo: 오디오 관련 퍼미션
    // 퍼미션 허가 후 음성인식 시작하기
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        if (requestCode == PERMISSION_REQUEST_AUDIO)
        {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                aiService!!.startListening()
                e(TAG, "onRequestPermissionsResult(): aiService!!.startListening()")
            }
            else
            {
                e(TAG, "permission denied")
            }
        }
    }

    // 오디오 퍼미션
    private fun requestAudioPermission()
    {
        // Permission has not been granted and must be requested.
        // 권한이 부여되지 않았으며 요청해야합니다.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.

            /*
                권한이 부여되지 않고 사용자가 권한 사용에 대한 추가 컨텍스트의 이점을 얻는 경우
                사용자에게 추가 근거를 제공하십시오.
                누락 된 권한을 요청하려면 cda 단추가있는 스낵바를 표시하십시오.
            */

            Snackbar.make(
                findViewById(R.id.main_container), "음성 기능을 사용하기 위해 다음 권한을 요청합니다", Snackbar.LENGTH_INDEFINITE
                         ).setAction("OK", View.OnClickListener {

                // Request the permission
                // 퍼미션 요청
                ActivityCompat.requestPermissions(
                    this@Activity_Chat_Bot, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_AUDIO
                                                 )
            }).show()

        }
        else
        {
            // Request the permission. The result will be received in onRequestPermissionResult().
            // 권한을 요청하십시오. 결과는 onRequestPermissionResult ()에 수신됩니다.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_AUDIO
                                             )
        }
    }
}


/*
// 키보드 활성화 감지
fun openSoftKeyboard(context: Context, view: View)
{
    view.requestFocus()
    // open the soft keyboard
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

    chat_message_content.setOnClickListener(View.OnClickListener {
        e(TAG, "showSoftInput: " + imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT))
    })
        if (imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT))
        {
            shortcut_message_area.visibility = View.GONE
        }
        else
        {
            shortcut_message_area.visibility = View.VISIBLE
        }

}*/
