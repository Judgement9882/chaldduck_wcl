package org.tensorflow.lite.examples.poseestimation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import android.media.MediaPlayer



class WaitActivity : AppCompatActivity() {
    lateinit var camera_view: ImageView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var  timer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)

        mediaPlayer = MediaPlayer.create(this@WaitActivity, R.raw.start)
        val intent = intent

        camera_view = findViewById(R.id.cameraview)

        val tv : TextView = findViewById(R.id.tv)
        timer = object : CountDownTimer(11000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tv.setText(" "+millisUntilFinished / 1000 + "초 후 측정을 시작합니다. ")
            }
            override fun onFinish() {
                // Activity 이동
                tv.setText("잠시후 측정화면으로 넘어갑니다.")

                mediaPlayer?.start()


                // 측정 페이지로 인텐트 이동
                val nextIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(nextIntent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer = null
        timer.cancel()
    }

}