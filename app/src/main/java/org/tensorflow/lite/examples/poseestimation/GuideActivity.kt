package org.tensorflow.lite.examples.poseestimation

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton

class GuideActivity : AppCompatActivity() {

    lateinit var turtle_start_button : ImageButton
    private var mediaPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)



        val intent = intent
        turtle_start_button = findViewById(R.id.turtle_start_button)

        mediaPlayer = MediaPlayer.create(this, R.raw.start2)

        // 측정 시작 버튼
        turtle_start_button.setOnClickListener {
            mediaPlayer?.start()

            val nextIntent = Intent(this@GuideActivity, WaitActivity::class.java)
            startActivity(nextIntent)
            finish()
        }

    }
}