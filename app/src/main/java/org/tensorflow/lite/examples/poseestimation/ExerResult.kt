package org.tensorflow.lite.examples.poseestimation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class ExerResult : AppCompatActivity() {

    lateinit var text_kind: TextView
    lateinit var text_set: TextView
    lateinit var text_rep: TextView
    lateinit var exer_exit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exer_result)


        // 전달한 id값 저장
        val intent = intent
        val user_id = intent.getStringExtra("id")
        var exer_name = intent.getStringExtra("exer_name")
        val exer_set = intent.getIntExtra("exer_set", 3)
        val exer_rep = intent.getIntExtra("exer_rep", 10)

        text_kind = findViewById(R.id.text_kind)
        text_set = findViewById(R.id.text_set)
        text_rep = findViewById(R.id.text_rep)

        exer_exit = findViewById(R.id.exer_exit)

        // 운동 종류
        if(exer_name == "exer_one"){
            text_kind.text = "운동 종류 : 사이드 래터럴 레이즈"
            exer_name = "side_lateral_raise"
        }
        else if(exer_name == "exer_two"){
            text_kind.text = "운동 종류 : 측면 다리들기"
            exer_name = "standing_side_leg_raise"
        }
        else{
            text_kind.text = "운동 종류 : 스쿼트"
            exer_name = "squat"
        }

        // 운동 세트
        text_set.setText("세트 : "+ exer_set.toString())

        // 운동 총 횟수
        text_rep.setText("운동 총 횟수 : "+ (exer_set * exer_rep).toString())

        var gson = GsonBuilder().setLenient().create()

        // 2. 레트로핏 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-13-57-234-0.us-west-1.compute.amazonaws.com:3000")
            .addConverterFactory(GsonConverterFactory.create(gson))
            // https://stackoverflow.com/questions/42386250/android-retrofit-2-0-json-document-was-not-fully-consumed
            // JSON Document was not fully consumed 오류 해결방법
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val service = retrofit.create(exerRequestService::class.java)

        // 운동 종료를 누르면 서버에 운동정보를 송신함.
        exer_exit.setOnClickListener{
            val idStr = user_id.toString()
            val exerName = exer_name
            val exerSet = exer_set.toString()
            val exerOKCount = exer_rep.toString()
            val exerNOKCount = "0"

            service.signup(idStr, exerName, exerSet, exerOKCount, exerNOKCount).enqueue(object : Callback<ExerJson> {
                override fun onResponse(call: retrofit2.Call<ExerJson>, response: Response<ExerJson>) {
                    val result = response.body()
                    Log.e("운동보내기1", "${result}")
                    // ok라면
                    if(result?.code == "200"){
                        finish()
                    }

                }

                override fun onFailure(call: retrofit2.Call<ExerJson>, t: Throwable) {
                    Log.e("운동보내기2", "${t.localizedMessage}")
                }
            })
        }

    }
}

interface exerRequestService{
    @FormUrlEncoded
    @POST("/process/exer/request")
    fun signup(@Field("exer_id") exer_id:String,
               @Field("exer_name") exer_name:String,
               @Field("exer_set") exer_set:String,
               @Field("exer_OK_count") exer_OK_count:String,
               @Field("exer_NOK_count") exer_NOK_count:String) : retrofit2.Call<ExerJson>

}