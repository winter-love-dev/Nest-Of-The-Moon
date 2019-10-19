package com.example.nest_of_the_moon.Menu_B_Management

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.nest_of_the_moon.ApiClient
import com.example.nest_of_the_moon.ApiInterface
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_INDEX
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_NAME
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_PICK_UP_RECOMMEND_TIME
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_PRICE
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_PRODUCE
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_SERVING_IMPOSSIBLE_TIME
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_SERVING_SIZE
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_SERVING_WAY
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_TYPE
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.GET_MENU_EDIT_Thumb
import com.example.nest_of_the_moon.Menu_B_Management.Fragment_Menu_Management.Companion.SortRequest
import com.example.nest_of_the_moon.R
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create_menu.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.HashMap

class Activity_Create_Menu: AppCompatActivity()
{
    var TAG: String = "Activity_Create_Menu"

    var RequestURL: String? = null

    // todo: set variable

    var ProductType: String? = null // 상품 타입

    var MenuName: String? = null       // 상품명
    var MenuServingWay: String? = null // 제공 방법

    var MenuPrice: String? = null   // 가격
    var MenuProduct: String? = null // 상품 소개

    var bitmap: Bitmap? = null      // 썸네일 담을 비트맵
    var MenuThumb: String? = null   // 문자열로 바꾼 썸네일을 담을 비트맵

    var MenuServingSize: String? = null            // 1회 제공량
    var MenuPickUpRecommendTime: String? = null    // 픽업 추천시간
    var MenuServingImpossibleTime: String? = null  // 폐기시간 안내

    var PhotoType: String? = null

