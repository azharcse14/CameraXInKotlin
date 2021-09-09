package com.azhar.photoeditorapplication.View.Utils

import android.Manifest


object Constants {
    const val TAG = "CameraX"
    const val FILE_NAME_FORMET = "yy-mm-dd-hh-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSION = 123
    val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
}