package com.example.nest_of_the_moon.Community


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.nest_of_the_moon.Client_ChatBot.Activity_Chat_Bot
import com.example.nest_of_the_moon.OnBackPressedListener

import com.example.nest_of_the_moon.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 */

class Fragment_Community : Fragment()
{

    var button_chat_bot_room: LinearLayout? = null
    var mContext_Fragment_Community: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                             ): View?

    {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_community, container, false)

        mContext_Fragment_Community = activity

        button_chat_bot_room = view.findViewById(R.id.button_chat_bot_room)
        button_chat_bot_room!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(mContext_Fragment_Community, Activity_Chat_Bot::class.java))
        })

        return view
    }
}
