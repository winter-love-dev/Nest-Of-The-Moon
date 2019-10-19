package com.example.nest_of_the_moon.MyPage


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.nest_of_the_moon.OnBackPressedListener

import com.example.nest_of_the_moon.R
import com.example.nest_of_the_moon.SessionManager
import kotlinx.android.synthetic.main.fragment_mypage.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment_Mypage : Fragment()
{

    var sessionManager: SessionManager? = null
    var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                             ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        mContext = mContext?.applicationContext

        // 세션 선언
        sessionManager = SessionManager(requireContext())

        view.button_logout.setOnClickListener(View.OnClickListener {
            sessionManager?.logout()
            activity?.finish()
        })

        return view
    }
}
