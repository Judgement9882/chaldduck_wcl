/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.*

import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import java.lang.Math.abs
import java.lang.Math.atan2
import kotlin.math.PI
import kotlin.math.roundToInt

import org.tensorflow.lite.examples.poseestimation.databinding.ActivityMainBinding
import android.os.SystemClock
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

//    9.5 타이머 설정
    private var mBinding: ActivityMainBinding?= null
    private val binding get() = mBinding!!
//////////////////////////////////////////////////////////

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     * 1 == MoveNet Thunder model
     * 2 == MoveNet MultiPose model
     * 3 == PoseNet model
     **/
    private var exer_id = ""
    private var exer_name = ""
    private var modelPos = 1
    private var exer_angle = 1.0
    private var exer_set = 0
    private var exer_count = 0
    private var exer_flag = 0
    private var comp_set = 0
    private var comp_rep = 0


    /** Default device is CPU */
    private var device = Device.CPU

    private var BodyPartVar = listOf(
        Pair(BodyPart.LEFT_ANKLE, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.RIGHT_ANKLE, BodyPart.RIGHT_ELBOW)
    )

    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var spnDevice: Spinner
    private lateinit var spnModel: Spinner
    private lateinit var spnTracker: Spinner
    private lateinit var vTrackerOption: View
    private lateinit var tvClassificationValue1: TextView
    private lateinit var tvClassificationValue2: TextView
    private lateinit var tvClassificationValue3: TextView
    private lateinit var swClassification: SwitchCompat
    private lateinit var vClassificationOption: View
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false

    // 추가 변수===============================================
    private lateinit var setLay : TextView
    private lateinit var countLay : TextView
    private lateinit var percentLay : TextView
    // 11.14 미디어
    private var mediaPlayer: MediaPlayer? = null
