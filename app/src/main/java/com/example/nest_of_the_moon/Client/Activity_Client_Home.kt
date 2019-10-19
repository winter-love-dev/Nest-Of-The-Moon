package com.example.nest_of_the_moon.Client

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TabHost
import com.example.nest_of_the_moon.Community.Fragment_Community
import com.example.nest_of_the_moon.MyPage.Fragment_Mypage
import com.example.nest_of_the_moon.Menu_C_Management.Fragment_C_Order_Management
import com.example.nest_of_the_moon.Notification_Tab_Client.Fragment_Client_Noti
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.SessionManager
import kotlinx.android.synthetic.main.activity_client_main.*

class Activity_Client_Home : AppCompatActivity()
{
    var TAG: String = "Activity_Client_Home"

    companion object
    {
        var sessionManager: SessionManager? = null

        var GET_ID: String? = null
        var GET_NAME: String? = null
        var GET_TYPE: String? = null
    }

    var TabPsotion: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_main)
        Log.e(TAG, "실행됨")

        // 세션 선언
        sessionManager = SessionManager(this)

        var user = sessionManager!!.userDetail
        GET_ID = user.get(SessionManager.ID)
        GET_NAME = user.get(SessionManager.NAME)
        GET_TYPE = user.get(SessionManager.TYPE)

        Log.e(TAG, "접속한 유저 정보 불러오기")
        Log.e(TAG, "GET_ID: $GET_ID")
        Log.e(TAG, "GET_NAME: $GET_NAME")
        Log.e(TAG, "GET_TYPE: $GET_TYPE")

        // todo: 탭호스트 설정하기
        tabhost.setup(this, supportFragmentManager, R.id.content)

        // todo: 커뮤니티
        val tab01 = ImageView(this)
        tab01.setImageResource(R.drawable.tab_c_01)

        // 커뮤니티 탭 연결
        val tabSpec1 = tabhost.newTabSpec("커뮤니티") // 구분자
        tabSpec1.setIndicator(tab01)
        tabhost.addTab(tabSpec1, Fragment_Community::class.java!!, null)

        // todo: 오더 탭
        val tab02 = ImageView(this)
        tab02.setImageResource(R.drawable.tab_c_02)

        // 오더 탭 연결
        val tabSpec2 = tabhost.newTabSpec("맞춤")
        tabSpec2.setIndicator(tab02)
        tabhost.addTab(tabSpec2, Fragment_C_Order_Management::class.java!!, null)

        // todo: 마이페이지
        val tab04 = ImageView(this)
        tab04.setImageResource(R.drawable.tab_c_04)

        // 마이페이지 탭 연결
        val tabSpec4 = tabhost.newTabSpec("알림")
        tabSpec4.setIndicator(tab04)
        tabhost.addTab(tabSpec4, Fragment_Client_Noti::class.java!!, null)

        // todo: 마이페이지
        val tab03 = ImageView(this)
        tab03.setImageResource(R.drawable.tab_c_03)

        // 마이페이지 탭 연결
        val tabSpec3 = tabhost.newTabSpec("인증")
        tabSpec3.setIndicator(tab03)
        tabhost.addTab(tabSpec3, Fragment_Mypage::class.java!!, null)

        TabPsotion = tabhost.getCurrentTab()
        Log.e(TAG, "onTabChanged: TabPsotion: $TabPsotion")

        // 이미지 간격 줄이기
        var padding: Int = 20
        for (i in 0 until tabhost.getTabWidget().getChildCount())
        {
            tabhost.getTabWidget().getChildAt(i).setPadding(padding, padding, padding, padding)
        }

        // 탭 배경색
        tabhost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#FFFFFF"))
        tabhost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#FFFFFF"))
        tabhost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#FFFFFF"))
        tabhost.getTabWidget().getChildAt(3).setBackgroundColor(Color.parseColor("#FFFFFF"))

        // 고객 화면의 기본 탭 위치는 '커뮤니티'로 지정한다
        tabhost.setCurrentTab(0)

        tabhost.setOnTabChangedListener(TabHost.OnTabChangeListener {
            // 탭 변경시 리스너
            TabPsotion = tabhost.getCurrentTab()
            Log.e(TAG, "onTabChanged: TabPsotion: $TabPsotion")
        })
    }

    // 프래그먼트 뒤로가기. 0번 포지션에 있을때 뒤로가기 눌렀을때만 앱 종료하기
    override fun onBackPressed()
    {
        if (TabPsotion != 0)
        {
            Log.e(TAG, "onBackPressed: 0번 포지션으로 이동합니다")
            tabhost.setCurrentTab(0)
        }
        else if (TabPsotion == 0)
        {
            Log.e(TAG, "onBackPressed: 앱을 종료합니다")
            finish()
        }
    }
}
