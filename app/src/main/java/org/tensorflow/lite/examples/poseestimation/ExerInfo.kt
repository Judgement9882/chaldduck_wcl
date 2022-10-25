package org.tensorflow.lite.examples.poseestimation

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class ExerInfo : AppCompatActivity() {
    // lateinit var exercise_name: EditText
    lateinit var say_hello : TextView
    var exercise_name : String = "side_lateral_raise"
    lateinit var exer_query1 : Button
    lateinit var exer_query2 : Button
    lateinit var exer_query3 : Button
    lateinit var barChart: BarChart
    lateinit var user_id: String

    // bar chart reference
    // https://www.geeksforgeeks.org/android-create-barchart-with-kotlin/

    // on below line we are creating
    // a variable for bar data
    lateinit var barData: BarData

    // on below line we are creating a
    // variable for bar data set
    lateinit var barDataSet: BarDataSet

    // on below line we are creating array list for bar data
    lateinit var barEntriesList: ArrayList<BarEntry>

    // 스피너
//    lateinit var binding : ActivityExerInfoBinding

    // 레트로핏 ============================================
    var gson = GsonBuilder().setLenient().create()

    // 2. 레트로핏 생성
    val retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-13-57-234-0.us-west-1.compute.amazonaws.com:3000")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val service = retrofit.create(ExerService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exer_info)

        // exercise_name = findViewById(R.id.exer_name_R)
        say_hello = findViewById(R.id.exer_result)
        exer_query1 = findViewById(R.id.exer_query1)
        exer_query2 = findViewById(R.id.exer_query2)
        exer_query3 = findViewById(R.id.exer_query3)
        barChart = findViewById(R.id.BarChart)



        // 전달한 id값 저장
        val intent = intent
        user_id = intent.getStringExtra("id").toString()

        say_hello.text = " " + user_id + "님의 운동기록"


        exer_query1.setOnClickListener{
            exercise_name = "side_lateral_raise"
            val exer_id = user_id
            val exer_name = exercise_name
            Log.e("테스트", exer_name)
            service.exer_info(exer_id, exer_name).enqueue(object : Callback<ExerJson> {
                override fun onResponse(call: retrofit2.Call<ExerJson>, response: Response<ExerJson>) {
                    val result = response.body()
                    Log.e("운동정보", "${result}")

                    var temp_str = "test"
                    var temp_str2 = "test2"
                    if(result?.code == "200"){
                        barEntriesList = ArrayList()

//                        val jsonObject = result.exer_OK.get
                        temp_str = result.exer_OK.toString()
                        Log.e("문자열", temp_str.indexOf(',').toString())
                        temp_str2 = temp_str.substring(2, temp_str.length-2)
                        Log.e("문자열", temp_str2)
                        var arr = temp_str2.split(",")

                        for(i in arr){

                            var deleteDoublePoint = i.replace("\"", "")
                            Log.e("자르기", deleteDoublePoint)
                            var arrDict = deleteDoublePoint.split(':')
                            Log.e("자르기", arrDict[0])
                            barEntriesList.add(BarEntry(arrDict[0].toFloat(), arrDict[1].toFloat()))
                            Log.e("ok값 확인",result.exer_OK.javaClass.name)

                        }
//                        barEntriesList.add(BarEntry(1f, 2f))
//                        barEntriesList.add(BarEntry(2f, 5f))
//                        barEntriesList.add(BarEntry(3f, 8f))
//                        barEntriesList.add(BarEntry(4f, 11f))
//                        barEntriesList.add(BarEntry(5f, 6f))
                        startBarChartData()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ExerJson>, t: Throwable) {
                    Log.e("운동정보 실패", "${t.localizedMessage}")
                }
            })
            Log.e("name : ", exercise_name)
        }

        exer_query2.setOnClickListener{
            exercise_name = "standing_side_leg_raise"
            val exer_id = user_id
            val exer_name = exercise_name
            Log.e("테스트", exer_name)
            service.exer_info(exer_id, exer_name).enqueue(object : Callback<ExerJson> {
                override fun onResponse(call: retrofit2.Call<ExerJson>, response: Response<ExerJson>) {
                    val result = response.body()
                    Log.e("운동정보", "${result}")

                    var temp_str = "test"
                    var temp_str2 = "test2"
                    if(result?.code == "200"){
                        barEntriesList = ArrayList()

//                        val jsonObject = result.exer_OK.get
                        temp_str = result.exer_OK.toString()
                        Log.e("문자열", temp_str.indexOf(',').toString())
                        temp_str2 = temp_str.substring(2, temp_str.length-2)
                        Log.e("문자열", temp_str2)
                        var arr = temp_str2.split(",")

                        for(i in arr){

                            var deleteDoublePoint = i.replace("\"", "")
                            Log.e("자르기", deleteDoublePoint)
                            var arrDict = deleteDoublePoint.split(':')
                            Log.e("자르기", arrDict[0])
                            barEntriesList.add(BarEntry(arrDict[0].toFloat(), arrDict[1].toFloat()))
                            Log.e("ok값 확인",result.exer_OK.javaClass.name)

                        }
//                        barEntriesList.add(BarEntry(1f, 2f))
//                        barEntriesList.add(BarEntry(2f, 5f))
//                        barEntriesList.add(BarEntry(3f, 8f))
//                        barEntriesList.add(BarEntry(4f, 11f))
//                        barEntriesList.add(BarEntry(5f, 6f))
                        startBarChartData()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ExerJson>, t: Throwable) {
                    Log.e("운동정보 실패", "${t.localizedMessage}")
                }
            })
            Log.e("name : ", exercise_name)
        }

        exer_query3.setOnClickListener {
            exercise_name = "squat"
            val exer_id = user_id
            val exer_name = exercise_name
            Log.e("테스트", exer_name)
            service.exer_info(exer_id, exer_name).enqueue(object : Callback<ExerJson> {
                override fun onResponse(call: retrofit2.Call<ExerJson>, response: Response<ExerJson>) {
                    val result = response.body()
                    Log.e("운동정보", "${result}")

                    var temp_str = "test"
                    var temp_str2 = "test2"
                    if(result?.code == "200"){
                        barEntriesList = ArrayList()

//                        val jsonObject = result.exer_OK.get
                        temp_str = result.exer_OK.toString()
                        Log.e("문자열", temp_str.indexOf(',').toString())
                        temp_str2 = temp_str.substring(2, temp_str.length-2)
                        Log.e("문자열", temp_str2)
                        var arr = temp_str2.split(",")

                        for(i in arr){

                            var deleteDoublePoint = i.replace("\"", "")
                            Log.e("자르기", deleteDoublePoint)
                            var arrDict = deleteDoublePoint.split(':')
                            Log.e("자르기", arrDict[0])
                            barEntriesList.add(BarEntry(arrDict[0].toFloat(), arrDict[1].toFloat()))
                            Log.e("ok값 확인",result.exer_OK.javaClass.name)

                        }
//                        barEntriesList.add(BarEntry(1f, 2f))
//                        barEntriesList.add(BarEntry(2f, 5f))
//                        barEntriesList.add(BarEntry(3f, 8f))
//                        barEntriesList.add(BarEntry(4f, 11f))
//                        barEntriesList.add(BarEntry(5f, 6f))
                        startBarChartData()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ExerJson>, t: Throwable) {
                    Log.e("운동정보 실패", "${t.localizedMessage}")
                }
            })
            Log.e("name : ", exercise_name)
        }
        // 레트로핏 ============================================

//        exer_query.setOnClickListener{
//
////            startBarChartData()
//            val exer_id = user_id
//            val exer_name = exercise_name
//            Log.e("테스트", exer_name)
//            service.exer_info(exer_id, exer_name).enqueue(object : Callback<ExerJson> {
//                override fun onResponse(call: retrofit2.Call<ExerJson>, response: Response<ExerJson>) {
//                    val result = response.body()
//                    Log.e("운동정보", "${result}")
//
//                    var temp_str = "test"
//                    var temp_str2 = "test2"
//                    if(result?.code == "200"){
//                        barEntriesList = ArrayList()
//
////                        val jsonObject = result.exer_OK.get
//                        temp_str = result.exer_OK.toString()
//                        Log.e("문자열", temp_str.indexOf(',').toString())
//                        temp_str2 = temp_str.substring(2, temp_str.length-2)
//                        Log.e("문자열", temp_str2)
//                        var arr = temp_str2.split(",")
//
//                        for(i in arr){
//
//                            var deleteDoublePoint = i.replace("\"", "")
//                            Log.e("자르기", deleteDoublePoint)
//                            var arrDict = deleteDoublePoint.split(':')
//                            Log.e("자르기", arrDict[0])
//                            barEntriesList.add(BarEntry(arrDict[0].toFloat(), arrDict[1].toFloat()))
//                            Log.e("ok값 확인",result.exer_OK.javaClass.name)
//
//                        }
////                        barEntriesList.add(BarEntry(1f, 2f))
////                        barEntriesList.add(BarEntry(2f, 5f))
////                        barEntriesList.add(BarEntry(3f, 8f))
////                        barEntriesList.add(BarEntry(4f, 11f))
////                        barEntriesList.add(BarEntry(5f, 6f))
//                        startBarChartData()
//                    }
//                }
//
//                override fun onFailure(call: retrofit2.Call<ExerJson>, t: Throwable) {
//                    Log.e("운동정보 실패", "${t.localizedMessage}")
//                }
//            })
//        }

        // 뒤로가기 버튼 구현

        // 스피터
//        binding = ActivityExerInfoBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        val itemList = listOf("사이드 래터럴 레이즈", "스탠딩 사이드 레그 레이즈", "스쿼트")
//        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, itemList)
//        binding.exerSpinner.adapter = adapter
//
//        binding.exerSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if(position != -1) {
//                    Toast.makeText(this@ExerInfo, itemList[position], Toast.LENGTH_SHORT).show()
//                    if(itemList[position] == "사이드 래터럴 레이즈"){
//                        exercise_name = "side_lateral_raise"
//                        exerQuery()
//                        Log.e("name : ", exercise_name)
//                    }
//                    else if(itemList[position] == "스탠딩 사이드 레그 레이즈"){
//                        exercise_name = "standing_side_leg_raise"
//                        exerQuery()
//                        Log.e("name : ", exercise_name)
//                    }
//                    else if(itemList[position] == "스쿼트"){
//                        exercise_name = "squat"
//                        exerQuery()
//                        Log.e("name : ", exercise_name)
//                    }
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//        }

    }

//    private fun exerQuery(){
//        val exer_id = user_id.toString()
//        val exer_name = exercise_name
//        Log.e("테스트", exer_name)
//        service.exer_info(exer_id, exer_name).enqueue(object : Callback<ExerJson> {
//            override fun onResponse(call: retrofit2.Call<ExerJson>, response: Response<ExerJson>) {
//                val result = response.body()
//                Log.e("운동정보", "${result}")
//
//                var temp_str = "test"
//                var temp_str2 = "test2"
//                if(result?.code == "200"){
//                    barEntriesList = ArrayList()
//
////                        val jsonObject = result.exer_OK.get
//                    temp_str = result.exer_OK.toString()
//                    Log.e("문자열", temp_str.indexOf(',').toString())
//                    temp_str2 = temp_str.substring(2, temp_str.length-2)
//                    Log.e("문자열", temp_str2)
//                    var arr = temp_str2.split(",")
//
//                    for(i in arr){
//
//                        var deleteDoublePoint = i.replace("\"", "")
//                        Log.e("자르기", deleteDoublePoint)
//                        var arrDict = deleteDoublePoint.split(':')
//                        Log.e("자르기", arrDict[0])
//                        barEntriesList.add(BarEntry(arrDict[0].toFloat(), arrDict[1].toFloat()))
//                        Log.e("ok값 확인",result.exer_OK.javaClass.name)
//
//                    }
////                        barEntriesList.add(BarEntry(1f, 2f))
////                        barEntriesList.add(BarEntry(2f, 5f))
////                        barEntriesList.add(BarEntry(3f, 8f))
////                        barEntriesList.add(BarEntry(4f, 11f))
////                        barEntriesList.add(BarEntry(5f, 6f))
//
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<ExerJson>, t: Throwable) {
//                Log.e("운동정보 실패", "${t.localizedMessage}")
//            }
//        })
//    }

    // 가장 최근 진행했던 운동 5회에 대한 정보 제공
    private fun startBarChartData() {

        // on below line we are initializing our bar data set
        barDataSet = BarDataSet(barEntriesList, "Bar Chart Data")

        // on below line we are initializing our bar data
        barData = BarData(barDataSet)

        // on below line we are setting data to our bar chart
        barChart.data = barData

        // on below line we are setting colors for our bar chart text
        barDataSet.valueTextColor = Color.BLACK

        // on below line we are setting color for our bar data set
        barDataSet.setColor(resources.getColor(R.color.purple_500))

        // on below line we are setting text size
        barDataSet.valueTextSize = 16f

        // on below line we are enabling description
        barChart.description.isEnabled = false
    }
}

interface ExerService{
    @FormUrlEncoded
    @POST("/process/exer/response")
    fun exer_info(
        @Field("exer_id") exer_id:String,
        @Field("exer_name") exer_name:String)
            : retrofit2.Call<ExerJson>
}