//    var cam_dir = 0
    var StartAngle = 30
    var StopAngle = 70
    // 추가 변수===============================================
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, FRAGMENT_DIALOG)
            }
        }
    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var changeTrackerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeTracker(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var setClassificationListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            showClassificationResult(isChecked)
            isClassifyPose = isChecked
            isPoseClassifier()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 10-18 합치기
        val intent = intent
        exer_id = intent.getStringExtra("id").toString()
        exer_name = intent.getStringExtra("exer_name").toString()
        comp_set = intent.getIntExtra("exer_set", 3)
        comp_rep = intent.getIntExtra("exer_rep", 10)
        Log.e("exer_set", comp_set.toString())
        Log.e("exer_rep", comp_rep.toString())

        // 22.11.14 운동 추가

        if(exer_name == "exer_one"){
            CameraSource.JointBody = listOf(Triple(BodyPart.RIGHT_WRIST, BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP))
            Log.e("exer : " , "1")
            StartAngle = 30
            StopAngle = 70
        }
        else if (exer_name == "exer_two"){
            CameraSource.JointBody = listOf(Triple(BodyPart.LEFT_ANKLE, BodyPart.LEFT_HIP, BodyPart.RIGHT_KNEE))
            Log.e("exer : " , "2")
            StartAngle = 30
            StopAngle = 70
        }
        else {
            CameraSource.JointBody = listOf(Triple(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_HIP, BodyPart.RIGHT_SHOULDER))
            Log.e("exer : " , "3")
            StartAngle = 150
            StopAngle = 80
        }


        // 220606 button listener
//        val btn_event = findViewById<Button>(R.id.button_retry)
//        val switch_event = findViewById<ImageButton>(R.id.camera_change)

        // 9.1 camera_switch =======================================
//        switch_event.setOnClickListener{switchCamera()}
//        switch_event.setOnClickListener {
//            if (cam_dir == 0) {
//                cameraSource?.switchCamera(cam_dir)
//                cam_dir = 1
//            } else {
//                cameraSource?.switchCamera(cam_dir)
//                cam_dir = 0
//            }
//        }
//
//                cameraSource?.cameraId = "1"
//                cameraSource?.camera?.close()
//                Log.d("device : ", cameraSource!!.cameraId)
//                openCamera()
//        }
//        else{
//                cameraSource?.cameraId = "0"
//                cameraSource?.camera?.close()
//                Log.d("device : ", cameraSource!!.cameraId)
//                openCamera()
//            }

        // 9.1 camera_switch =======================================

//        9.5 Timer

        //바인딩 초기화
        mBinding = ActivityMainBinding.inflate(layoutInflater)

        // 생성된 뷰 액티비티에 표시시
        setContentView(binding.root)

        // elapsedRealtime: 부팅 이후의 밀리초를 리턴 (절전 모드에서 보낸 시간 포함)
        // 사용자가 현재시간을 수정해도 영향 받지 않음
//        binding.startBtn.setOnClickListener {
//            binding.chronometer.base = SystemClock.elapsedRealtime()
//            binding.chronometer.start()
//
//            //버튼 표시 여부 조정
//            binding.buttonRetry.isEnabled = true
//            binding.startBtn.isEnabled = true
//        }
//
//        binding.buttonRetry.setOnClickListener {
//            binding.chronometer.base = SystemClock.elapsedRealtime()
//            binding.chronometer.stop()
//
//            //버튼 표시 여부 조정
//            binding.buttonRetry.isEnabled = true
//            binding.startBtn.isEnabled = true
//
//            exer_count = 0
////            exer_set = 0
//        }


//        btn_event.setOnClickListener{
//            exer_count = 0
//            exer_set = 0
//        }


        tvScore = findViewById(R.id.tvScore)
        tvFPS = findViewById(R.id.tvFps)
        spnModel = findViewById(R.id.spnModel)
        spnDevice = findViewById(R.id.spnDevice)
        spnTracker = findViewById(R.id.spnTracker)
        vTrackerOption = findViewById(R.id.vTrackerOption)
        surfaceView = findViewById(R.id.surfaceView)
        tvClassificationValue1 = findViewById(R.id.tvClassificationValue1)
        tvClassificationValue2 = findViewById(R.id.tvClassificationValue2)
        tvClassificationValue3 = findViewById(R.id.tvClassificationValue3)
        swClassification = findViewById(R.id.swPoseClassification)
        vClassificationOption = findViewById(R.id.vClassificationOption)
        countLay = findViewById(R.id.countLayout)
        setLay = findViewById(R.id.setLayout)
        percentLay = findViewById(R.id.percent)
        initSpinner()

        spnModel.setSelection(modelPos)
        swClassification.setOnCheckedChangeListener(setClassificationListener)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
    }

    // 9.1 camera_switch =======================================
