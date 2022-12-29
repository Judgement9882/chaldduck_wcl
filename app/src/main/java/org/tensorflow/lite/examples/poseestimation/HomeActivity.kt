package org.tensorflow.lite.examples.poseestimation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HomeActivity : AppCompatActivity() {
    lateinit var user_info: TextView
    lateinit var start_exercise_button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 전달한 id값 저장

        user_info = findViewById(R.id.choose_text)
        start_exercise_button = findViewById(R.id.exer_two)

        user_info.text = "당신은 거북목이 아니었습니다."

        start_exercise_button.setOnClickListener {
            val nextIntent = Intent(this@HomeActivity, GuideActivity::class.java)
            startActivity(nextIntent)
        }
    }
}