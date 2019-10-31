package com.example.nest_of_the_moon.Barista

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TabHost
import com.example.nest_of_the_moon.Community.Fragment_Community
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management
import com.example.nest_of_the_moon.MyPage.Fragment_Mypage
import com.example.nest_of_the_moon.Order_B_Menagement.Fragment_B_Order_Management
import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.SessionManager
import kotlinx.android.synthetic.main.activity_client_main.*

class Activity_Barista_Home : AppCompatActivity()
{
    companion object
    {
        var GET_ID: String? = null
        var GET_NAME: String? = null
        var GET_TYPE: String? = null
    }

    var TAG: String = "Activity_Client_Home"

    var sessionManager: SessionManager? = null

    var TabPsotion: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__barista__home)

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

        // todo: 1. 주문관리
        val tab01 = ImageView(this)
        tab01.setImageResource(R.drawable.tab_b_01)

        // 주문 관리
        val tabSpec1 = tabhost.newTabSpec("주문관리") // 구분자
        tabSpec1.setIndicator(tab01)
        tabhost.addTab(tabSpec1, Fragment_B_Order_Management::class.java!!, null)

        // todo: 2. 메뉴 관리
        val tab02 = ImageView(this)
        tab02.setImageResource(R.drawable.tab_b_02)

        // 메뉴 관리
        val tabSpec2 = tabhost.newTabSpec("매뉴관리")
        tabSpec2.setIndicator(tab02)
        tabhost.addTab(tabSpec2, Fragment_Menu_Management::class.java!!, null)

        // todo: 3. 커뮤니티
        val tab03 = ImageView(this)
        tab03.setImageResource(R.drawable.tab_b_03)

        // 커뮤니티
        val tabSpec3 = tabhost.newTabSpec("커뮤니티")
        tabSpec3.setIndicator(tab03)
        tabhost.addTab(tabSpec3, Fragment_Community::class.java!!, null)

        // todo: 4. 마이페이지
        val tab04 = ImageView(this)
        tab04.setImageResource(R.drawable.tab_b_04)

        // 마이페이지
        val tabSpec4 = tabhost.newTabSpec("마이페이지")
        tabSpec4.setIndicator(tab04)
        tabhost.addTab(tabSpec4, Fragment_Mypage::class.java!!, null)

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

        // 바리스타 화면의 기본 탭 위치는 '주문 관리'로 지정한다
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
