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
    lateinit var top_text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exer_youtube)

        video = findViewById(R.id.videoView)
        startbutton = findViewById(R.id.buttonSTART)
        information_text = findViewById(R.id.exer_info)
        top_text = findViewById(R.id.exer_name)

        val intent = intent
        val user_id = intent.getStringExtra("id")
        val exer_name = intent.getStringExtra("exer_name")


        //video?.setVideoURI(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))

        if(exer_name == "exer_one"){
            top_text.text = "사이드 래터럴 레이즈"
            information_text.text = "1. 등을 곧게 펴고 발을 어깨너비로 벌립니다.\n2. 팔을 옆으로 완전히 뻗은 상태에서 손바닥이 몸을 향하게 합니다.\n3. 팔꿈치를 옆구리 가까이에 두고 시작합니다.\n4. 팔을 완전히 펴고 몸통을 고정한 상태에서 어깨 높이까지 옆으로 들어 올린 후 숨을 내쉽니다.\n5. 숨을 들이쉬면서 부드럽게 제어된 움직임으로 시작 위치로 돌아갑니다."

            val VIDEO_PATH =
                "android.resource://" + "org.tensorflow.lite.examples.poseestimation" + "/" + R.raw.exer_one
            var uri: Uri = Uri.parse(VIDEO_PATH)
            video?.setVideoURI(uri)
        }
        else if (exer_name == "exer_two"){
            top_text.text = "측면 다리들기"
            information_text.text = "1. 어깨 너비로 발을 벌리고 가슴을 위로 올리고 복부에 힘을 줍니다. \n2. 무릎을 약간 구부리고 선택한 다리를 옆으로 들어 올리십시오."
            val VIDEO_PATH =
                "android.resource://" + "org.tensorflow.lite.examples.poseestimation" + "/" + R.raw.exer_two
            var uri: Uri = Uri.parse(VIDEO_PATH)
            video?.setVideoURI(uri)
        }
        else{
            top_text.text = "스쿼트"
            information_text.text = "1. 어깨 너비로 발을 벌리고 가슴을 위로 유지하고, 복부에 힘을 줍니다. \n2. 팔을 어깨 높이로 올려줍니다. \n3. 동시에 무릎을 구부리고 의자에 앉듯이 엉덩이를 뒤로 빼십시오. \n4. 허벅지 위쪽이 지면과 평행을 이루면 잠시 멈췄다가, 엉덩이를 앞으로 밀어 시작 위치로 돌아갑니다."
            val VIDEO_PATH =
                "android.resource://" + "org.tensorflow.lite.examples.poseestimation" + "/" + R.raw.exer_three
            var uri: Uri = Uri.parse(VIDEO_PATH)
            video?.setVideoURI(uri)
        }

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