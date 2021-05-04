package com.example.meals_schdueler

import android.app.Activity
import android.content.Context
import android.content.Intent

interface CameraInterface {

    fun onCaptureImageResult(data: Intent);
    fun onSelectFromHalleryResult(data: Intent?)
    fun getActivityy(): Activity?
    fun getContextt(): Context?

}