    // 1. 가나다순
    // 2. 음료만
    // 3. 원두만
    // 4. 검색

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_menu)

        // todo: Edit Listener
        if (Fragment_Menu_Management.intoTheActivityCreateMode.equals("Edit"))
        {
            create_menu_serving_noti.visibility = View.GONE

            create_menu_title.setText("상품 정보를 수정합니다")

            // 상품명
            create_menu_name.setText(GET_MENU_EDIT_NAME)

            // 가격
            create_menu_price.setText(GET_MENU_EDIT_PRICE)

            // 상품 소개
            create_menu_product.setText(GET_MENU_EDIT_PRODUCE)

            // 픽업 추천시간
            create_menu_pick_up_recommend_time.setText(GET_MENU_EDIT_PICK_UP_RECOMMEND_TIME)

            // 폐기 시간 안내
            create_menu_serving_impossible_time.setText(GET_MENU_EDIT_SERVING_IMPOSSIBLE_TIME)

            // 썸네일 이미지
            Picasso.get().load(GET_MENU_EDIT_Thumb).placeholder(R.drawable.logo_1) // 로드되지 않은 경우 기본 이미지
                //.resize(300,300)       // 300x300 픽셀
                .fit()                   // 이미지 늘림 없이 ImageView에 맞춤
                .into(create_menu_thumb) // ImageView에 로드

            // 수정 중 사진을 새로 선택하면 PhotoType이 BitMap으로 바뀜.
            // 사진을 선택하지 않으면 PhotoType을 URL로 유지
            PhotoType = "URL"

            // 수정 버튼
            create_menu_done.setText("수정 완료")
            create_menu_done.setOnClickListener(View.OnClickListener
            {
                RequestURL = "http://115.68.231.84/EditCoffeeMenu.php"

                // todo: 서버로 수정 요청 보내기
                RequestCreateOrEdit()
            })
        }
        else
        {
            // 작성 완료 버튼
            create_menu_done.setOnClickListener(View.OnClickListener {
                RequestURL = "http://115.68.231.84/addCoffeeMenu.php"

                // todo: 서버로 신규 메뉴 생성 요청 보내기
                RequestCreateOrEdit()
            })
        }

        // todo: Radio Event

        // 상품 유형 선택
        val onCheckedChangeListener = OnCheckedChangeListener { group, checkedId ->

            // 상품 유형: 음료
            if (checkedId == R.id.radio_button_1_1)
            {
                ProductType = "Beverage" // 마실 것

                create_menu_serving_way.visibility = View.VISIBLE

                create_menu_serving_size_type.setText("ml")

                // todo: 마실 것 선택했을 때 활성화 할 뷰: create_menu_container_pick_up_guide
                create_menu_container_pick_up_guide.visibility = View.VISIBLE
            }

            // 상품 유형: 원두
            else if (checkedId == R.id.radio_button_1_2)
            {
                ProductType = "Beans" // 원두

                create_menu_serving_way.visibility = View.GONE

                create_menu_serving_size_type.setText("g")

                // todo: 원두 선택했을 때 비활성화 할 뷰: create_menu_container_pick_up_guide
                create_menu_container_pick_up_guide.visibility = View.GONE
            }
        }

        // 라디오 그룹 활성화: 상품 유형 선택
        radioGroup1.setOnCheckedChangeListener(onCheckedChangeListener)

        // 1회 제공량 입력
        val onCheckedChangeListener2 = OnCheckedChangeListener { group, checkedId ->

            // 입력 안 함
            if (checkedId == R.id.radio_button_2_1)
            {
                MenuServingSize = "none"
                create_menu_serving_size_container.visibility = View.GONE
            }

            // 입력함. 1회 제공량 입력창 활성화
            else if (checkedId == R.id.radio_button_2_2)
            {
                create_menu_serving_size_container.visibility = View.VISIBLE
            }
        }

        // 라디오 그룹 활성화: 1회 제공량 입력
        radioGroup2.setOnCheckedChangeListener(onCheckedChangeListener2)

        // 상품 유형 선택
        val onCheckedChangeListener3 = OnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.radio_button_3_1)
            {
                MenuServingWay = "HOT / ICE"
            }

            else if (checkedId == R.id.radio_button_3_2)
            {
                MenuServingWay = "HOT"
            }

            else if (checkedId == R.id.radio_button_3_3)
            {
                MenuServingWay = "ICE"
            }
        }

        // 라디오 그룹 활성화: 1회 제공량 입력
        radioGroup3.setOnCheckedChangeListener(onCheckedChangeListener3)

        // 라디오그룹 디폴트 설정
        if (Fragment_Menu_Management.intoTheActivityCreateMode.equals("Edit"))
        {
            Log.e(TAG, "GET_MENU_EDIT_TYPE: $GET_MENU_EDIT_TYPE" )
            Log.e(TAG, "GET_MENU_EDIT_SERVING_SIZE: $GET_MENU_EDIT_SERVING_SIZE" )
            Log.e(TAG, "GET_MENU_EDIT_SERVING_WAY: $GET_MENU_EDIT_SERVING_WAY" )

            // 상품 유형
            if (GET_MENU_EDIT_TYPE.equals("Beverage")) // or Beans
            {
                // 라디오그룹 디폴트 선택
                radioGroup1.check(radio_button_1_1.id)
            }
            else
            {
                // 라디오그룹 디폴트 선택
                radioGroup1.check(radio_button_1_2.id)
            }

            // 1회 제공량
            if (GET_MENU_EDIT_SERVING_SIZE.equals("none"))
            {
                // 라디오그룹 디폴트 선택
                radioGroup2.check(radio_button_2_1.id)
            }
            else
            {
                // 라디오그룹 디폴트 선택
                radioGroup2.check(radio_button_2_2.id)

                // 1회 제공량
                create_menu_serving_size.setText(GET_MENU_EDIT_SERVING_SIZE)
            }

            // 음료 제공 방법
            if (GET_MENU_EDIT_SERVING_WAY.equals("HOT / ICE"))
            {
                // 라디오그룹 디폴트 선택
                radioGroup3.check(radio_button_3_1.id)
            }

            else if(GET_MENU_EDIT_SERVING_WAY.equals("HOT"))
            {
                // 라디오그룹 디폴트 선택
                radioGroup3.check(radio_button_3_2.id)
            }

            else if(GET_MENU_EDIT_SERVING_WAY.equals("ICE"))
            {
                // 라디오그룹 디폴트 선택
                radioGroup3.check(radio_button_3_3.id)
            }

        }
        else
        {
            // 라디오그룹 디폴트 선택
            radioGroup1.check(radio_button_1_1.id)

            // 라디오그룹 디폴트 선택
            radioGroup2.check(radio_button_2_1.id)

            // 라디오그룹 디폴트 선택
            radioGroup3.check(radio_button_3_1.id)
        }

        // todo: Click Event

        // 이미지 선택 버튼
        create_menu_thumb.setOnClickListener(View.OnClickListener {
            CropImage.activity() // 크롭하기 위한 이미지를 가져온다.
                .setGuidelines(CropImageView.Guidelines.ON) // 이미지를 크롭하기 위한 도구 ,Guidelines를
                .setAspectRatio(1, 1) // 수직, 수평 비율 설정 (1:1 비율로)
                .start(this@Activity_Create_Menu) // 실행한다.
        })
    }

    // todo: 서버로 신규 메뉴 생성 및 수정 요청 보내기
    private fun RequestCreateOrEdit()
    {
        // todo: Get Text
        // 상품명
        MenuName = create_menu_name.text.toString()

        // 가격
        MenuPrice = create_menu_price.text.toString()

        // 상품 소개
        MenuProduct = create_menu_product.text.toString()

        // 픽업 추천시간
        MenuPickUpRecommendTime = create_menu_pick_up_recommend_time.text.toString()

        // 폐기 시간 안내
        MenuServingImpossibleTime = create_menu_serving_impossible_time.text.toString()

        // 1회 제공량
        MenuServingSize = create_menu_serving_size.text.toString()

        // 상품이 원두일시 음료 제공 방법 표시하지 않음
        if (ProductType.equals("Beans"))
        {
            MenuServingWay = "none"
            MenuPickUpRecommendTime = "none"
            MenuServingImpossibleTime = "none"
        }

        if (MenuServingSize!!.length == 0 && MenuPickUpRecommendTime?.length == 0 && MenuServingImpossibleTime?.length == 0)
        {
            MenuServingSize = "none"
        }

        // 음료 제공 방법
        if (MenuServingWay!!.length == 0 && MenuServingWay?.length == 0 && MenuServingWay?.length == 0)
        {
            MenuServingWay = "HOT / ICE"
        }

        // 문자열로 변환한 비트맵
        MenuThumb = getStringImage(bitmap)

        // todo: See Log
        Log.e(TAG, "ProductType: $ProductType")
        Log.e(TAG, "MenuName: $MenuName")
        Log.e(TAG, "MenuPrice: $MenuPrice")
        Log.e(TAG, "MenuProduct: $MenuProduct")

        Log.e(TAG, "MenuServingSize: $MenuServingSize")
        Log.e(TAG, "MenuPickUpRecommendTime: $MenuPickUpRecommendTime")
        Log.e(TAG, "MenuServingImpossibleTime: $MenuServingImpossibleTime")

        Log.e(TAG, "MenuServingWay: $MenuServingWay")

        // Log.e(TAG, "MenuThumb: $MenuThumb" )

        // todo: Send Result: 서버로 입력한 값 전송하기
        // 입력한 정보를 php POST로 DB에 전송합니다.
        val stringRequest = object: StringRequest(Request.Method.POST, RequestURL, Response.Listener { response ->
            Log.e(TAG, "onResponse: response = $response")

            try
            {
                val jsonObject = JSONObject(response)

                val success = jsonObject.getString("success")

                if (success == "1")
                {
                    Toast.makeText(this@Activity_Create_Menu, "작성 완료", Toast.LENGTH_SHORT).show()

                    Log.e(Fragment_Menu_Management.TAG, "getProjectList(): 메뉴 목록 불러오기")

                    //building retrofit object
                    val retrofit = Retrofit.Builder().baseUrl(ApiClient.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build()

                    //Defining retrofit api service
                    val projectListRequest = retrofit.create(ApiInterface::class.java!!)

                    //defining the call
                    val listCall = projectListRequest.getNestMenuList(SortRequest!!)

                    listCall.enqueue(object: Callback<List<Item_Nest_Menu>>
                    {
                        override fun onResponse(call: Call<List<Item_Nest_Menu>>,
                                                response: retrofit2.Response<List<Item_Nest_Menu>>)
                        {
                            Log.e(
                                    Fragment_Menu_Management.TAG, "list call onResponse = " + response.body()!!
                                 )
                            Log.e(
                                    Fragment_Menu_Management.TAG,
                                    "list call onResponse = " + response.body()!!.toString()
                                 )

                            Fragment_Menu_Management.item_Nest_Menu = response.body() as ArrayList<Item_Nest_Menu>

                            // 넘어온 값 확인하기
                            for (i in Fragment_Menu_Management.item_Nest_Menu.indices)
                            {
                                Log.e(
                                        Fragment_Menu_Management.TAG,
                                        "onResponse: BroadCastTitle: " + Fragment_Menu_Management.item_Nest_Menu.get(i).Menu_Name
                                     )
                            }

                            Fragment_Menu_Management.menu_recycler_view.adapter =
                                Fragment_Menu_Management.Companion.NestMenuAdapter(Fragment_Menu_Management.mContext)
                            Fragment_Menu_Management.menu_recycler_view.setAdapter(Fragment_Menu_Management.menu_recycler_view.adapter)

                            // 작성 완료하면 액티비티 종료
                            finish()
                        }

                        override fun onFailure(call: Call<List<Item_Nest_Menu>>, t: Throwable)
                        {
                            Toast.makeText(
                                    Fragment_Menu_Management.mContext, "리스트 로드 실패", Toast.LENGTH_SHORT
                                          ).show()
                            Log.e(Fragment_Menu_Management.TAG, "onFailure: t: " + t.message)
                        }
                    })
                }
                else
                {
                    Toast.makeText(
                            this@Activity_Create_Menu, "\"message\":\"error\" 문제발생.", Toast.LENGTH_SHORT
                                  ).show()
                }
            }
            catch (e: JSONException)
            {
                e.printStackTrace()
                Toast.makeText(
                        this@Activity_Create_Menu, "JSONException 문제발생.$e", Toast.LENGTH_SHORT
                              ).show()
                Log.e(TAG, "onResponse: JSONException e: $e")
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(this@Activity_Create_Menu, "VolleyError 문제발생.$error", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "onErrorResponse: error: $error")
        })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()

                params.put("ProductType", ProductType.toString())
                params.put("MenuName", MenuName.toString())
                params.put("MenuPrice", MenuPrice.toString())

                params.put("MenuProduct", MenuProduct.toString())
                params.put("MenuServingSize", MenuServingSize.toString())
                params.put("MenuPickUpRecommendTime", MenuPickUpRecommendTime.toString())

                params.put("MenuServingImpossibleTime", MenuServingImpossibleTime.toString())
                params.put("MenuThumb", MenuThumb.toString())
                params.put("PhotoType", PhotoType.toString())

                params.put("MenuServingWay", MenuServingWay.toString())
                Log.e(TAG, "MenuServingWay: $MenuServingWay")


                Log.e(TAG, "PhotoType: $PhotoType")

                // 현재 페이지를 Edit 모드로 접속했다면 서버에 수정 사실 전달하기
                if (Fragment_Menu_Management.intoTheActivityCreateMode.equals("Edit"))
                {
                    params.put("GET_MENU_EDIT_INDEX", GET_MENU_EDIT_INDEX.toString())
                    Log.e(TAG, "수정 요청 RequestURL: $RequestURL")
                }
                else
                {
                    Log.e(TAG, "신규메뉴 생성 요청 RequestURL: $RequestURL")
                }

                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(this@Activity_Create_Menu)
        requestQueue.add(stringRequest) // stringRequest = 바로 위에 회원가입 요청메소드 실행
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        // 이미지 크로퍼에서 선택한 이미지 받기
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK && result != null)
            // requestCode == 1 && /*&& data.getData() != null*/
            {
                // 사진의 경로를 담는다
                val filePath = result.uri
                try
                {
                    // 바로 위 변수인 filePath에 담은 경로로 이미지를 비트맵에 담는다.
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.e(TAG, "onActivityResult: bitmap2: $bitmap")

                    PhotoType = "BitMap"

                    // 이미지 세팅하기
                    create_menu_thumb.setImageBitmap(bitmap)
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }
        }
    }

    // 비트맵을 문자열로 변환하는 메소드
    fun getStringImage(bitmap: Bitmap?): String
    {
        // 바이트 배열 사용.
        val byteArrayOutputStream = ByteArrayOutputStream()

        // 비트맵을 변환한다. 원래 100%였던 것을 50%의 품질로. 그리고 바이트 배열화 시킨다.
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)

        val imageByteArray = byteArrayOutputStream.toByteArray()

        // 베이스 64? 뭔 소린지 모르겠다.
        // 위키를 참고했다. (https://ko.wikipedia.org/wiki/베이스64)
        // 64진법이라고 한다. 64진법으로 인코딩 하는건가?

        // 인코딩 결과를 반환한다.
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT)
    }
}
