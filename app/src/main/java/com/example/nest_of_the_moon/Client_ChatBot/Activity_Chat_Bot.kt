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
import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.Service.nestService
import com.example.nest_of_the_moon.TCP_Manager
import com.example.recycler_view_multi_view_test.Item_chatBot_Message
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.MESSAGE_BOT
import com.example.recycler_view_multi_view_test.Item_chatBot_Message.Companion.MESSAGE_USER
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_chat_bot.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import com.example.nest_of_the_moon.Service.nestService.Companion.PROGRESS_CODE

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

    var receiveHandler: Handler? = null

    var messagee: String? = null
    var intentName: String? = null

    companion object
    {
        val chatList = arrayListOf<Item_chatBot_Message>()
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
        recylcer_view_chat_bot!!.adapter = Adapter_ChatBot_Message_MultiView(this@Activity_Chat_Bot)
        //        recylcer_view_chat_bot.setAdapter(recylcer_view_chat_bot?.adapter)

        // todo: 단축키로 메시지 전송: 안녕!
        shortcut_message_1.setOnClickListener(View.OnClickListener {
            shortcutMessage("안녕!")
        })

        // todo: 단축키로 메시지 전송: 안녕!
        shortcut_message_2.setOnClickListener(View.OnClickListener {
            shortcutMessage("메뉴 보여줘")
        })

        openSoftKeyboard(this, shortcut_message_area)

        // 단축기 숨김 처리
//        if (chat_message_content.isFocused) {
//            shortcut_message_area.visibility = View.GONE
//        } else {
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
                        chatList.add(Item_chatBot_Message(MESSAGE_USER, text, time.toString(), null, false, false))
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
                                        intentName = result!!.metadata.intentName

                                        val message: Message = receiveHandler?.obtainMessage()!!
                                        message.what = PROGRESS_CODE
                                        message.arg1 = 2222

                                        receiveHandler!!.sendMessage(message)
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

        // todo: 메시지 수신받기
        receiveHandler = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == 101)
                {
                    if (msg.arg1 == 2222)
                    {

                        if (intentName == "menu")
                        {
                            chatList.add(Item_chatBot_Message(MESSAGE_BOT, messagee.toString(), "none", null, false, false))
                        }
                        else if(intentName == "asdasd")
                        {
                            chatList.add(Item_chatBot_Message(MESSAGE_BOT, messagee.toString(), "none", null, false, false))
                        }

                        else
                        {
                            chatList.add(Item_chatBot_Message(MESSAGE_BOT, messagee.toString(), "none", null, false, false))
                        }

                        recylcer_view_chat_bot!!.adapter!!.notifyDataSetChanged()

                        // 채팅 메시지 맨 아래로 내리기
                        recylcer_view_chat_bot!!.scrollToPosition(chatList.size - 1)

                        result = null
                        intentName = null
                    }
                }
            }
        }
    }

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
/*
        if (imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT))
        {
            shortcut_message_area.visibility = View.GONE
        }
        else
        {
            shortcut_message_area.visibility = View.VISIBLE
        }
*/

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
        chatList.add(Item_chatBot_Message(MESSAGE_USER, text, time.toString(), null, false, false))
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

                        val message: Message = receiveHandler?.obtainMessage()!!
                        message.what = PROGRESS_CODE
                        message.arg1 = 2222

                        receiveHandler!!.sendMessage(message)
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

    // todo: 음성인식
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
        chatList.add(Item_chatBot_Message(MESSAGE_USER, result!!.resolvedQuery, time.toString(), null, false, false))

        // 챗봇
        chatList.add(Item_chatBot_Message(MESSAGE_BOT, result!!.fulfillment.speech, "none", null, false, false))
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
