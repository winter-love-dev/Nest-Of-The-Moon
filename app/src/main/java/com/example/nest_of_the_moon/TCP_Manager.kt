package com.example.nest_of_the_moon

import android.os.Handler
import android.util.Log
import android.util.Log.e
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class TCP_Manager
{
    var TAG = "TCP_Manager"

    companion object
    {
        // todo: 소켓 통신 세팅
        var msgHandler: Handler? = null

        var msgFilter: Array<String>? = null

        var socket: Socket? = null
        var client: SocketClient? = null

//        var ipad2 = "192.168.0.219" // 5사
        var ipad2 = "192.168.0.83" // 3사_5g

        var port = 12347

//        var send: SendThread? = null
        var send: SendThreadd? = null
        var recevie: ReceiveThread? = null

        var TCPSendUerID: String? = null

        var RoomNo: String? = null
        var UserType: String? = null
        /*
                RoomNo
                UserType
        */
    }

    // todo: 접속 스레드
    class SocketClient(var roomAndUserData: String): Thread() // 방 정보 ( 방번호 /  접속자 아이디 )): Thread()
    {
        var TAG = "TCP_Manager: SocketClient"

        var input: DataInputStream? = null
        var output: DataOutputStream? = null

        override fun run()
        {
            try
            {
                // 채팅 서버에 접속 ( 연결 )  ( 서버쪽 ip와 포트 )
                socket = Socket(ipad2/*서버 ip*/, port/*포트*/)

                e(TAG, "SocketClient run: 접속 시도: " + socket)

                // 메세지를 서버에 전달 할 수 있는 통로 ( 만들기 )
                output = DataOutputStream(socket?.getOutputStream())
                input = DataInputStream(socket?.getInputStream())

                output!!.writeUTF(roomAndUserData)

                // (메세지 수신용 쓰레드 생성 ) 리시브 쓰레드 시작
                recevie = ReceiveThread(socket!!)
                recevie?.start()
            }

            catch (e: Exception)
            {
                e.printStackTrace()
                e(TAG, "e.printStackTrace: " + e.printStackTrace().toString())
            }
        }
    } //SocketClient의 끝

    // todo: 발신 스레드
    class SendThreadd: Thread
    {
        var socket: Socket // ip와 포트?
        var sendmsg: String? = null
        var output: DataOutputStream? = null

        var TAG = "TCP_Manager: SendThreadd"

        constructor(socket: Socket, sendmsg: String)
        {

            this.socket = socket
            this.sendmsg = sendmsg
            try
            {
                // 채팅 서버로 메세지를 보내기 위한  스트림 생성.
                output = DataOutputStream(socket.getOutputStream())
                e(TAG, "constructor: output: " + output)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }

        // 서버로 메세지 전송 ( 이클립스 서버단에서 temp 로 전달이 된다.
        override fun run()
        {
            e(TAG, "run: SendThread: 실행됨")
            try
            {
                if (output != null)
                {
                    e(TAG, "run: output: " + output)
                    e(TAG, "run: sendmsg: " + sendmsg)

                    if (sendmsg != null)
                    {
                        // int roomNo = 1;
                        // 여기서 방번호와 상대방 아이디 까지 해서 보내줘야 할거같다 .
                        // 서버로 메세지 전송하는 부분

                        // 방 번호 @ 유저 인덱스 @ 유저 이름 @ 메시지
                        //                        output.writeUTF(roomNo + "@" + targetID + "@" + targetNickName + "@" + sendmsg);

                        //                         방 번호 @ 내 아이디 @ 상대 아이디 @ 메시지
                        //                        output.writeUTF(RoomNo + "@" + loginUserId + "@" + targetID + "@" + sendmsg + "@" + Type);
                        output?.writeUTF(RoomNo + "│" + UserType + "│" + TCPSendUerID + "│" + sendmsg)
                        e(TAG, "TCPSendUerID: $TCPSendUerID")
                        e(TAG, "output: " + output)
                        e(TAG, "전송완료")

                        sendmsg = null

                        // 리사이클러뷰의 아이템 위치를 아래로 내리기
                        //                        message_area.scrollToPosition(messageData.size() - 1);
                    }
                }

            }
            catch (e: Exception)
            {
                e.printStackTrace()
                e(TAG, "e: " + e.printStackTrace().toString())
            }

        }
    }

    // todo: 수신 스레드
    class ReceiveThread(socket: Socket): Thread()
    {
        var TAG = "TCP_Manager: ReceiveThread"
        var input: DataInputStream? = null

        init
        {

            try
            {
                // 채팅 서버로부터 메세지를 받기 위한 스트림 생성.
                input = DataInputStream(socket.getInputStream())
                e(TAG, "ReceiveThread try input: 연결됨 ")
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }

        override fun run()
        {
            try
            {
                while (input != null)
                {
                    // 채팅 서버로 부터 받은 메세지
                    val msg = input!!.readUTF()
                    e(TAG, "ReceiveThread: run(): String msg = null?: " + msg!!)

                    if (msg != null)
                    {
                        e(TAG, "ReceiveThread: run() = input.readUTF(): $msg")

                        // 핸들러에게 전달할 메세지 객체
                        val hdmg = msgHandler!!.obtainMessage()

                        e(TAG, "ReceiveThread: hdmg = msgHandler.obtainMessage(): $hdmg")

                        // 핸들러에게 전달할 메세지의 식별자
                        hdmg.what = 1111

                        // 메세지의 본문
                        hdmg.obj = msg

                        // 핸들러에게 메세지 전달 ( 화면 처리 )
                        msgHandler!!.sendMessage(hdmg)

                        // 방 목록 갱신하기

                        e(TAG, "ReceiveThread: run()에서 onCreate의 핸들러로 메시지 전달함 ")

                        // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
                        //                        message_area.smoothScrollToPosition(messageData.size());
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                e(TAG, "run: Exception e: $e")
                //                Toast.makeText(Activity_ChatRoom.this, "Exception e: " + e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

/*    class ReceiveThread(socket: Socket): Thread()
    {
        var input: DataInputStream? = null
        //        var socket: Socket? = null
        var TAG = "TCP_Manager: ReceiveThread"

        init
        {
            try
            {
                // 채팅 서버로부터 메세지를 받기 위한 스트림 생성.
                input = DataInputStream(socket.getInputStream())
                Log.e(TAG, "ReceiveThread try input: 연결됨 ")
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }

        override fun run()
        {
            try
            {
                while (input != null)
                {
                    // 채팅 서버로 부터 받은 메세지
                    val msg = input!!.readUTF()
                    Log.e(TAG, "ReceiveThread: run(): String msg = null?: " + msg!!)

                    if (msg != null)
                    {
                        Log.e(TAG, "ReceiveThread: run() = input.readUTF(): $msg")

                        // 핸들러에게 전달할 메세지 객체
                        val hdmg = msgHandler?.obtainMessage()

                        Log.e(TAG, "ReceiveThread: hdmg = msgHandler.obtainMessage(): $hdmg")

                        // 핸들러에게 전달할 메세지의 식별자
                        hdmg?.what = 1111

                        // 메세지의 본문
                        hdmg?.obj = msg

                        // 핸들러에게 메세지 전달 ( 화면 처리 )
                        msgHandler?.sendMessage(hdmg)

                        // 방 목록 갱신하기
                        //                        ChatRoomList_Request();

                        Log.e(TAG, "ReceiveThread: run()에서 onCreate의 핸들러로 메시지 전달함 ")

                        // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
                        //                        message_area.smoothScrollToPosition(messageData.size());
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                Log.e(TAG, "run: Exception e: $e")
            }

        }
    }*/

}