package com.azhar.photoeditorapplication.View

import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.azhar.photoeditorapplication.R
import com.azhar.photoeditorapplication.View.Utils.Constants
import kotlinx.android.synthetic.main.activity_camera_xactivity.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CameraXActivity : AppCompatActivity() {

    private var imageCapture:ImageCapture?= null
    private lateinit var outputDirectory:File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_xactivity)
        
        outputDirectory = getOutputDirectory()

        if (allPermissionGranted()) {
            startCamera()
//            Toast.makeText(this, "We have permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                Constants.REQUIRED_PERMISSION,
                Constants.REQUEST_CODE_PERMISSION
            )
        }

        imageCaptureBtn.setOnClickListener{
            takePhoto()
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile,resources.getString(R.string.app_name)).apply {
                mkdir()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun takePhoto() {
        val imageCapture = imageCapture?:return
        val photoFile = File(
            outputDirectory,SimpleDateFormat(Constants.FILE_NAME_FORMET, Locale.getDefault()).format(System.currentTimeMillis())+".jpg"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "photo Saved"

                    Toast.makeText(this@CameraXActivity, "$msg $savedUri", Toast.LENGTH_LONG).show()

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG, "onError:${exception.message}", exception)
                }

            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSION){
            if (allPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(this, "Permission not granted by the user.", Toast.LENGTH_SHORT).show()

                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraproviderFuture = ProcessCameraProvider.getInstance(this)

        cameraproviderFuture.addListener({
            val cameraProvider:ProcessCameraProvider = cameraproviderFuture.get()
            val preview = Preview.Builder().build().also { mPreview ->

                mPreview.setSurfaceProvider(
                    viewFinder.surfaceProvider

                )

            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture)

            }catch (e:Exception){
                Log.d(Constants.TAG,"startCamera Fail:",e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionGranted() = Constants.REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}