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

    val FLAG_REQ_CAMERA = 101

    private lateinit var  timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)

        mediaPlayer = MediaPlayer.create(this, R.raw.start2)


        val intent = intent
        val user_id = intent.getStringExtra("id")
        val exer_name = intent.getStringExtra("exer_name")
        val exer_set = intent.getIntExtra("exer_set", 3)
        val exer_rep = intent.getIntExtra("exer_rep", 10)

        camera_view = findViewById(R.id.cameraview)
        setPermission() // 카메라 권한 수행

        val tv : TextView = findViewById(R.id.tv)
        timer = object : CountDownTimer(11000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
//                val cam : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cam, FLAG_REQ_CAMERA)
                tv.setText(" "+millisUntilFinished / 1000 + "초 후 운동을 시작합니다.")
            }
            override fun onFinish() {
                // Activity 이동
                tv.setText("잠시후 운동화면으로 넘어갑니다.")

                mediaPlayer?.start()


                // 운동페이지로 인텐트 이동
                val nextIntent = Intent(applicationContext, MainActivity::class.java)
                nextIntent.putExtra("id", user_id)
                nextIntent.putExtra("exer_name", exer_name)
                nextIntent.putExtra("exer_set", exer_set)
                nextIntent.putExtra("exer_rep", exer_rep)
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
        //mediaPlayer?.release()
        mediaPlayer = null
        timer.cancel()
    }

    private fun setPermission(){
        val permission = object : PermissionListener{
            override fun onPermissionGranted() { // 위험권한 허용시 수행
                Toast.makeText(this@WaitActivity, "권한이 허용됐습니다.", Toast.LENGTH_SHORT).show()
            }

            // 위험권한 거부시 수행
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@WaitActivity, "권한이 거부됐습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 권한부여
        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("Exerlearning 앱을 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(android.Manifest.permission.CAMERA)
            .check()
    }

}