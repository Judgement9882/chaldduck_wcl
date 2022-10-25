package org.tensorflow.lite.examples.poseestimation

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import org.w3c.dom.Text

class ExerYoutube : AppCompatActivity() {

    var video: VideoView? = null
    private var mediaPlayer: MediaPlayer? = null
    lateinit var startbutton: Button
    lateinit var information_text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exer_youtube)

        video = findViewById(R.id.videoView)
        startbutton = findViewById(R.id.buttonSTART)
        information_text = findViewById(R.id.exer_info)

        val intent = intent
        val user_id = intent.getStringExtra("id")
        val exer_name = intent.getStringExtra("exer_name")

        information_text.text = "1. 어깨 너비로 발을 벌리고 가슴을 위로 올리고 복부에 힘을 줍니다. 2. 무릎을 약간 구부리고 선택한 다리를 옆으로 들어 올리십시오."



        //video?.setVideoURI(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))


        val VIDEO_PATH =
            "android.resource://" + "org.tensorflow.lite.examples.poseestimation" + "/" + R.raw.sample2
        var uri: Uri = Uri.parse(VIDEO_PATH)
        video?.setVideoURI(uri)

        startbutton.setOnClickListener {
            val nextIntent =
                Intent(this@ExerYoutube, InsertSetRep::class.java) // 다음 화면으로 넘어가기 위한 인텐트 객체 생성
            nextIntent.putExtra("id", user_id)
            nextIntent.putExtra("exer_name", exer_name)
            startActivity(nextIntent)
            finish()
        }
    }

    fun onPlay(view: View) {
        // Play button click!
        video?.start()
    }

    fun onStop(view: View) {
        // Stop button click!
        video?.pause()
    }

}