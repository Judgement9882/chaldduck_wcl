package org.tensorflow.lite.examples.poseestimation

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class InsertSetRep : AppCompatActivity() {
    lateinit var exer_set: EditText
    lateinit var exer_rep: EditText
    lateinit var exer_start_button : Button
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_set_rep)

        val intent = intent
        val user_id = intent.getStringExtra("id")
        val exer_name = intent.getStringExtra("exer_name")

        exer_set = findViewById(R.id.exer_set_num)
        exer_rep = findViewById(R.id.exer_rep_num)
        exer_start_button = findViewById(R.id.exer_exit)

        // 운동 시작 버튼
        exer_start_button.setOnClickListener {

            if(mediaPlayer == null){
                mediaPlayer = MediaPlayer.create(this, R.raw.start)
            }
            mediaPlayer?.start()

            val nextIntent = Intent(this@InsertSetRep,WaitActivity::class.java)
            nextIntent.putExtra("id", user_id)
            nextIntent.putExtra("exer_name", exer_name)
            nextIntent.putExtra("exer_set", exer_set.text.toString().toInt())
            nextIntent.putExtra("exer_rep", exer_rep.text.toString().toInt())

            Log.e("exer_set", exer_set.text.toString())
            Log.e("exer_rep", exer_rep.text.toString())

            startActivity(nextIntent)
            finish()
        }
    }
}

//    fun onStart(view: View){
//
//        // 사운드 재생
////        if (mediaPlayer == null) {
////            mediaPlayer = MediaPlayer.create(this, R.raw.start)
////        }
////        mediaPlayer?.start()
//
//        // 운동페이지로 인텐트 이동
//        val nextIntent = Intent(this, InsertSetRep::class.java) // 다음 화면으로 넘어가기 위한 인텐트 객체 생성
//        nextIntent.putExtra("id", user_id)
//        nextIntent.putExtra("exer_name", "exer_three")
//        startActivity(intent)
//    }
