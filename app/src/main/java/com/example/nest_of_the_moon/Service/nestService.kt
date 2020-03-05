package com.example.nest_of_the_moon.Service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log.e
import com.example.nest_of_the_moon.Barista.Activity_Barista_Home
import com.example.nest_of_the_moon.Client.Activity_Client_Home
import com.example.nest_of_the_moon.TCP_Manager
import com.example.nest_of_the_moon.TCP_Manager.Companion.client
import com.example.nest_of_the_moon.TCP_Manager.Companion.msgFilter
import com.example.nest_of_the_moon.TCP_Manager.Companion.msgHandler
import android.content.Context
import android.app.*
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class nestService: Service()
{
    var TAG = "nestService"

    companion object
    {
        var serviceType: String? = null // waitingRoomB or waitingRoomC or serialRoomB or serialRoomC

        var orderRequestHandler: Handler? = null // 핸들러: 체크박스가 몇 개 체크했는지 확인하기
        val START_CODE = 100
        val PROGRESS_CODE = 101
    }

    override fun onBind(intent: Intent): IBinder
    {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        e(TAG, "서비스 onStartCommand()")

        if (serviceType == "waitingRoomC")
        {
            // todo: 웨이팅 서버 접속(바리스타)
            // 방 번호
            var waitingRoomNo = "777"
            var loginUsertype = "Client"
            var loginUserId: String = Activity_Client_Home.GET_ID.toString()
            var orderRequest = "imClient" /*    or "newOrderRequest"    */

            var waitingRoomAndUserData: String =
                waitingRoomNo + "@" + loginUsertype + "@" + loginUserId + "@" + orderRequest

            e(TAG, "onStartCommand: roomAndUserData: $waitingRoomAndUserData")

            // 방번호와 유저의 이름으로 서버에 접속한다
            client = TCP_Manager.SocketClient(waitingRoomAndUserData)
            client?.start()

            e(TAG, "(service) 웨이팅서버 접속 완료")

            // 웨이팅 룸 접속 완료
        }
        else if (serviceType == "waitingRoomB")
        {
            // todo: 웨이팅 서버 접속(바리스타)
            // 방 번호
            var waitingRoomNo = "777"
            var loginUsertype = "Barista"
            var loginUserId: String = Activity_Barista_Home.GET_ID.toString()
            var orderRequest = "imBarista" /*    or "newOrderRequest"    */

            var waitingRoomAndUserData: String =
                waitingRoomNo + "@" + loginUsertype + "@" + loginUserId + "@" + orderRequest
            e(TAG, "onStartCommand: roomAndUserData: $waitingRoomAndUserData")

            // 방번호와 유저의 이름으로 서버에 접속한다
            client = TCP_Manager.SocketClient(waitingRoomAndUserData)
            client?.start()

            e(TAG, "(service) 웨이팅서버 접속 완료")

            // 웨이팅 룸 접속 완료
        }

        // 통신 유형: 주문 요청
        // todo: 웨이팅 룸에서 실시간 주문 수신 받기
        msgHandler = @SuppressLint("HandlerLeak") object: Handler()
        {
            override fun handleMessage(msg: Message)
            {
                if (msg.what == 1111)
                {
                    e(TAG, " 수신 받음")

                    // 메시지 수신받기
                    e(TAG, "받은 메시지: " + msg.obj.toString())

                    msgFilter = msg.obj.toString().split("│".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    // 주문 감지
                    for (i in msgFilter!!.indices)
                    {
                        e(TAG, "msgFilter[$i]: " + msgFilter?.get(i)!!)
                        e(TAG, "msgFilter[$i]: " + msgFilter!![i])

                        // todo: 신규 주문 감지
                        if (msgFilter!![i].equals("newOrderRequest"))
                        {

                            // 주문 감지되면 리스트 새로고침
                            val message: Message = orderRequestHandler?.obtainMessage()!!
                            message.what = PROGRESS_CODE
                            message.arg1 = 7777

                            // 목록 새로고침 하기
                            orderRequestHandler!!.sendMessage(message)

                            // todo: 신규 주문 알림 notification
                            createNotificationChannel(this@nestService, NotificationManagerCompat.IMPORTANCE_HIGH,
                                    false, getString(com.example.nest_of_the_moon.R.string.app_name), "Nest Of The Moon") // 1 노티피케이션 채널을 만들지 않았다면 생성함

                            e(TAG, "")

                            val channelId = "$packageName-${getString(com.example.nest_of_the_moon.R.string.app_name)}" // 2 채널을 만들 때 사용하는 채널 ID. 노티를 등록할 때 필요함
                            val title = "신규 주문 알림"
                            val content = "'여기'를 클릭하여 새 주문을 확인하세요"

                            val intent = Intent(baseContext, Activity_Barista_Home::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            val pendingIntent = PendingIntent.getActivity(baseContext, 0,
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT)    // 3 노티를 터치했을 때 액티비티를 실행하기 위해 필요합니다.

                            val builder = NotificationCompat.Builder(this@nestService, channelId)  // 4 Notification을 만드는 Builder입니다. 여기에 channelId를 인자로 넣어야 합니다.
                            builder.setSmallIcon(com.example.nest_of_the_moon.R.drawable.logo_1)    // 5
                            builder.setContentTitle(title)    // 6
                            builder.setContentText(content)    // 7
                            builder.priority = NotificationCompat.PRIORITY_DEFAULT    // 8 노티의 중요도. 중요도에 따라서 노티가 안 보일 수도 있음
                            builder.setAutoCancel(true)   // 9 AutoCancel이 true이면 사용자가 노티를 터치했을 때 사라지게 합니다. false면 눌러도 사라지지 않습니다.
                            builder.setContentIntent(pendingIntent)   // 10 위에서 만든 PendingIntent를 노티에 등록합니다.

                            val notificationManager = NotificationManagerCompat.from(this@nestService)
                            notificationManager.notify(3333, builder.build())    // 11 NotificationManager.notify()으로 노티를 등록합니다. ID가 같으면 1개의 노티만 생성되고, 다르면 여러개의 노티가 생성됩니다. ID를 알면 그 ID로 등록된 노티를 코드로 취소할 수도 있습니다.
                            //

                            // todo: 시리얼 룸 입장하기
                            var waitingRoomNo = msgFilter!![1] // 시리얼 룸 번호
                            var loginUsertype = "Barista"
                            var loginUserId: String = Activity_Barista_Home.GET_ID.toString()
                            var orderRequest = "BaristaJoinSerialRoom" /*    or "newOrderRequest"    */

                            var waitingRoomAndUserData: String =
                                waitingRoomNo + "@" + loginUsertype + "@" + loginUserId + "@" + orderRequest
                            e(TAG, "onStartCommand: roomAndUserData: $waitingRoomAndUserData")

                            // 방번호와 유저의 이름으로 서버에 접속한다
                            client = TCP_Manager.SocketClient(waitingRoomAndUserData)
                            client?.start()

                            e(TAG, "시리얼 룸 입장함: " + msgFilter!![1])
                        }


                        // todo: '제작 시작' 알림 감지
                        if (msgFilter!![i].equals("orderStatus_Start"))
                        {
                            // todo: 제작 시작 알림 notification
                            createNotificationChannel(this@nestService, NotificationManagerCompat.IMPORTANCE_HIGH,
                                    false, getString(com.example.nest_of_the_moon.R.string.app_name), "Nest Of The Moon") // 1 노티피케이션 채널을 만들지 않았다면 생성함

                            val channelId = "$packageName-${getString(com.example.nest_of_the_moon.R.string.app_name)}" // 2 채널을 만들 때 사용하는 채널 ID. 노티를 등록할 때 필요함
                            val title = "제작을 시작했습니다(주문번호: "+ msgFilter!![1] +")"
                            val content = "고객님의 메뉴를 제작중입니다"

                            val intent = Intent(baseContext, Activity_Client_Home::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            val pendingIntent = PendingIntent.getActivity(baseContext, 0,
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT)    // 3 노티를 터치했을 때 액티비티를 실행하기 위해 필요합니다.

                            val builder = NotificationCompat.Builder(this@nestService, channelId)  // 4 Notification을 만드는 Builder입니다. 여기에 channelId를 인자로 넣어야 합니다.
                            builder.setSmallIcon(com.example.nest_of_the_moon.R.drawable.logo_1)    // 5
                            builder.setContentTitle(title)    // 6
                            builder.setContentText(content)    // 7
                            builder.priority = NotificationCompat.PRIORITY_DEFAULT    // 8 노티의 중요도. 중요도에 따라서 노티가 안 보일 수도 있음
                            builder.setAutoCancel(true)   // 9 AutoCancel이 true이면 사용자가 노티를 터치했을 때 사라지게 합니다. false면 눌러도 사라지지 않습니다.
                            builder.setContentIntent(pendingIntent)   // 10 위에서 만든 PendingIntent를 노티에 등록합니다.

                            val notificationManager = NotificationManagerCompat.from(this@nestService)
                            notificationManager.notify(22, builder.build())    // 11 NotificationManager.notify()으로 노티를 등록합니다. ID가 같으면 1개의 노티만 생성되고, 다르면 여러개의 노티가 생성됩니다. ID를 알면 그 ID로 등록된 노티를 코드로 취소할 수도 있습니다.

                            if (orderRequestHandler == null)
                            {
                                e(TAG, "액티비티 실행중 아님 닫혀있음")
                            }
                            else
                            {
                                // 주문 감지되면 리스트 새로고침
                                val message: Message = orderRequestHandler?.obtainMessage()!!
                                message.what = PROGRESS_CODE
                                message.arg1 = 8888

                                // 목록 새로고침 하기
                                orderRequestHandler!!.sendMessage(message)
                            }
                        }

                        // todo: '제작 완료' 알림 감지
                        if (msgFilter!![i].equals("orderStatus_End"))
                        {
                            // todo: 제작 완료 알림 notification
                            createNotificationChannel(this@nestService, NotificationManagerCompat.IMPORTANCE_HIGH,
                                    false, getString(com.example.nest_of_the_moon.R.string.app_name), "Nest Of The Moon") // 1 노티피케이션 채널을 만들지 않았다면 생성함

                            val channelId = "$packageName-${getString(com.example.nest_of_the_moon.R.string.app_name)}" // 2 채널을 만들 때 사용하는 채널 ID. 노티를 등록할 때 필요함
                            val title = "제작을 완료했습니다 (주문번호: D" + msgFilter!![1] + ")"

                            val content = "직원에게 주문 번호를 제시하고 상품을 픽업하세요"

                            val intent = Intent(baseContext, Activity_Client_Home::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            val pendingIntent = PendingIntent.getActivity(baseContext, 0,
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT)    // 3 노티를 터치했을 때 액티비티를 실행하기 위해 필요합니다.

                            val builder = NotificationCompat.Builder(this@nestService, channelId)  // 4 Notification을 만드는 Builder입니다. 여기에 channelId를 인자로 넣어야 합니다.
                            builder.setSmallIcon(com.example.nest_of_the_moon.R.drawable.logo_1)    // 5
                            builder.setContentTitle(title)    // 6
                            builder.setContentText(content)    // 7
                            builder.priority = NotificationCompat.PRIORITY_DEFAULT    // 8 노티의 중요도. 중요도에 따라서 노티가 안 보일 수도 있음
                            builder.setAutoCancel(true)   // 9 AutoCancel이 true이면 사용자가 노티를 터치했을 때 사라지게 합니다. false면 눌러도 사라지지 않습니다.
                            builder.setContentIntent(pendingIntent)   // 10 위에서 만든 PendingIntent를 노티에 등록합니다.

                            val notificationManager = NotificationManagerCompat.from(this@nestService)
                            notificationManager.notify(111, builder.build())    // 11 NotificationManager.notify()으로 노티를 등록합니다. ID가 같으면 1개의 노티만 생성되고, 다르면 여러개의 노티가 생성됩니다. ID를 알면 그 ID로 등록된 노티를 코드로 취소할 수도 있습니다.

                            if (orderRequestHandler == null)
                            {
                                e(TAG, "액티비티 실행중 아님 닫혀있음")
                            }
                            else
                            {
                                // 주문 감지되면 리스트 새로고침
                                val message: Message = orderRequestHandler?.obtainMessage()!!
                                message.what = PROGRESS_CODE
                                message.arg1 = 9999

                                // 목록 새로고침 하기
                                orderRequestHandler!!.sendMessage(message)
                            }
                        }
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel(context: Context,
                                          importance: Int,
                                          showBadge: Boolean,
                                          name: String,
                                          description: String)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate()
    {
        e(TAG, "서비스 onCreate()")

        super.onCreate()
    }

    override fun onDestroy()
    {
        super.onDestroy()
    }
}
