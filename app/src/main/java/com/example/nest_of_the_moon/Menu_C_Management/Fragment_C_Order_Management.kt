package com.example.nest_of_the_moon.Menu_C_Management


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

import com.example.nest_of_the_moon.R
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_C_Order_Management: Fragment()
{
    var mContext: Context? = null

    var menu_for_c_pager_tab: TabLayout? = null
    var menu_c_pager: ViewPager? = null

    companion object
    {
        var menu_c_management_cart: TextView? = null
        var menu_c_management_cart_button: LinearLayout? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_c_order_management, container, false)

        // todo: View Find
        menu_for_c_pager_tab = view.findViewById(R.id.menu_for_c_pager_tab)
        menu_c_pager = view.findViewById(R.id.menu_c_pager)
        menu_c_management_cart = view.findViewById(R.id.menu_c_management_cart)
        menu_c_management_cart_button = view.findViewById(R.id.menu_c_management_cart_button)

        // 장바구니로 이동하기
        menu_c_management_cart?.setOnClickListener(View.OnClickListener {

        })

        // 뷰페이저 연결 ( Main_Pager_Adapter 와 연결 )
        val fragmentAdapter = MenuForCPager(childFragmentManager)
        menu_c_pager?.adapter = fragmentAdapter

        // 탭레이아웃 연결
        menu_for_c_pager_tab?.setupWithViewPager(menu_c_pager)

        // 탭, 탭 제목 추가
        menu_for_c_pager_tab?.getTabAt(0)?.setText("음료")
        menu_for_c_pager_tab?.getTabAt(1)?.setText("원두")

        // 페이지 리스너
        menu_for_c_pager_tab?.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener
        {
            override fun onTabReselected(tab: TabLayout.Tab?)
            {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?)
            {
            }

            override fun onTabSelected(tab: TabLayout.Tab?)
            {
            }
        })

        return view
    }

    // 뷰페이저 어댑터
    inner class MenuForCPager: FragmentPagerAdapter
    {
        // 프래그먼트 연결
        var data1: Fragment = Fragment_Menu_ForC_Beverage()
        var data2: Fragment = Fragment_Menu_ForC_Beans()

        // 리스트에 프래그먼트 담기
        var mData: ArrayList<Fragment> = arrayListOf(data1, data2)

        constructor(fm: FragmentManager): super(fm)
        {

        }

        override fun getItem(position: Int): Fragment
        {
            return mData.get(position)
        }

        override fun getCount(): Int
        {
            return mData.size
        }
    }
}