//    private fun switchCamera(){
//        if(cameraSource?.cameraId == "0"){
//
//                cameraSource?.cameraId = "1"
//                cameraSource?.camera?.close()
//                Log.d("device : ", cameraSource!!.cameraId)
//                openCamera()
//        }
//        else{
//                cameraSource?.cameraId = "0"
//                cameraSource?.camera?.close()
//                Log.d("device : ", cameraSource!!.cameraId)
//                openCamera()
//            }
//
//    }
    // 9.1 camera_switch =======================================

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                        }

                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {

                            Log.d("test : ", calculateAngle(CameraSource.firstX, CameraSource.firstY,
                                CameraSource.secondX, CameraSource.secondY, CameraSource.thirdX, CameraSource.thirdY).toString())
                            exer_angle = calculateAngle(CameraSource.firstX, CameraSource.firstY, CameraSource.secondX,
                                CameraSource.secondY, CameraSource.thirdX, CameraSource.thirdY)

                            // 22.11.14 운동 종류에 따른 변수 변화
                            if(exer_name == "exer_one" || exer_name == "exer_two"){
                                if ((exer_angle > StopAngle) and (exer_flag == 0)) {
                                    exer_count++

                                    if(exer_count % comp_rep != 0){
                                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.count_music)
                                        mediaPlayer?.start()
                                    }


                                    if(exer_count % comp_rep == 0){
                                        exer_set++

                                        if(comp_set != exer_set){
                                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.set_music)
                                            mediaPlayer?.start()
                                        }


                                        // 목표 횟수 도달 => ExerResult로 넘어감
                                        if(comp_set == exer_set){

                                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.exit)
                                            mediaPlayer?.start()


                                            val nextIntent = Intent(this@MainActivity,ExerResult::class.java)
                                            nextIntent.putExtra("id", exer_id)
                                            nextIntent.putExtra("exer_name", exer_name)
                                            nextIntent.putExtra("exer_set", comp_set)
                                            nextIntent.putExtra("exer_rep", comp_rep)
                                            startActivity(nextIntent)

                                            finish()
                                        }

                                        exer_count%=comp_rep
                                    }
                                    exer_flag = 1
                                }
                                else if ((exer_angle < StartAngle) and (exer_flag==1)){
                                    exer_flag = 0
                                }
                            }
                            // 스쿼트일 경우
                            else{
                                if ((exer_angle < StopAngle) and (exer_flag == 0)) {
                                    exer_count++
                                    if(exer_count % comp_rep != 0){
                                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.count_music)
                                        mediaPlayer?.start()
                                    }

                                    if(exer_count % comp_rep == 0){
                                        exer_set++

                                        if(comp_set != exer_set){
                                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.set_music)
                                            mediaPlayer?.start()
                                        }



                                        // 목표 횟수 도달 => ExerResult로 넘어감
                                        if(comp_set == exer_set){

                                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.exit)
                                            mediaPlayer?.start()

                                            val nextIntent = Intent(this@MainActivity,ExerResult::class.java)
                                            nextIntent.putExtra("id", exer_id)
                                            nextIntent.putExtra("exer_name", exer_name)
                                            nextIntent.putExtra("exer_set", comp_set)
                                            nextIntent.putExtra("exer_rep", comp_rep)
                                            startActivity(nextIntent)

                                            finish()
                                        }

                                        exer_count%=comp_rep
                                    }
                                    exer_flag = 1
                                }
                                else if ((exer_angle > StartAngle) and (exer_flag==1)){
                                    exer_flag = 0
                                }
                            }

                            countLay.text = "Count : " + exer_count.toString()
                            setLay.text = "Set : " + exer_set.toString()

                            // 22.06.22 강준영
                            // 각도에 따른 운동 진행도와 그에 따른 텍스트 색깔 변화 ===========================

                            // 22.11.14
                            // 운동 종류에 따른 각도 로직 변경
                            if(exer_name == "exer_one" || exer_name == "exer_two"){
                                if (exer_angle < StartAngle) percentLay.text ="0%"
                                else if(exer_angle > StopAngle) percentLay.text ="100%"
                                else {
                                    percentLay.text = ((100*(exer_angle-StartAngle)/(StopAngle - StartAngle)).roundToInt()).toString() + "%"
                                    if ( 100*(exer_angle-StartAngle)/(StopAngle - StartAngle) < 30) {
                                        percentLay.setTextColor(Color.RED)
                                        VisualizationUtils.SkeletonLineColor = Color.RED
                                    }
                                    else if (100*(exer_angle-StartAngle)/(StopAngle - StartAngle) > 65) {
                                        percentLay.setTextColor(Color.GREEN)
                                        VisualizationUtils.SkeletonLineColor = Color.GREEN
                                    }
                                    else {
                                        percentLay.setTextColor(Color.YELLOW)
                                        VisualizationUtils.SkeletonLineColor = Color.YELLOW
                                    }
                                }
                            }
                            // 스쿼트 일 경우
                            else{
                                if (exer_angle > StartAngle) percentLay.text ="0%"
                                else if(exer_angle < StopAngle) percentLay.text ="100%"
                                else {
                                    percentLay.text = ((100 - 100*(exer_angle-StopAngle)/(StartAngle - StopAngle)).roundToInt()).toString() + "%"
                                    if (100 - 100*(exer_angle-StopAngle)/(StartAngle - StopAngle) < 30) {
                                        percentLay.setTextColor(Color.RED)
                                        VisualizationUtils.SkeletonLineColor = Color.RED
                                    }
                                    else if (100 - 100*(exer_angle-StopAngle)/(StartAngle - StopAngle) > 65) {
                                        percentLay.setTextColor(Color.GREEN)
                                        VisualizationUtils.SkeletonLineColor = Color.GREEN
                                    }
                                    else {
                                        percentLay.setTextColor(Color.YELLOW)
                                        VisualizationUtils.SkeletonLineColor = Color.YELLOW
                                    }
                                }
                            }
                            // ========================================================================

                            poseLabels?.sortedByDescending { it.second }?.let {
                                tvClassificationValue1.text = getString(
                                    R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                )
                                tvClassificationValue2.text = getString(
                                    R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 2) it[1] else null)
                                )
                                tvClassificationValue3.text = getString(
                                    R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 3) it[2] else null)
                                )
                            }
                        }

                    }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun calculateAngle(ax : Double, ay : Double, bx : Double, by : Double, cx : Double, cy : Double): Double{

        var radians = atan2(cy-by, cx-bx) - atan2(ay-by, ax-bx)
        var angle = abs(radians*180.0 / PI)

        if (angle > 180.0) angle = 360-angle

        return angle
    }


    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
    }

    // Initialize spinners to let user select model/accelerator/tracker.
    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_models_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnModel.adapter = adapter
            spnModel.onItemSelectedListener = changeModelListener
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
        ).also { adaper ->
            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnDevice.adapter = adaper
            spnDevice.onItemSelectedListener = changeDeviceListener
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_tracker_array, android.R.layout.simple_spinner_item
        ).also { adaper ->
            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnTracker.adapter = adaper
            spnTracker.onItemSelectedListener = changeTrackerListener
        }
    }

    // Change model when app is running
    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    // Change device (accelerator) type when app is running
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    // 내가넣은코드
    // 내가넣은코드

    // Change tracker for Movenet MultiPose model
    private fun changeTracker(position: Int) {
        cameraSource?.setTracker(
            when (position) {
                1 -> TrackerType.BOUNDING_BOX
                2 -> TrackerType.KEYPOINTS
                else -> TrackerType.OFF
            }
        )
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                MoveNet.create(this, device, ModelType.Thunder)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                MoveNet.create(this, device, ModelType.Lightning)
            }
            2 -> {
                // MoveNet (Lightning) MultiPose
                showPoseClassifier(false)
                showDetectionScore(false)
                // Movenet MultiPose Dynamic does not support GPUDelegate
                if (device == Device.GPU) {
                    showToast(getString(R.string.tfe_pe_gpu_error))
                }
                showTracker(true)
                MoveNetMultiPose.create(
                    this,
                    device,
                    Type.Dynamic
                )
            }
            3 -> {
                // PoseNet (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                PoseNet.create(this, device)
            }
            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    // Show/hide the pose classification option.
    private fun showPoseClassifier(isVisible: Boolean) {
        vClassificationOption.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (!isVisible) {
            swClassification.isChecked = false
        }
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Show/hide classification result.
    private fun showClassificationResult(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        tvClassificationValue1.visibility = visibility
        tvClassificationValue2.visibility = visibility
        tvClassificationValue3.visibility = visibility
    }

    // Show/hide the tracking options.
    private fun showTracker(isVisible: Boolean) {
        if (isVisible) {
            // Show tracker options and enable Bounding Box tracker.
            vTrackerOption.visibility = View.VISIBLE
            spnTracker.setSelection(1)
        } else {
            // Set tracker type to off and hide tracker option.
            vTrackerOption.visibility = View.GONE
            spnTracker.setSelection(0)
        }
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }

    //private fun calculate_angle(a, b, c)



}
