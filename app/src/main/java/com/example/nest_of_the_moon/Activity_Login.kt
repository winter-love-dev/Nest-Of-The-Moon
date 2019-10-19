package com.example.nest_of_the_moon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nest_of_the_moon.Barista.Activity_Barista_Home
import com.example.nest_of_the_moon.Client.Activity_Client_Home
import kotlinx.android.synthetic.main.activity__login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Activity_Login : AppCompatActivity()
{
    private var TAG: String = "Activity_Login"

//    // 회원정보 입력칸
//    private var email: EditText? = null
//    private var password: EditText? = null
//
//    // 로그인 버튼
//    private var button_login: Button? = null
//
//    // 가입버튼
//    private var link_regist: TextView? = null
//
//    // 로딩바
//    private var loading: ProgressBar? = null

    // 로그인 PHP의 주소를 담을 변수 선언
    // private static String URL_LOGIN = "http://34.73.32.3/login.php"; // gcp
    private val URL_LOGIN = "http://115.68.231.84/login.php" //iwinv

    // 세션 담을 변수 선언
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__login)
        Log.e(TAG, "실행됨")

        // 세션 시작.
        // 1. 로그인 메소드에서 유저의 이름, 이메일, 유저번호를 담아둔다.
        // 2. 세션에 유저 정보가 담겨있으면 자동 로그인 후 홈화면으로 이동.
        // 3. 세션에 담긴 값을 초기화 하면 다시 로그인 필요함.
        // 4. 로그인 = 1번 프로세스부터 시작함.
        sessionManager = SessionManager(this)

        // 로그인 버튼
        loggin_button?.setOnClickListener(View.OnClickListener {
            // 이메일과 패스워드 입력값 받아오기
            val mEmail = login_mail?.getText().toString().trim { it <= ' ' }
            val mPass = login_password?.getText().toString().trim { it <= ' ' }

            Log.e(TAG, "onResponse: button_login mEmail: = $mEmail" + ", mPass: $mPass" )

            // 이메일 빈 칸 체크
            if (TextUtils.isEmpty(mEmail))
            // 이메일 입력칸이 비어있으면 경고하기
            { // 로그인 실패
                val anim =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.alpha) // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                loggin_button?.startAnimation(anim) // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(this@Activity_Login, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show() // 토스트 메시지를 띄우고
                login_mail?.requestFocus() // 해당 EditText로 커서를 포커스 한다.
                return@OnClickListener  // 포커스 하러 돌아가! return 안하면 제기능 못 함
            }

            // 패스워드 빈 칸 체크
            if (TextUtils.isEmpty(mPass))
            // 패스워드 입력칸이 비어있으면 경고하기
            { // 로그인 실패
                val anim =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.alpha) // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                loggin_button?.startAnimation(anim)  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(this@Activity_Login, "패스워드를 입력하세요!", Toast.LENGTH_SHORT).show() // 토스트 메시지를 띄우고
                login_password?.requestFocus() // 해당 EditText로 커서를 포커스 한다.
                return@OnClickListener  // return 안하면 제기능 못 함
            }

            // 이메일과 패스워드를 모두 입력 받으면
            // 로그인 메소드 실행하기
            if (!mEmail.isEmpty() || !mPass.isEmpty())
            {
                // 로그인 메소드는 onCreate 바로 아래 위치에 작성됨.
                Login(mEmail, mPass)
            }
            else
            {
                // 이메일이나 패스워드가 입력되지 않으면 에러로 간주하기
                login_mail?.setError("이메일을 입력해주세요")
                login_password?.setError("패스워드를 입력해주세요")
                Toast.makeText(this@Activity_Login, "문제 발생.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

                // 로딩바 비활성
                login_loading?.setVisibility(View.GONE)

                // 로그인 재시도를 위해 로그인 버튼 활성화
                login_password?.setVisibility(View.VISIBLE)
            }
        })

        // 회원가입 액티비티로 이동하기
        go_reg?.setOnClickListener(View.OnClickListener {
            val MOVE_REG_ACTIVITY = Intent(this@Activity_Login, Activity_Register::class.java)
            startActivity(MOVE_REG_ACTIVITY)
        })
    }

    // 로그인 메소드
    private fun Login(email: String, password: String)
    {
        Log.e(TAG, "onResponse: Login()실행됨" )

        // 로그인 버튼을 누르면 프로그레스 다이얼로그를 활성화 하기.
        login_loading?.setVisibility(View.VISIBLE)

        // 로그인 버튼을 누르면 로그인 버튼을 비활성화 하기.
        loggin_button?.setVisibility(View.GONE)

        // StringRequest 1. volley로 로그인 결과를 요청할 때 필요한 요소들을 담아두는 메소드. / 2. 요청한 결과를 받음.
        // 필요한 요소 _ 1. 요청 메소드 선택(POST, GET) / 2. 요청보낼 주소 입력
        // 입력값을 POST로 전송할 주소를 담아둠.
        // StringRequest에 담긴 정보들을 RequestQueue 메소드 담는다.
        val stringRequest = object : StringRequest(Request.Method.POST, URL_LOGIN, Response.Listener { response ->
            // onResponse = 요청 결과를 받는 메소드
            // 요청한 결과를 response에 담는다.
            try
            // JSON 형태로 결과 요청을 받는다.
            {
                Log.e(TAG, "onResponse: Login = $response")
                // json = 프로그램 언어간 데이터 전달방식 통용화.
                // or 서버에서 클라이언트에게 값을 전달하는 수단으로 사용됨.

                // login.php에서 JSON 형태로 인코딩 된 로그인 결과값을 전송받는다.
                // 결과값은 response에 JSON 형태로 담겨있다.
                // respnse에 받을 json 결과값은 아래 참고
                // {
                // "login": [{"name":"\uae40\uc131\ud6c8","email":"aa@aa.com","id":"1"}],"success":"1","message":"success"}

                // JSONObject = json에 담긴 값에서 필요한 문자열을 추출하기 위해 필요한 메소드
                val jsonObject = JSONObject(response)

                // json에 담긴 문자열 중 success를 추출한다.
                val success = jsonObject.getString("success") // = 1
                Log.e(TAG, "onResponse: jsonObject.getString success = $success")

                // 키값이 login인 json에 담긴 값들을 배열에 담는다.
                // = [{"name":"김성훈","email":"aa@aa.com","id":"1"}]
                // JSONArray로 json 값을들 배열로 정리하는 건데.
                // JSONArray로 정리된 배열값은 다음 세 사가지다. [김성훈], [aa@aa.com]. [유저번호]
                val jsonArray = jsonObject.getJSONArray("login")
                Log.e(TAG, "onResponse: jsonObject.getJSONArray login = $jsonArray")

                // "success":"1","message":"success"
                // JSON 결과값 중 success:1 이 포함되어 있는 JSON이 있으면 아래 조건문을 실행한다.
                if (success == "1")
                {
                    // JSON 배열에 담긴 값들을 반복문으로 모두 가져온다.
                    for (i in 0 until jsonArray.length())
                    {
                        val `object` = jsonArray.getJSONObject(i)

                        // JSON 배열에 담긴 값들을 아래 문자열로 추출한다.
                        val name = `object`.getString("name").trim { it <= ' ' } // 김성훈
                        val email = `object`.getString("email").trim { it <= ' ' } // aa@aa.com
                        val id = `object`.getString("id").trim { it <= ' ' } // 유저번호
                        val type = `object`.getString("type").trim { it <= ' ' } // 유저 타입 구분하기(1: 클라이언트, 2: 바리스타)

                        Log.e(TAG, "type: " + type)

                        // todo: 클라이언트 계정으로 로그인
                        if (type == "1")
                        {
                            // 세션에 이름, 이메일, 유저번호 값을 담아서
                            // 세션 매니저 클래스의 createSession 메소드로 전달한다.
                            sessionManager?.createSession(name, email, id, type, "lately")

                            // 로그인 액티비티에서 홈 액티비티로 이동하기.
                            val intent = Intent(this@Activity_Login, Activity_Client_Home::class.java)

                            // 이동할 때 name과 email 값 담아서 홈 액티비티에 보내기
                            intent.putExtra("name", name)
                            intent.putExtra("email", email)
                            startActivity(intent)

                            // 로그인 완료 후 로딩 비활성화
                            login_loading?.setVisibility(View.GONE)
                            Toast.makeText(this@Activity_Login, "클라이언트 계정으로 접속하셨습니다", Toast.LENGTH_SHORT).show()

                            // 액티비티 종료
                            finish()
                        }

                        // todo: 바리스타 계정으로 로그인
                        else
                        {
                            // 세션에 이름, 이메일, 유저번호 값을 담아서
                            // 세션 매니저 클래스의 createSession 메소드로 전달한다.
                            sessionManager?.createSession(name, email, id, type, "lately")

                            // 로그인 액티비티에서 홈 액티비티로 이동하기.
                            val intent = Intent(this@Activity_Login, Activity_Barista_Home::class.java)

                            // 이동할 때 name과 email 값 담아서 홈 액티비티에 보내기
                            intent.putExtra("name", name)
                            intent.putExtra("email", email)
                            startActivity(intent)

                            // 로그인 완료 후 로딩 비활성화
                            login_loading?.setVisibility(View.GONE)
                            Toast.makeText(this@Activity_Login, "바리스타 계정으로 접속하셨습니다", Toast.LENGTH_SHORT).show()

                            // 액티비티 종료
                            finish()
                        }
                    }
                }
                else
                {
                    Toast.makeText(this@Activity_Login, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    login_loading?.setVisibility(View.GONE)
                    loggin_button?.setVisibility(View.VISIBLE)
                }
            }
            catch (e: JSONException)
            {
                // json 결과값에 아무것도 담겨있지 않거나
                // success에 포함된 결과값이 0이면 로그인 실패로 간주함.
                e.printStackTrace()

                // 로딩 비활성화 후 로그인 버튼 다시 활성화
                login_loading?.setVisibility(View.GONE)
                loggin_button?.setVisibility(View.VISIBLE)

                // 로그인 실패결과 알림.
                Toast.makeText(this@Activity_Login, "문제발생\n 다시 시도해주세요\n$e", Toast.LENGTH_SHORT).show()

                // 문제 확인용 로그 기록
                Log.e("JSONException e", "에러: LOGIN = $e")
            }
        },
                // 요청 결과를 못 받았거나 다른 문제 발생시
                Response.ErrorListener { error ->
                    // 로딩 비활성화 후 로그인 버튼 다시 활성화
                    login_loading?.setVisibility(View.GONE)
                    loggin_button?.setVisibility(View.VISIBLE)

                    // 로그인 실패결과 알림.
                    Toast.makeText(this@Activity_Login, "문제발생\n 다시 시도해주세요\n$error", Toast.LENGTH_SHORT).show()

                    // 문제 확인용 로그 기록
                    Log.e("VolleyError", "에러: $error")
                })
        {

            // 맵의 key와 value를 문자열로 구성한다.
            // 이메일과 패스워드를 Map으로 저장한다.
            // 다음은 인터넷 설명. 뭔 소린지 잘 모르겠다.
            /*
             * 서버에 전송할 데이터는 StringRequest를 상속받은 클래스를 정의하고,
             * 이 클래스에서 getParams( ) 함수를 재정의하며 getParams ( ) 함수에서
             * 서버에 전송할 데이터를 Map 객체에 담아 반환하면 됩니다.
             * 이렇게 하면 getParams( ) 함수에서 반환한 Map 객체의 데이터를 웹의 질의 문자열 형식으로 만들어
             * RequestQueue에서 서버 요청 시 서버에 전송합니다.
             * */
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        // requestQueue로 로그인 결과값 요청을 시작한다.
        val requestQueue = Volley.newRequestQueue(this)

        // stringRequest메소드에 기록한 내용들로 requestQueue를 시작한다.
        requestQueue.add(stringRequest)
    }
}
